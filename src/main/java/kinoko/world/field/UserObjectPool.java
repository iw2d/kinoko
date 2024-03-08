package kinoko.world.field;

import kinoko.world.field.summoned.Summoned;

import java.time.Instant;
import java.util.Optional;

public final class UserObjectPool extends FieldObjectPool<UserObject> {
    public UserObjectPool(Field field) {
        super(field);
    }

    public void updateObjects(Instant now) {
        // TODO
    }


    // CSummonedPool ---------------------------------------------------------------------------------------------------

    public Optional<Summoned> getSummonedById(int objectId) {
        final Optional<UserObject> objectResult = getById(objectId);
        if (objectResult.isEmpty() || !(objectResult.get() instanceof Summoned summoned)) {
            return Optional.empty();
        }
        return Optional.of(summoned);
    }

    public void addSummoned(Summoned summoned) {
        summoned.setField(field);
        summoned.setId(field.getNewObjectId());
        addObject(summoned);
        field.broadcastPacket(summoned.enterFieldPacket());
    }

    public boolean removeSummoned(Summoned summoned) {
        if (removeObject(summoned)) {
            return false;
        }
        field.broadcastPacket(summoned.leaveFieldPacket());
        return true;
    }
}
