package kinoko.packet;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class ClientPacket {
    // CClientSocket::ProcessPacket ------------------------------------------------------------------------------------

    public static OutPacket migrateCommand(byte[] channelHost, int channelPort) {
        assert channelHost.length == 4;
        final OutPacket outPacket = OutPacket.of(OutHeader.MigrateCommand);
        outPacket.encodeByte(true);
        outPacket.encodeArray(channelHost);
        outPacket.encodeShort(channelPort);
        outPacket.encodeByte(0);
        return outPacket;
    }

    public static OutPacket aliveReq() {
        return OutPacket.of(OutHeader.AliveReq);
    }
}
