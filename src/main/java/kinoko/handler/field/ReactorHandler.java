package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.script.ScriptDispatcher;
import kinoko.world.field.Field;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class ReactorHandler {
    private static final Logger log = LogManager.getLogger(ReactorHandler.class);

    @Handler(InHeader.REACTOR_HIT)
    public static void handleReactorHit(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwID
        inPacket.decodeInt(); // skillReactor?
        inPacket.decodeInt(); // dwHitOption
        final short delay = inPacket.decodeShort(); // tDelay
        final int skillId = inPacket.decodeInt(); // skillId, 0 for basic attack

        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getById(objectId);
        if (reactorResult.isEmpty()) {
            log.error("Received REACTOR_HIT for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedReactor = reactorResult.get().acquire()) {
            // Hit reactor
            final Reactor reactor = lockedReactor.get();
            if (!reactor.hit(skillId)) {
                log.error("{} : could not hit reactor with skill ID {}", reactor, skillId);
            }
            field.getReactorPool().hitReactor(reactor, delay);
            // Check if last state and dispatch action script
            if (!reactor.isLastState() || !reactor.hasAction()) {
                return;
            }
            ScriptDispatcher.startReactorScript(user, reactor);
        }
    }

    @Handler(InHeader.REACTOR_TOUCH)
    public static void handleReactorTouch(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwID
        final boolean inside = inPacket.decodeBoolean(); // PtInRect
        // TODO
    }
}
