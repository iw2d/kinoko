package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserCommonPacket;
import kinoko.packet.user.UserLocalPacket;
import kinoko.packet.user.UserRemotePacket;
import kinoko.server.ServerConfig;
import kinoko.server.command.CommandProcessor;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.life.MovePath;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(UserHandler.class);

    @Handler(InHeader.USER_MOVE)
    public static void handleUserMove(User user, InPacket inPacket) {
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeByte(); // bFieldKey
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // dwCrc
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // Crc32

        final MovePath movePath = MovePath.decode(inPacket);
        movePath.applyTo(user);
        user.getField().broadcastPacket(UserRemotePacket.userMove(user.getCharacterId(), movePath), user);
    }

    @Handler(InHeader.USER_SIT_REQUEST)
    public static void handleUserSitRequest(User user, InPacket inPacket) {
        final short fieldSeatId = inPacket.decodeShort();
        user.write(UserLocalPacket.userSitResult(fieldSeatId != -1, fieldSeatId));
    }

    @Handler(InHeader.USER_CHAT)
    public static void handleUserChat(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final String text = inPacket.decodeString(); // sText
        final boolean onlyBalloon = inPacket.decodeBoolean(); // bOnlyBalloon

        if (text.startsWith(ServerConfig.COMMAND_PREFIX) && CommandProcessor.tryProcessCommand(user, text)) {
            return;
        }

        user.getField().broadcastPacket(UserCommonPacket.userChat(user.getCharacterId(), 0, text, onlyBalloon));
    }
}
