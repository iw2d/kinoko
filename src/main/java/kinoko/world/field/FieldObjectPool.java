package kinoko.world.field;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public abstract class FieldObjectPool<T extends FieldObject> {
    protected final Map<Integer, T> objects = new HashMap<>(); // FieldObject::getId() -> FieldObject
    protected final Lock lock = new ReentrantLock();
    protected final Field field;

    protected FieldObjectPool(Field field) {
        this.field = field;
    }

    public final Optional<T> getById(int id) {
        lock.lock();
        try {
            if (!objects.containsKey(id)) {
                return Optional.empty();
            }
            return Optional.of(objects.get(id));
        } finally {
            lock.unlock();
        }
    }

    public final void forEach(Consumer<T> consumer) {
        lock.lock();
        try {
            objects.values().forEach(consumer);
        } finally {
            lock.unlock();
        }
    }

    protected void addObjectUnsafe(T object) {
        objects.put(object.getId(), object);
    }

    protected boolean removeObjectUnsafe(T object) {
        return objects.remove(object.getId(), object);
    }

    protected Collection<T> getObjectsUnsafe() {
        return objects.values();
    }
}
