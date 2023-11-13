package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class UserCommonPacket {
    public static OutPacket userChat(int characterId, int type, String text, boolean onlyBalloon) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_CHAT);
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(type); // lType
        outPacket.encodeString(text); // sChat
        outPacket.encodeByte(onlyBalloon);
        return outPacket;
    }
}
