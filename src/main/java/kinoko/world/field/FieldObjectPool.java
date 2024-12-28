package kinoko.world.field;

import kinoko.util.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class FieldObjectPool<T extends FieldObject> {
    protected final ConcurrentHashMap<Integer, T> objects = new ConcurrentHashMap<>(); // FieldObject::getId() -> FieldObject
    protected final Field field;

    protected FieldObjectPool(Field field) {
        this.field = field;
    }

    public final int getCount() {
        return objects.size();
    }

    public final Optional<T> getById(int id) {
        return Optional.ofNullable(objects.get(id));
    }

    public final Optional<T> getBy(Predicate<T> predicate) {
        for (T object : objects.values()) {
            if (predicate.test(object)) {
                return Optional.of(object);
            }
        }
        return Optional.empty();
    }

    public final List<T> getInsideRect(Rect rect) {
        final List<T> inside = new ArrayList<>();
        for (T object : getObjects()) {
            if (rect.isInsideRect(object.getX(), object.getY())) {
                inside.add(object);
            }
        }
        return inside;
    }

    public final void forEach(Consumer<T> consumer) {
        objects.forEachValue(Long.MAX_VALUE, consumer);
    }

    public final boolean isEmpty() {
        return objects.isEmpty();
    }

    public final void clear() {
        objects.clear();
    }

    protected void addObject(T object) {
        objects.put(object.getId(), object);
    }

    protected boolean removeObject(T object) {
        return objects.remove(object.getId(), object);
    }

    protected List<T> getObjects() {
        return objects.values().stream().toList();
    }
}
