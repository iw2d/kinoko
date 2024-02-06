package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AttackHandler {
    private static final Logger log = LogManager.getLogger(AttackHandler.class);

    @Handler(InHeader.USER_MELEE_ATTACK)
    public static void handlerUserMeleeAttack(User user, InPacket inPacket) {

    }
}
