package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public interface FieldObject {
    OutPacket enterFieldPacket();

    OutPacket leaveFieldPacket();

    Field getField();

    void setField(Field field);

    int getId();

    void setId(int id);

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);
}
