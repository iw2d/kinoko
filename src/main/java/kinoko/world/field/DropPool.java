package kinoko.world.field;

import kinoko.packet.field.DropPacket;
import kinoko.world.drop.Drop;
import kinoko.world.drop.DropEnterType;
import kinoko.world.drop.DropLeaveType;

public final class DropPool extends FieldObjectPool<Drop> {
    public DropPool(Field field) {
        super(field);
    }

    public void addDrop(Drop drop, DropEnterType enterType) {
        lock.lock();
        try {
            drop.setId(field.getNewObjectId());
            addObjectUnsafe(drop);
            field.broadcastPacket(DropPacket.dropEnterField(drop, enterType));
        } finally {
            lock.unlock();
        }
    }

    public boolean removeDrop(Drop drop, DropLeaveType leaveType, int pickUpId, int petId) {
        lock.lock();
        try {
            if (!removeObjectUnsafe(drop)) {
                return false;
            }
            field.broadcastPacket(DropPacket.dropLeaveField(drop, leaveType, pickUpId, petId));
            return true;
        } finally {
            lock.unlock();
        }
    }
}
