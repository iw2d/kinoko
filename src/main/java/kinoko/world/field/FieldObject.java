package kinoko.world.field;

import java.util.Optional;
import java.util.Set;

public interface FieldObject {
    Field getField();

    void setField(Field field);

    int getId();

    void setId(int id);

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    <T extends FieldObject> Optional<T> getNearestObject(Set<T> objects);
}
