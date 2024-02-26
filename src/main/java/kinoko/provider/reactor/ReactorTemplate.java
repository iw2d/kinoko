package kinoko.provider.reactor;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

import java.util.*;

public final class ReactorTemplate {
    private final int id;
    private final String action;
    private final Map<Integer, ReactorState> states;

    public ReactorTemplate(int id, String action, Map<Integer, ReactorState> states) {
        this.id = id;
        this.action = action;
        this.states = states;
    }

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public Map<Integer, ReactorState> getStates() {
        return states;
    }

    public int getLastState() {
        final Optional<Integer> maxResult = states.keySet().stream()
                .max(Comparator.comparingInt(Integer::valueOf));
        return maxResult.orElse(0);
    }

    public static ReactorTemplate from(int reactorId, String action, WzListProperty reactorProp) throws ProviderError {
        // Process states
        final Map<Integer, ReactorState> states = new HashMap<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!(reactorProp.get(String.valueOf(i)) instanceof WzListProperty stateProp)) {
                break;
            }
            int timeOut = 0;
            // Process events
            final Set<ReactorEvent> events = new HashSet<>();
            if (stateProp.get("event") instanceof WzListProperty eventList) {
                timeOut = WzProvider.getInteger(eventList.get("timeOut"), 0);
                for (int j = 0; j < Integer.MAX_VALUE; j++) {
                    if (!(eventList.get(String.valueOf(j)) instanceof WzListProperty eventProp)) {
                        break;
                    }
                    events.add(ReactorEvent.from(eventProp));
                }
            }
            states.put(i, new ReactorState(
                    Collections.unmodifiableSet(events),
                    timeOut
            ));
        }
        return new ReactorTemplate(
                reactorId,
                action,
                Collections.unmodifiableMap(states)
        );
    }
}
