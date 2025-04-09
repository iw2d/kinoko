package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
import kinoko.world.field.affectedarea.AffectedArea;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public final class AffectedAreaPool extends FieldObjectPool<AffectedArea> {
    private final AtomicInteger fieldTickCounter = new AtomicInteger(1);

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

    public void updateAffectedAreas(Instant now) {
        final int counter = fieldTickCounter.getAndIncrement();
        final var iter = objects.values().iterator();
        while (iter.hasNext()) {
            final AffectedArea affectedArea = iter.next();
            // Check users and mobs inside area every `interval` ticks
            if (affectedArea.getInterval() != 0 && counter % affectedArea.getInterval() == 0) {
                switch (affectedArea.getType()) {
                    case UserSkill -> field.getMobPool().forEach((mob) -> {
                        if (mob.getHp() > 0 && affectedArea.getRect().isInsideRect(mob.getX(), mob.getY())) {
                            affectedArea.handleMobInside(mob);
                        }
                    });
                    case MobSkill, Buff, BlessedMist -> field.getUserPool().forEach((user) -> {
                        if (user.getHp() > 0 && affectedArea.getRect().isInsideRect(user.getX(), user.getY())) {
                            affectedArea.handleUserInside(user);
                        }
                    });
                }
            }
            // Check affected area expire time and remove
            if (now.isAfter(affectedArea.getExpireTime())) {
                iter.remove();
                field.broadcastPacket(FieldPacket.affectedAreaRemoved(affectedArea));
            }
        }
    }
}
