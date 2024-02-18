package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public abstract class FieldObject {
    private Field field;
    private int id;
    private int x;
    private int y;

    public abstract OutPacket enterFieldPacket();

    public abstract OutPacket leaveFieldPacket();

    public final Field getField() {
        return field;
    }

    public final void setField(Field field) {
        this.field = field;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public final int getX() {
        return x;
    }

    public final void setX(int x) {
        this.x = x;
    }

    public final int getY() {
        return y;
    }

    public final void setY(int y) {
        this.y = y;
    }
}
