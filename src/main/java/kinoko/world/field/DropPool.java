package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
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

    public void addDrop(Drop drop, DropEnterType enterType, int x, int y, int delay) {
        final Optional<Foothold> footholdResult = field.getFootholdBelow(x, y);
        if (footholdResult.isPresent()) {
            drop.setX(x);
            drop.setY(footholdResult.get().getYFromX(x));
        } else {
            drop.setX(x);
            drop.setY(y);
        }
        drop.setField(field);
        drop.setId(field.getNewObjectId());
        if (enterType != DropEnterType.FADING_OUT) {
            addObject(drop);
            // Handle drop reactors
            field.getReactorPool().forEach((reactor) -> reactor.handleDrop(drop));
        }
        field.broadcastPacket(FieldPacket.dropEnterField(drop, enterType, delay));
    }

    public void addDrops(Set<Drop> drops, DropEnterType enterType, int centerX, int centerY, int addDelay) {
        int dropX = centerX - (drops.size() * GameConstants.DROP_SPREAD / 2);
        int delay = 0;
        for (Drop drop : drops) {
            addDrop(drop, enterType, dropX, centerY, delay);
            dropX += GameConstants.DROP_SPREAD;
            delay += addDelay;
        }
    }

    public boolean removeDrop(Drop drop, DropLeaveType leaveType, int pickUpId, int petIndex, int delay) {
        if (!removeObject(drop)) {
            return false;
        }
        field.broadcastPacket(FieldPacket.dropLeaveField(drop, leaveType, pickUpId, petIndex, delay));
        return true;
    }

    public void expireDrops(Instant now) {
        final var iter = objects.values().iterator();
        while (iter.hasNext()) {
            final Drop drop = iter.next();
            // Check drop expire time and remove drop
            if (now.isBefore(drop.getExpireTime())) {
                continue;
            }
            iter.remove();
            field.broadcastPacket(FieldPacket.dropLeaveField(drop, DropLeaveType.TIMEOUT, 0, 0, 0));
        }
    }
}
