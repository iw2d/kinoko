package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.MapleTvMessage;

public class MapleTvPacket {
    // CMapleTVMan::OnPacket -------------------------------------------------------------------------------------------

    public static OutPacket updateMessage(MapleTvMessage message, int totalWaitTime) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MapleTVUpdateMessage);
        outPacket.encodeByte(message.getFlag());
        outPacket.encodeByte(message.getType()); // m_nMessageType (0 : MAPLETV, 1 : MAPLESOLETV, 2 : MAPLELOVETV)
        message.getSender().encode(outPacket); // m_alSender
        outPacket.encodeString(message.getSenderName()); // sSender
        outPacket.encodeString(message.getReceiverName()); // sReceiver
        outPacket.encodeString(message.getS1());
        outPacket.encodeString(message.getS2());
        outPacket.encodeString(message.getS3());
        outPacket.encodeString(message.getS4());
        outPacket.encodeString(message.getS5());
        outPacket.encodeInt(totalWaitTime); // m_nTotalWaitTime
        if ((message.getFlag() & 2) != 0) {
            message.getReceiver().encode(outPacket); // m_alReceiver
        }
        return outPacket;
    }

    public static OutPacket clearMessage() {
        return OutPacket.of(OutHeader.MapleTVClearMessage);
    }
}
