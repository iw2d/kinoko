package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserCommonPacket;
import kinoko.server.Client;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.USER_CHAT)
    public static void handleUserChat(Client c, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final String text = inPacket.decodeString(); // sText
        final boolean onlyBalloon = inPacket.decodeBoolean(); // bOnlyBalloon

        c.write(UserCommonPacket.userChat(c.getUser().getId(), 0, text, onlyBalloon));
    }
}
