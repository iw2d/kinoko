package kinoko.server.event;

import kinoko.packet.field.FieldPacket;
import kinoko.provider.map.PortalInfo;
import kinoko.server.field.FieldStorage;
import kinoko.world.field.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

public abstract class Event {
    protected static final Logger log = LogManager.getLogger(Event.class);
    protected final FieldStorage fieldStorage;
    protected EventState currentState;
    protected ScheduledFuture<?> eventFuture;

    public Event(FieldStorage fieldStorage) {
        this.fieldStorage = fieldStorage;
    }

    public abstract EventType getType();

    public abstract void initialize();

    public abstract void nextState();

    public final EventState getState() {
        return currentState;
    }

    public final void shutdown() {
        eventFuture.cancel(true);
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    protected final int getNearestMinute() {
        return LocalDateTime.now().plusSeconds(30).getMinute();
    }

    protected final void warp(int sourceFieldId, int destinationFieldId, String portalName) {
        warp(List.of(sourceFieldId), destinationFieldId, portalName);
    }

    protected final void warp(List<Integer> sourceFieldIds, int destinationFieldId, String portalName) {
        // Resolve destination field
        final Optional<Field> destinationFieldResult = fieldStorage.getFieldById(destinationFieldId);
        if (destinationFieldResult.isEmpty()) {
            log.error("Could not resolve destination field ID : {}", destinationFieldId);
            return;
        }
        final Field destinationField = destinationFieldResult.get();
        // Resolve portal
        final Optional<PortalInfo> destinationPortalResult = destinationField.getPortalByName(portalName);
        if (destinationPortalResult.isEmpty()) {
            log.error("Could not resolve portal {} for field ID : {}", portalName, destinationFieldId);
            return;
        }
        final PortalInfo destinationPortal = destinationPortalResult.get();
        // Warp users in source fields
        for (int sourceFieldId : sourceFieldIds) {
            final Optional<Field> sourceFieldResult = fieldStorage.getFieldById(sourceFieldId);
            if (sourceFieldResult.isEmpty()) {
                log.error("Could not resolve source field ID : {}", sourceFieldId);
                return;
            }
            sourceFieldResult.get().getUserPool().forEach((user) -> {
                try (var locked = user.acquire()) {
                    locked.get().warp(destinationField, destinationPortal, false, false);
                }
            });
        }
    }

    protected final void setReactorState(int fieldId, int reactorTemplateId, int newState) {
        // Resolve field
        final Optional<Field> fieldResult = fieldStorage.getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            log.error("Could not resolve field ID : {}", fieldId);
            return;
        }
        final Field field = fieldResult.get();
        // Resolve and change reactor state
        field.getReactorPool().forEach((reactor) -> {
            if (reactor.getTemplateId() == reactorTemplateId) {
                try (var lockedReactor = reactor.acquire()) {
                    reactor.setState(newState);
                    field.broadcastPacket(FieldPacket.reactorChangeState(reactor, 0, 0, 0));
                }
            }
        });
    }
}
