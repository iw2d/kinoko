package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public abstract class FieldObjectImpl implements FieldObject {
    private Field field;
    private int id;
    private int x;
    private int y;

    @Override
    public abstract OutPacket enterFieldPacket();

    @Override
    public abstract OutPacket leaveFieldPacket();

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
}
