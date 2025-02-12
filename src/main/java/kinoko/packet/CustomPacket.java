package kinoko.packet;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class CustomPacket {
    public static OutPacket registerMigrationResultSuccess(byte[] channelHost, int channelPort, byte[] clientKey) {
        final OutPacket outPacket = OutPacket.of(OutHeader.RegisterMigrationResult);
        outPacket.encodeByte(0);
        outPacket.encodeArray(channelHost);
        outPacket.encodeShort(channelPort);
        outPacket.encodeArray(clientKey);
        return outPacket;
    }

    public static OutPacket registerMigrationResultFail() {
        final OutPacket outPacket = OutPacket.of(OutHeader.RegisterMigrationResult);
        outPacket.encodeByte(1);
        return outPacket;
    }
}
