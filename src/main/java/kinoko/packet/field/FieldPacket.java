package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class FieldPacket {
    public static OutPacket transferChannelReqIgnored(int failureType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TRANSFER_CHANNEL_REQ_IGNORED);
        outPacket.encodeByte(failureType);
        // 1 : Cannot move to that Channel
        // 2 : Cannot go into Cash Shop
        // 3 : Item Trading Shop is currently unavailable
        // 4 : Cannot go into Trade Shop due to user count
        // 5 : Do not meet the minimum level requirement to access the Trade Shop
        // default : no message
        return outPacket;
    }
}
