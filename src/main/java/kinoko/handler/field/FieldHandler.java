package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FieldHandler {
    private static final Logger log = LogManager.getLogger(FieldHandler.class);

    @Handler(InHeader.CANCEL_INVITE_PARTY_MATCH)
    public static void handleCancelInvitePartyMatch(User user, InPacket inPacket) {
    }
}
