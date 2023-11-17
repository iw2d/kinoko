package kinoko.world.life;

import kinoko.world.field.Field;
import kinoko.world.field.FieldObject;

public abstract class Life implements FieldObject {
    private final Field field;
    private int lifeId = -1;

    protected Life(Field field) {
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }

    public int getLifeId() {
        return lifeId;
    }

    public void setLifeId(int lifeId) {
        this.lifeId = lifeId;
    }
}
