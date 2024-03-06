package kinoko.world.field;

import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.world.life.Life;
import kinoko.world.life.mob.Mob;
import kinoko.world.life.npc.Npc;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public final class UserPool extends FieldObjectPool<User> {
    public UserPool(Field field) {
        super(field);
    }

    @SuppressWarnings("unchecked")
    public void addUser(User user) {
        lock.lock();
        try {
            // Update user client with existing users in pool
            for (User existingUser : getObjectsUnsafe()) {
                try (var locked = existingUser.acquire()) {
                    user.write(locked.get().enterFieldPacket());
                }
            }

            // Add user to pool
            addObjectUnsafe(user);
            broadcastPacketUnsafe(user.enterFieldPacket(), user);

            // Add user pets
            for (Pet pet : user.getPets()) {
                if (pet == null) {
                    continue;
                }
                pet.setX(user.getX());
                pet.setY(user.getY());
                pet.setFoothold(user.getFoothold());
                broadcastPacketUnsafe(pet.enterFieldPacket());
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
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean removeUser(User user) {
        lock.lock();
        try {
            if (!removeObjectUnsafe(user)) {
                return false;
            }
            broadcastPacketUnsafe(user.leaveFieldPacket(), user);

            // Handle controller change
            final Optional<User> controllerResult = Util.getRandomFromCollection(getObjectsUnsafe());
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
                setControllerUnsafe(controlled, controllerResult.get());
            };
            field.getMobPool().forEach((Consumer<Mob>) controllerHandler);
            field.getNpcPool().forEach((Consumer<Npc>) controllerHandler);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void expireTemporaryStat(Instant now) {
        lock.lock();
        try {
            for (User user : getObjectsUnsafe()) {
                try (var locked = user.acquire()) {
                    final Set<CharacterTemporaryStat> removed = new HashSet<>();
                    final var iter = user.getSecondaryStat().getTemporaryStats().entrySet().iterator();
                    while (iter.hasNext()) {
                        final Map.Entry<CharacterTemporaryStat, TemporaryStatOption> entry = iter.next();
                        final CharacterTemporaryStat cts = entry.getKey();
                        final TemporaryStatOption option = entry.getValue();
                        // Check temporary stat expire time and remove cts
                        if (now.isBefore(option.getExpireTime())) {
                            continue;
                        }
                        iter.remove();
                        removed.add(cts);
                    }
                    // Update users if required
                    if (removed.isEmpty()) {
                        return;
                    }
                    user.write(WvsContext.temporaryStatReset(removed));
                    broadcastPacketUnsafe(UserRemote.temporaryStatReset(user, removed), user);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void assignController(ControlledObject controlled) {
        lock.lock();
        try {
            final Optional<User> controllerResult = Util.getRandomFromCollection(getObjectsUnsafe());
            if (controllerResult.isEmpty()) {
                controlled.setController(null);
                return;
            }
            setControllerUnsafe(controlled, controllerResult.get());
        } finally {
            lock.unlock();
        }
    }

    private void setControllerUnsafe(ControlledObject controlled, User controller) {
        controlled.setController(controller);
        controller.write(controlled.changeControllerPacket(true));
        broadcastPacketUnsafe(controlled.changeControllerPacket(false), controller);
    }

    private void broadcastPacketUnsafe(OutPacket outPacket) {
        broadcastPacketUnsafe(outPacket, null);
    }

    private void broadcastPacketUnsafe(OutPacket outPacket, User except) {
        getObjectsUnsafe().forEach((user) -> {
            if (except != null && user.getCharacterId() == except.getCharacterId()) {
                return;
            }
            user.write(outPacket);
        });
    }
}
