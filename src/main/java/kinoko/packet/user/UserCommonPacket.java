package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

public final class UserCommonPacket {
    public static OutPacket userChat(User user, int type, String text, boolean onlyBalloon) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_CHAT);
        outPacket.encodeInt(user.getId());
        outPacket.encodeByte(type); // lType
        outPacket.encodeString(text); // sChat
        outPacket.encodeByte(onlyBalloon);
        return outPacket;
    }
}
