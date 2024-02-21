package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SkillHandler {
    private static final Logger log = LogManager.getLogger(SkillHandler.class);

    @Handler(InHeader.USER_SKILL_UP_REQUEST)
    public static void handleUserSkillUpRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int skillId = inPacket.decodeInt(); // nSkillID
        // TODO
    }

    @Handler(InHeader.USER_SKILL_USE_REQUEST)
    public static void handleUserSkillUseRequest(User user, InPacket inPacket) {
        // TODO
    }
}
