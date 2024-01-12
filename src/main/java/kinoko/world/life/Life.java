package kinoko.world.life;

import kinoko.world.field.Field;
import kinoko.world.field.FieldObject;

public abstract class Life implements FieldObject {
    private int objectId = -1;

    private int x;
    private int y;
    private int fh;
    private int moveAction;
    private Field field;

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getFh() {
        return fh;
    }

    @Override
    public void setFh(int fh) {
        this.fh = fh;
    }

    @Override
    public int getMoveAction() {
        return moveAction;
    }

    @Override
    public void setMoveAction(int moveAction) {
        this.moveAction = moveAction;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void setField(Field field) {
        this.field = field;
    }
}
