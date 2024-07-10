package kinoko.world.field;

import java.util.Collection;
import java.util.Optional;

public interface FieldObject {
    Field getField();

    void setField(Field field);

    int getId();

    void setId(int id);

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    <T extends FieldObject> Optional<T> getNearestObject(Collection<T> objects);
}
