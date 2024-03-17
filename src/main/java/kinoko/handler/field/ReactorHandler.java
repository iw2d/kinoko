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
            if (reactor.isNotHitable()) {
                log.error("{} : tried to hit reactor that is not hitable", reactor);
                return;
            }
            if (!reactor.hit(skillId)) {
                log.error("{} : could not hit reactor with skill ID {}", reactor, skillId);
                return;
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

        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getById(objectId);
        if (reactorResult.isEmpty()) {
            log.error("Received handleReactorTouch for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedReactor = reactorResult.get().acquire()) {
            // Hit reactor
            final Reactor reactor = lockedReactor.get();
            if (!reactor.isActivateByTouch()) {
                log.error("{} : tried to hit reactor that is not activated by touch", reactor);
                return;
            }
            // There are no reactors activated by touch in v95
            throw new IllegalStateException(String.format("Unexpected reactor touch received for %s", reactor));
        }
    }

    @Handler(InHeader.REQUIRE_FIELD_OBSTACLE_STATUS)
    public static void handleRequireFieldObstacleStatus(User user, InPacket inPacket) {
    }
}
