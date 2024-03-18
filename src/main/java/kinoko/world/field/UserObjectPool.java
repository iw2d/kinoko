package kinoko.world.field;

import kinoko.world.field.summoned.Summoned;
import kinoko.world.user.User;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class UserObjectPool extends FieldObjectPool<UserObject> {
    public UserObjectPool(Field field) {
        super(field);
    }

    public void addUserObjects(Set<UserObject> added) {
        for (UserObject object : added) {
            object.setField(field);
            object.setId(field.getNewObjectId());
            addObject(object);
            field.broadcastPacket(object.enterFieldPacket());
        }
    }

    public Set<UserObject> removeUserObjects(User user) {
        final Set<UserObject> removed = new HashSet<>();
        final var iter = objects.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Integer, UserObject> entry = iter.next();
            final UserObject object = entry.getValue();
            if (object.getOwnerId() != user.getCharacterId()) {
                continue;
            }
            iter.remove();
            removed.add(object);
            field.broadcastPacket(object.leaveFieldPacket());
        }
        return removed;
    }

    public void updateUserObjects(Instant now) {
        final var iter = objects.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Integer, UserObject> entry = iter.next();
            if (entry.getValue() instanceof Summoned summoned) {
                if (now.isBefore(summoned.getExpireTime())) {
                    continue;
                }
                iter.remove();
                field.broadcastPacket(summoned.leaveFieldPacket());
            }
        }
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
        addUserObjects(Set.of(summoned));
    }

    public boolean removeSummoned(Summoned summoned) {
        if (removeObject(summoned)) {
            return false;
        }
        field.broadcastPacket(summoned.leaveFieldPacket());
        return true;
    }
}
