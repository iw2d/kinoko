package kinoko.world.field;

import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.world.life.Life;
import kinoko.world.life.mob.Mob;
import kinoko.world.life.npc.Npc;
import kinoko.world.user.User;

import java.util.Optional;
import java.util.function.Consumer;

public final class UserPool extends FieldObjectPool<User> {
    public UserPool(Field field) {
        super(field);
    }

    @SuppressWarnings("unchecked")
    public void addUser(User user) {
        lock.lock();
        try {
            addObjectUnsafe(user);
            broadcastPacketUnsafe(user.enterFieldPacket(), user);

            // Handle field objects
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

    private void broadcastPacketUnsafe(OutPacket outPacket, User except) {
        getObjectsUnsafe().forEach((user) -> {
            if (except != null && user.getCharacterId() == except.getCharacterId()) {
                return;
            }
            user.write(outPacket);
        });
    }
}
