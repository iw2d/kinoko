package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public interface FieldObject {
    Field getField();

    OutPacket enterFieldPacket();

    OutPacket leaveFieldPacket();
}
