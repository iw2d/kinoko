package kinoko.server.event;

import kinoko.server.field.ChannelFieldStorage;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class EventManager {
    private final Map<EventType, Event> eventMap = new EnumMap<>(EventType.class); // event identifier -> event

    public Optional<EventState> getEventState(EventType eventType) {
        final Event event = eventMap.get(eventType);
        if (event == null) {
            return Optional.empty();
        }
        return Optional.of(event.getState());
    }

    public void initialize(ChannelFieldStorage fieldStorage) {
        // Orbis - Victoria Airship
        initializeEvent(new ContiMoveVictoria(fieldStorage));
        // Orbis - Ludibrium Airship
        initializeEvent(new ContiMoveLudibrium(fieldStorage));
        // Orbis - Leafre Airship
        initializeEvent(new ContiMoveLeafre(fieldStorage));
        // Orbis - Ariant Genie
        initializeEvent(new ContiMoveAriant(fieldStorage));
        // Ludibrium Elevator
        initializeEvent(new Elevator(fieldStorage));
        // KC - NLC Subway
        initializeEvent(new Subway(fieldStorage));
        // KC - CBD Airport
        initializeEvent(new Airport(fieldStorage));
    }

    public void shutdown() {
        for (Event event : eventMap.values()) {
            event.shutdown();
        }
        eventMap.clear();
    }

    private void initializeEvent(Event event) {
        if (eventMap.containsKey(event.getType())) {
            throw new IllegalStateException("Tried to register duplicate event type : " + event.getType());
        }
        event.initialize();
        eventMap.put(event.getType(), event);
    }
}
