package kinoko.world.field;

import kinoko.packet.field.DropPacket;
import kinoko.provider.map.Foothold;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropLeaveType;

import java.util.Optional;

public final class DropPool extends FieldObjectPool<Drop> {
    public DropPool(Field field) {
        super(field);
    }

    public void addDrop(Drop drop, DropEnterType enterType, int x, int y) {
        final Optional<Foothold> footholdResult = field.getFootholdBelow(x, y);
        drop.setId(field.getNewObjectId());
        if (footholdResult.isPresent()) {
            drop.setX(x);
            drop.setY(footholdResult.get().getYFromX(x));
        } else {
            drop.setX(x);
            drop.setY(y);
        }
        lock.lock();
        try {
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
