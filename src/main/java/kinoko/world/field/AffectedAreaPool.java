package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
import kinoko.world.field.affectedarea.AffectedArea;

import java.time.Instant;

public final class AffectedAreaPool extends FieldObjectPool<AffectedArea> {
    public AffectedAreaPool(Field field) {
        super(field);
    }

    public void addAffectedArea(AffectedArea affectedArea) {
        affectedArea.setField(field);
        affectedArea.setId(field.getNewObjectId());
        addObject(affectedArea);
        field.broadcastPacket(FieldPacket.affectedAreaCreated(affectedArea));
    }

    public boolean removeAffectedArea(AffectedArea affectedArea) {
        if (!removeObject(affectedArea)) {
            return false;
        }
        field.broadcastPacket(FieldPacket.affectedAreaRemoved(affectedArea));
        return true;
    }

    public void removeByOwnerId(int ownerId) {
        final var iter = objects.values().iterator();
        while (iter.hasNext()) {
            final AffectedArea affectedArea = iter.next();
            if (affectedArea.getOwner().getId() == ownerId) {
                iter.remove();
                field.broadcastPacket(FieldPacket.affectedAreaRemoved(affectedArea));
            }
        }
    }

    public void expireAffectedAreas(Instant now) {
        final var iter = objects.values().iterator();
        while (iter.hasNext()) {
            final AffectedArea affectedArea = iter.next();
            // Check affected area expire time and remove
            if (now.isBefore(affectedArea.getExpireTime())) {
                continue;
            }
            iter.remove();
            field.broadcastPacket(FieldPacket.affectedAreaRemoved(affectedArea));
        }
    }
}
