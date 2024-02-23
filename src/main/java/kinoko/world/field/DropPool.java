package kinoko.world.field;

import kinoko.packet.field.DropPacket;
import kinoko.provider.map.Foothold;
import kinoko.server.event.EventScheduler;
import kinoko.world.GameConstants;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropLeaveType;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
            if (enterType != DropEnterType.FADING_OUT) {
                addObjectUnsafe(drop);
                EventScheduler.addEvent(
                        () -> removeDrop(drop, DropLeaveType.TIMEOUT, 0, 0),
                        GameConstants.DROP_REMAIN_ON_GROUND_TIME,
                        TimeUnit.SECONDS
                );
            }
            field.broadcastPacket(DropPacket.dropEnterField(drop, enterType));
        } finally {
            lock.unlock();
        }
    }

    public boolean removeDrop(Drop drop, DropLeaveType leaveType, int pickUpId, int petIndex) {
        lock.lock();
        try {
            if (!removeObjectUnsafe(drop)) {
                return false;
            }
            field.broadcastPacket(DropPacket.dropLeaveField(drop, leaveType, pickUpId, petIndex));
            return true;
        } finally {
            lock.unlock();
        }
    }
}
