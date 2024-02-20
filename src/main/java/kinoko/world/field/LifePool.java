package kinoko.world.field;

import kinoko.world.field.life.Life;

public final class LifePool extends FieldObjectPool<Life> {
    public LifePool(Field field) {
        super(field);
    }

    public void addLife(Life life) {
        life.setField(field);
        lock.lock();
        try {
            life.setId(field.getNewObjectId());
            addObjectUnsafe(life);
            field.broadcastPacket(life.enterFieldPacket());

            // Assign controller
            if (!(life instanceof ControlledObject controlled)) {
                return;
            }
            field.getUserPool().assignController(controlled);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeLife(Life life) {
        lock.lock();
        try {
            if (!removeObjectUnsafe(life)) {
                return false;
            }
            field.broadcastPacket(life.leaveFieldPacket());
            return true;
        } finally {
            lock.unlock();
        }
    }
}
