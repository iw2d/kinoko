package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReactorHandler {
    private static final Logger log = LogManager.getLogger(LifeHandler.class);

    @Handler(InHeader.REACTOR_HIT)
    public static void handleReactorHit(User user, InPacket inPacket) {
        inPacket.decodeInt(); // dwID
        inPacket.decodeInt(); // skillReactor?
        inPacket.decodeInt(); // dwHitOption
        inPacket.decodeShort(); // tDelay
        inPacket.decodeInt(); // skillId
        // TODO
    }

    @Handler(InHeader.REACTOR_TOUCH)
    public static void handleReactorTouch(User user, InPacket inPacket) {
        inPacket.decodeInt(); // dwID
        inPacket.decodeBoolean(); // PtInRect
    }
}
