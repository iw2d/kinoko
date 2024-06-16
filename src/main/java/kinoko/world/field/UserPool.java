package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.MobPacket;
import kinoko.packet.field.NpcPacket;
import kinoko.packet.user.*;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.map.PortalInfo;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.Pet;
import kinoko.world.user.User;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class UserPool extends FieldObjectPool<User> {
    public UserPool(Field field) {
        super(field);
    }

    public synchronized void addUser(User user) {
        // Update client with existing users in pool
        forEach((existingUser) -> {
            try (var locked = existingUser.acquire()) {
                user.write(UserPacket.userEnterField(locked.get()));
                for (Pet pet : existingUser.getPets()) {
                    user.write(PetPacket.petActivated(existingUser, pet));
                }
                for (Summoned summoned : existingUser.getSummoned().values()) {
                    user.write(SummonedPacket.summonedEnterField(existingUser, summoned));
                }
            }
        });

        // Add user to pool
        addObject(user);
        broadcastPacket(UserPacket.userEnterField(user), user);

        // Add user pets
        for (Pet pet : user.getPets()) {
            pet.setPosition(field, user.getX(), user.getY());
            broadcastPacket(PetPacket.petActivated(user, pet));
        }

        // Add user summoned
        for (Summoned summoned : user.getSummoned().values()) {
            summoned.setPosition(field, user.getX(), user.getY());
            broadcastPacket(SummonedPacket.summonedEnterField(user, summoned));
        }

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
            user.write(FieldPacket.dropEnterField(drop, DropEnterType.ON_THE_FOOTHOLD, 0));
        });
        field.getTownPortalPool().forEach((townPortal) -> {
            final User owner = townPortal.getOwner();
            if ((owner.getCharacterId() == user.getCharacterId() && owner.getPartyId() == 0) ||
                    (owner.getPartyId() != 0 && owner.getPartyId() == user.getPartyId())) {
                if (townPortal.getTownField() == field) {
                    final Optional<PortalInfo> portalPointResult = townPortal.getTownPortalPoint();
                    if (portalPointResult.isPresent()) {
                        final PortalInfo portalPoint = portalPointResult.get();
                        user.write(FieldPacket.townPortalCreated(owner.getCharacterId(), portalPoint.getX(), portalPoint.getY(), false));
                    }
                } else {
                    user.write(FieldPacket.townPortalCreated(owner.getCharacterId(), townPortal.getX(), townPortal.getY(), false));
                }
            }
        });
        field.getAffectedAreaPool().forEach((affectedArea) -> {
            user.write(FieldPacket.affectedAreaCreated(affectedArea));
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

        // Remove summoned
        final var iter = user.getSummoned().values().iterator();
        while (iter.hasNext()) {
            final Summoned summoned = iter.next();
            if (!summoned.canMigrate()) {
                broadcastPacket(SummonedPacket.summonedLeaveField(user, summoned));
                iter.remove();
            }
        }

        // Remove affected areas
        field.getAffectedAreaPool().removeByOwnerId(user.getCharacterId());
        return true;
    }

    public void updateUsers(Instant now) {
        for (User user : getObjects()) {
            try (var locked = user.acquire()) {
                // Handle CTS updates on tick
                SkillProcessor.processUpdate(locked, now);
                // Expire temporary stat
                user.resetTemporaryStat((cts, option) -> now.isAfter(option.getExpireTime()));
                // Expire skill cooltimes
                final Set<Integer> resetCooltimes = user.getSkillManager().expireSkillCooltime(now);
                for (int skillId : resetCooltimes) {
                    user.write(UserLocal.skillCooltimeSet(skillId, 0));
                }
                // Expire summoned
                final var summonedIter = user.getSummoned().values().iterator();
                while (summonedIter.hasNext()) {
                    final Summoned summoned = summonedIter.next();
                    if (now.isBefore(summoned.getExpireTime())) {
                        continue;
                    }
                    summonedIter.remove();
                    user.write(MessagePacket.skillExpire(summoned.getSkillId()));
                    broadcastPacket(SummonedPacket.summonedLeaveField(user, summoned));
                }
                // Expire town portal
                final TownPortal townPortal = user.getTownPortal();
                if (townPortal != null) {
                    if (townPortal.getExpireTime().isBefore(now)) {
                        townPortal.destroy();
                        user.setTownPortal(null);
                        user.write(WvsContext.resetTownPortal());
                        user.write(MessagePacket.skillExpire(townPortal.getSkillId()));
                        user.getConnectedServer().notifyUserUpdate(user);
                    }
                }
            }
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

    /**
     * Does not include user.
     */
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

    /**
     * Includes Summoned owned by user.
     */
    public void forEachPartySummoned(User user, BiConsumer<User, Summoned> consumer) {
        final int partyId = user.getPartyId();
        if (partyId != 0) {
            forEach((member) -> {
                if (member.getPartyId() == partyId) {
                    member.getSummoned().forEach((id, summoned) -> consumer.accept(member, summoned));
                }
            });
        } else {
            user.getSummoned().forEach((id, summoned) -> consumer.accept(user, summoned));
        }
    }
}
