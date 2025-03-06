package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.field.ContiMovePacket;
import kinoko.packet.field.FieldPacket;
import kinoko.server.event.*;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class FieldHandler {
    private static final Logger log = LogManager.getLogger(FieldHandler.class);

    @Handler(InHeader.CANCEL_INVITE_PARTY_MATCH)
    public static void handleCancelInvitePartyMatch(User user, InPacket inPacket) {
    }


    // BEGIN_DROPPOOL --------------------------------------------------------------------------------------------------

    @Handler(InHeader.DropPickUpRequest)
    public static void handleDropPickUpRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte();
        if (user.getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // update_time
        inPacket.decodeShort(); // pt->x
        inPacket.decodeShort(); // pt->y
        final int objectId = inPacket.decodeInt(); // dwDropID
        inPacket.decodeInt(); // dwCliCrc

        // Find drop in field
        final Field field = user.getField();
        final Optional<Drop> dropResult = field.getDropPool().getById(objectId);
        if (dropResult.isEmpty()) {
            user.dispose();
            return;
        }

        // Pick up drop
        try (var locked = user.acquire()) {
            field.getDropPool().pickUpDrop(locked, dropResult.get(), DropLeaveType.PICKED_UP_BY_USER, 0);
            user.dispose();
        }
    }


    // BEGIN_REACTORPOOL -----------------------------------------------------------------------------------------------

    @Handler(InHeader.ReactorHit)
    public static void handleReactorHit(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwID
        inPacket.decodeInt(); // skillReactor?
        inPacket.decodeInt(); // dwHitOption
        final short delay = inPacket.decodeShort(); // tDelay
        final int skillId = inPacket.decodeInt(); // skillId, 0 for basic attack

        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getById(objectId);
        if (reactorResult.isEmpty()) {
            log.error("Received ReactorHit for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedReactor = reactorResult.get().acquire()) {
            // Hit reactor
            final Reactor reactor = lockedReactor.get();
            if (reactor.isNotHitable()) {
                log.error("{} : tried to hit reactor that is not hitable", reactor);
                return;
            }
            if (!reactor.tryHit(skillId)) {
                log.error("{} : could not hit reactor with skill ID {}", reactor, skillId);
                return;
            }
            field.getReactorPool().hitReactor(user, reactor, delay);
        }
    }

    @Handler(InHeader.ReactorTouch)
    public static void handleReactorTouch(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwID
        final boolean inside = inPacket.decodeBoolean(); // PtInRect

        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getById(objectId);
        if (reactorResult.isEmpty()) {
            log.error("Received ReactorTouch for invalid object with ID : {}", objectId);
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
            log.error(String.format("Unexpected reactor touch received for %s", reactor));
        }
    }

    @Handler(InHeader.RequireFieldObstacleStatus)
    public static void handleRequireFieldObstacleStatus(User user, InPacket inPacket) {
    }


    // CONTISTATE ------------------------------------------------------------------------------------------------------

    @Handler(InHeader.CONTISTATE)
    public static void handleContiState(User user, InPacket inPacket) {
        final int fieldId = inPacket.decodeInt();
        inPacket.decodeByte(); // nShipKind
        // Resolve event type
        final EventType eventType;
        switch (fieldId) {
            case ContiMoveVictoria.ORBIS_STATION_VICTORIA_BOUND, ContiMoveVictoria.STATION_TO_ORBIS,
                    ContiMoveVictoria.DURING_THE_RIDE_VICTORIA_BOUND, ContiMoveVictoria.DURING_THE_RIDE_TO_ORBIS -> {
                eventType = EventType.CM_VICTORIA;
            }
            case ContiMoveLudibrium.ORBIS_STATION_LUDIBRIUM, ContiMoveLudibrium.LUDIBRIUM_STATION_ORBIS -> {
                eventType = EventType.CM_LUDIBRIUM;
            }
            case ContiMoveLeafre.ORBIS_STATION_TO_LEAFRE, ContiMoveLeafre.LEAFRE_STATION -> {
                eventType = EventType.CM_LEAFRE;
            }
            case ContiMoveAriant.ORBIS_STATION_TO_ARIANT, ContiMoveAriant.ARIANT_STATION_PLATFORM -> {
                eventType = EventType.CM_ARIANT;
            }
            default -> {
                log.error("Received CONTISTATE for unhandled field ID : {}", fieldId);
                return;
            }
        }
        // Resolve event state
        final Optional<EventState> eventStateResult = user.getConnectedServer().getEventState(eventType);
        if (eventStateResult.isEmpty()) {
            log.error("Could not resolve event state for event type : {}", eventType);
            return;
        }
        // Update client
        final EventState eventState = eventStateResult.get();
        if (eventState == EventState.CONTIMOVE_BOARDING || eventState == EventState.CONTIMOVE_WAITING) {
            user.write(ContiMovePacket.enterShipMove());
        } else if (eventState == EventState.CONTIMOVE_MOBGEN) {
            user.write(ContiMovePacket.mobGen());
        }
    }


    // BEGIN_ITEMUPGRADE -----------------------------------------------------------------------------------------------

    @Handler(InHeader.ItemUpgradeComplete)
    public static void handleItemUpgradeComplete(User user, InPacket inPacket) {
        inPacket.decodeInt(); // nReturnResult
        final int result = inPacket.decodeInt(); // nResult
        user.write(FieldPacket.itemUpgradeResultDone(result));
    }
}
