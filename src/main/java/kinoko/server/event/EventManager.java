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
        // NLC - Kerning City Subway
        final Subway subway = new Subway(fieldStorage);
        subway.initialize();
        registerEvent(subway);
        // Ludibrium Elevator
        final Elevator elevator = new Elevator(fieldStorage);
        elevator.initialize();
        registerEvent(elevator);
    }

    public void shutdown() {
        for (Event event : eventMap.values()) {
            event.shutdown();
        }
        eventMap.clear();
    }

    private void registerEvent(Event event) {
        if (eventMap.containsKey(event.getType())) {
            throw new IllegalStateException("Tried to register duplicate event type : " + event.getType());
        }
        eventMap.put(event.getType(), event);
    }
}
