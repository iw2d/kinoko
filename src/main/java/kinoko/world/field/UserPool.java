package kinoko.world.field;

import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.world.field.life.Life;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.npc.Npc;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public final class UserPool extends FieldObjectPool<User> {
    public UserPool(Field field) {
        super(field);
    }

    @SuppressWarnings("unchecked")
    public void addUser(User user) {
        // Update client with existing users in pool
        forEach((existingUser) -> {
            try (var locked = existingUser.acquire()) {
                user.write(locked.get().enterFieldPacket());
            }
        });

        // Add user to pool
        addObject(user);
        broadcastPacket(user.enterFieldPacket(), user);

        // Add user pets
        for (Pet pet : user.getPets()) {
            if (pet == null) {
                continue;
            }
            pet.setX(user.getX());
            pet.setY(user.getY());
            pet.setFoothold(user.getFoothold());
            broadcastPacket(pet.enterFieldPacket());
        }

        // Handle other field objects
        final Consumer<? extends Life> lifeHandler = (life) -> {
            user.write(life.enterFieldPacket());
            if (!(life instanceof ControlledObject controlled)) {
                return;
            }
            // Update controller as required
            if (controlled.getController() == null) {
                controlled.setController(user);
                user.write(controlled.changeControllerPacket(true));
            } else {
                user.write(controlled.changeControllerPacket(false));
            }
        };
        field.getMobPool().forEach((Consumer<Mob>) lifeHandler);
        field.getNpcPool().forEach((Consumer<Npc>) lifeHandler);
        field.getReactorPool().forEach((reactor) -> {
            user.write(reactor.enterFieldPacket());
        });
        field.getDropPool().forEach((drop) -> {
            user.write(drop.enterFieldPacket());
        });
    }

    @SuppressWarnings("unchecked")
    public boolean removeUser(User user) {
        if (!removeObject(user)) {
            return false;
        }
        broadcastPacket(user.leaveFieldPacket(), user);

        // Handle controller change
        final Optional<User> controllerResult = Util.getRandomFromCollection(getObjects());
        final Consumer<? extends Life> controllerHandler = (life) -> {
            if (!(life instanceof ControlledObject controlled)) {
                return;
            }
            if (controlled.getController() != user) {
                return;
            }
            if (controllerResult.isEmpty()) {
                controlled.setController(null);
                return;
            }
            setController(controlled, controllerResult.get());
        };
        field.getMobPool().forEach((Consumer<Mob>) controllerHandler);
        field.getNpcPool().forEach((Consumer<Npc>) controllerHandler);
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
            }
        }
    }

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

    public void assignController(ControlledObject controlled) {
        final Optional<User> controllerResult = Util.getRandomFromCollection(getObjects());
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
}
