package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserCommonPacket;
import kinoko.server.ServerConfig;
import kinoko.server.client.Client;
import kinoko.server.command.CommandProcessor;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.life.MovePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.USER_MOVE)
    public static void handleUserMove(Client c, InPacket inPacket) {
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeByte(); // bFieldKey
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // dwCrc
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // Crc32

        final MovePath movePath = MovePath.decode(inPacket);
        log.info(movePath);
    }

    @Handler(InHeader.USER_CHAT)
    public static void handleUserChat(Client c, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final String text = inPacket.decodeString(); // sText
        final boolean onlyBalloon = inPacket.decodeBoolean(); // bOnlyBalloon

        if (text.startsWith(ServerConfig.COMMAND_PREFIX) && CommandProcessor.tryProcessCommand(c, text)) {
            return;
        }

        c.write(UserCommonPacket.userChat(c.getUser().getId(), 0, text, onlyBalloon));
    }
}
