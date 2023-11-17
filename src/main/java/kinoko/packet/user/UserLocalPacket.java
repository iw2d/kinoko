package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class UserLocalPacket {
    public static OutPacket sitResult(boolean sit, short fieldSeatId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SIT_RESULT);
        outPacket.encodeByte(sit);
        if (sit) {
            outPacket.encodeShort(fieldSeatId);
        }
        return outPacket;
    }

    public static OutPacket chatMsg(int type, String text) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CHAT_MSG);
        outPacket.encodeShort(type); // lType
        outPacket.encodeString(text); // sChat
        return outPacket;
    }
}
