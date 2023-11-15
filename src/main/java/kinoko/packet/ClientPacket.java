package kinoko.packet;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class ClientPacket {
    public static OutPacket migrateCommand(byte[] channelAddress, int port) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MIGRATE_COMMAND);
        outPacket.encodeByte(true);
        outPacket.encodeArray(channelAddress);
        outPacket.encodeShort(port);
        return outPacket;
    }

    public static OutPacket aliveReq() {
        return OutPacket.of(OutHeader.ALIVE_REQ);
    }
}
