package kinoko.server.event;

import kinoko.server.field.ChannelFieldStorage;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class EventManager {
    private final ConcurrentHashMap<String, Event> eventMap = new ConcurrentHashMap<>(); // event identifier -> event

    public Optional<EventState> getEventState(String eventIdentifier) {
        final Event event = eventMap.get(eventIdentifier);
        if (event == null) {
            return Optional.empty();
        }
        return Optional.of(event.getState());
    }

    public void initialize(ChannelFieldStorage fieldStorage) {
        // Ludibrium Elevator
        final Elevator elevator = new Elevator(fieldStorage);
        elevator.initialize();
        eventMap.put(elevator.getIdentifier(), elevator);
        // NLC - Kerning City Subway
        final Subway subway = new Subway(fieldStorage);
        subway.initialize();
        eventMap.put(subway.getIdentifier(), subway);
    }

    public void shutdown() {
        for (Event event : eventMap.values()) {
            event.shutdown();
        }
        eventMap.clear();
    }
}
