package kinoko.packet;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class ClientPacket {
    public static OutPacket migrateCommand(byte[] channelHost, int channelPort) {
        assert channelHost.length == 4;
        final OutPacket outPacket = OutPacket.of(OutHeader.MIGRATE_COMMAND);
        outPacket.encodeByte(true);
        outPacket.encodeArray(channelHost);
        outPacket.encodeShort(channelPort);
        return outPacket;
    }

    public static OutPacket aliveReq() {
        return OutPacket.of(OutHeader.ALIVE_REQ);
    }
}
