package kinoko.world.field;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class FieldObjectPool<T extends FieldObject> {
    protected final ConcurrentHashMap<Integer, T> objects = new ConcurrentHashMap<>(); // FieldObject::getId() -> FieldObject
    protected final Field field;

    protected FieldObjectPool(Field field) {
        this.field = field;
    }

    public Optional<T> getById(int id) {
        return Optional.ofNullable(objects.get(id));
    }

    public void forEach(Consumer<T> consumer) {
        objects.forEachValue(Long.MAX_VALUE, consumer);
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    protected void addObject(T object) {
        objects.put(object.getId(), object);
    }

    protected boolean removeObject(T object) {
        return objects.remove(object.getId(), object);
    }

    protected Set<T> getObjects() {
        return objects.values().stream().collect(Collectors.toUnmodifiableSet());
    }
}
