package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.MobPacket;
import kinoko.packet.field.NpcPacket;
import kinoko.packet.user.*;
import kinoko.packet.world.WvsContext;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.social.party.TownPortal;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class UserPool extends FieldObjectPool<User> {
    private final ConcurrentHashMap<User, TownPortal> townPortals = new ConcurrentHashMap<>();

    public UserPool(Field field) {
        super(field);
    }

    public synchronized void addUser(User user) {
        // Update client with existing users in pool
        forEach((existingUser) -> {
            try (var locked = existingUser.acquire()) {
                user.write(UserPacket.userEnterField(locked.get()));
            }
        });

        // Add user to pool
        addObject(user);
        broadcastPacket(UserPacket.userEnterField(user), user);

        // Add user pets
        for (Pet pet : user.getPets()) {
            pet.setX(user.getX());
            pet.setY(user.getY());
            pet.setFoothold(user.getFoothold());
            broadcastPacket(PetPacket.petActivated(user, pet));
        }

        // Add user summoned
        for (Summoned summoned : user.getSummoned().values()) {
            summoned.setX(user.getX());
            summoned.setY(user.getY());
            summoned.setFoothold(user.getFoothold());
            broadcastPacket(SummonedPacket.summonedEnterField(user, summoned));
        }

        // Add town portals
        townPortals.forEach((owner, townPortal) -> {
            if (user.getCharacterId() == owner.getCharacterId() ||
                    (user.getPartyId() != 0 && user.getPartyId() == owner.getPartyId())) {
                user.write(FieldPacket.townPortalCreated(user, townPortal, false));
            }
        });

        // Update party
        forEachPartyMember(user, (member) -> {
            try (var lockedMember = member.acquire()) {
                user.write(UserRemote.receiveHp(lockedMember.get()));
                lockedMember.get().write(UserRemote.receiveHp(user));
            }
        });

        // Handle field objects
        field.getMobPool().forEach((mob) -> {
            user.write(MobPacket.mobEnterField(mob));
            if (mob.getController() == null) {
                mob.setController(user);
                user.write(MobPacket.mobChangeController(mob, true));
            } else {
                user.write(MobPacket.mobChangeController(mob, false));
            }
        });
        field.getNpcPool().forEach((npc) -> {
            user.write(NpcPacket.npcEnterField(npc));
            if (npc.getController() == null) {
                npc.setController(user);
                user.write(NpcPacket.npcChangeController(npc, true));
            } else {
                user.write(NpcPacket.npcChangeController(npc, false));
            }
        });
        field.getReactorPool().forEach((reactor) -> {
            user.write(FieldPacket.reactorEnterField(reactor));
        });
        field.getDropPool().forEach((drop) -> {
            user.write(FieldPacket.dropEnterField(drop, DropEnterType.ON_THE_FOOTHOLD));
        });
    }

    public synchronized boolean removeUser(User user) {
        if (!removeObject(user)) {
            return false;
        }
        broadcastPacket(UserPacket.userLeaveField(user), user);

        // Handle controller change
        field.getMobPool().forEach((mob) -> {
            if (mob.getController() == user) {
                assignController(mob);
            }
        });
        field.getNpcPool().forEach((mob) -> {
            if (mob.getController() == user) {
                assignController(mob);
            }
        });
        return true;
    }

    public void updateUsers(Instant now) {
        for (User user : getObjects()) {
            try (var locked = user.acquire()) {
                // Expire temporary stat
                final Set<CharacterTemporaryStat> resetStats = locked.get().getSecondaryStat().expireTemporaryStat(now);
                if (!resetStats.isEmpty()) {
                    user.validateStat();
                    user.write(WvsContext.temporaryStatReset(resetStats));
                    broadcastPacket(UserRemote.temporaryStatReset(user, resetStats), user);
                }
                // Expire skill cooltimes
                final Set<Integer> resetCooltimes = locked.get().getSkillManager().expireSkillCooltime(now);
                for (int skillId : resetCooltimes) {
                    user.write(UserLocal.skillCooltimeSet(skillId, 0));
                }
                // Expire summoned
                final var summonedIter = locked.get().getSummoned().entrySet().iterator();
                while (summonedIter.hasNext()) {
                    final Map.Entry<Integer, Summoned> entry = summonedIter.next();
                    final Summoned summoned = entry.getValue();
                    if (now.isBefore(summoned.getExpireTime())) {
                        continue;
                    }
                    summonedIter.remove();
                    broadcastPacket(SummonedPacket.summonedLeaveField(user, summoned));
                }
            }
        }
        // Expire town portals
        final var townPortalIter = townPortals.entrySet().iterator();
        while (townPortalIter.hasNext()) {
            final Map.Entry<User, TownPortal> entry = townPortalIter.next();
            final User owner = entry.getKey();
            final TownPortal townPortal = entry.getValue();
            if (now.isBefore(townPortal.getExpireTime())) {
                continue;
            }
            townPortalIter.remove();
            notifyRemoveTownPortal(owner);
        }
    }


    // CONTROLLER METHODS ----------------------------------------------------------------------------------------------

    public void assignController(ControlledObject controlled) {
        final Optional<User> controllerResult = controlled.getNearestObject(getObjects()); // closest user to controlled object
        if (controllerResult.isEmpty()) {
            controlled.setController(null);
            return;
        }
        setController(controlled, controllerResult.get());
    }

    private void setController(ControlledObject controlled, User controller) {
        controlled.setController(controller);
        controller.write(controlled.changeControllerPacket(true));
        broadcastPacket(controlled.changeControllerPacket(false), controller);
    }


    // TOWN PORTAL METHODS ---------------------------------------------------------------------------------------------

    public void addTownPortal(User user, TownPortal townPortal) {
        townPortals.put(user, townPortal);
        user.setTownPortal(townPortal);
        user.write(WvsContext.townPortal(townPortal));
        if (user.getPartyId() != 0) {
            forEachPartyMember(user, (member) -> {
                member.write(FieldPacket.townPortalCreated(user, townPortal, true));
            });
            user.getConnectedServer().submitPartyUpdate(user, townPortal);
        }
    }

    public void removeTownPortal(User user) {
        if (townPortals.remove(user) != null) {
            notifyRemoveTownPortal(user);
        }
    }

    public void notifyRemoveTownPortal(User user) {
        user.setTownPortal(null);
        user.write(WvsContext.townPortal(TownPortal.EMPTY_PORTAL));
        if (user.getPartyId() != 0) {
            forEachPartyMember(user, (member) -> {
                member.write(FieldPacket.townPortalRemoved(user));
            });
            user.getConnectedServer().submitPartyUpdate(user, TownPortal.EMPTY_PORTAL);
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void broadcastPacket(OutPacket outPacket) {
        broadcastPacket(outPacket, null);
    }

    public void broadcastPacket(OutPacket outPacket, User except) {
        forEach((user) -> {
            if (except != null && user.getCharacterId() == except.getCharacterId()) {
                return;
            }
            user.write(outPacket);
        });
    }

    public void forEachPartyMember(User user, Consumer<User> consumer) {
        final int partyId = user.getPartyId();
        if (partyId != 0) {
            forEach((member) -> {
                if (member.getCharacterId() != user.getCharacterId() && member.getPartyId() == partyId) {
                    consumer.accept(member);
                }
            });
        }
    }
}
