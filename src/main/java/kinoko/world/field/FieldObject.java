package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public interface FieldObject {
    Field getField();

    void setField(Field field);

    OutPacket enterFieldPacket();

    OutPacket leaveFieldPacket();
}
