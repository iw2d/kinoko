package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public interface FieldObject {
    OutPacket enterFieldPacket();

    OutPacket leaveFieldPacket();
}
