package kinoko.world.field;

import kinoko.packet.field.DropPacket;
import kinoko.provider.map.Foothold;
import kinoko.world.GameConstants;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropLeaveType;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public final class DropPool extends FieldObjectPool<Drop> {
    public DropPool(Field field) {
        super(field);
    }

    public void addDrop(Drop drop, DropEnterType enterType, int x, int y) {
        prepareDrop(drop, x, y);
        lock.lock();
        try {
            if (enterType != DropEnterType.FADING_OUT) {
                addObjectUnsafe(drop);
            }
            field.broadcastPacket(DropPacket.dropEnterField(drop, enterType));
        } finally {
            lock.unlock();
        }
    }

    public void addDrops(Set<Drop> drops, DropEnterType enterType, int centerX, int centerY) {
        final int width = drops.size() * GameConstants.DROP_SPREAD;
        int dropX = centerX - (width / 2);
        for (Drop drop : drops) {
            addDrop(drop, enterType, dropX, centerY);
            dropX += GameConstants.DROP_SPREAD;
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

    public void expireDrops(Instant now) {
        lock.lock();
        try {
            final var iter = objects.values().iterator();
            while (iter.hasNext()) {
                final Drop drop = iter.next();
                // Check drop expire time and remove drop
                if (now.isBefore(drop.getExpireTime())) {
                    continue;
                }
                iter.remove();
                field.broadcastPacket(DropPacket.dropLeaveField(drop, DropLeaveType.TIMEOUT, 0, 0));
            }
        } finally {
            lock.unlock();
        }
    }

    private void prepareDrop(Drop drop, int x, int y) {
        final Optional<Foothold> footholdResult = field.getFootholdBelow(x, y);
        drop.setId(field.getNewObjectId());
        if (footholdResult.isPresent()) {
            drop.setX(x);
            drop.setY(footholdResult.get().getYFromX(x));
        } else {
            drop.setX(x);
            drop.setY(y);
        }
    }
}
