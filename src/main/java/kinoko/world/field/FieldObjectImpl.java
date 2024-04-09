package kinoko.world.field;

import kinoko.util.Util;

import java.util.Optional;
import java.util.Set;

public abstract class FieldObjectImpl implements FieldObject {
    private Field field;
    private int id;
    private int x;
    private int y;

    @Override
    public final Field getField() {
        return field;
    }

    @Override
    public final void setField(Field field) {
        this.field = field;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public final int getX() {
        return x;
    }

    @Override
    public final void setX(int x) {
        this.x = x;
    }

    @Override
    public final int getY() {
        return y;
    }

    @Override
    public final void setY(int y) {
        this.y = y;
    }

    @Override
    public final <T extends FieldObject> Optional<T> getNearestObject(Set<T> objects) {
        double nearestDistance = Double.MAX_VALUE;
        T nearestObject = null;
        for (T object : objects) {
            final double distance = Util.distance(getX(), getY(), object.getX(), object.getY());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestObject = object;
            }
        }
        return Optional.ofNullable(nearestObject);
    }
}
