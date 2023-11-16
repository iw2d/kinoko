package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

public final class UserPacket {
    public static OutPacket userEnterField(User user) {
        // TODO
        return OutPacket.of(OutHeader.USER_ENTER_FIELD);
    }

    public static OutPacket userLeaveField(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_LEAVE_FIELD);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterId
        return outPacket;
    }
}
