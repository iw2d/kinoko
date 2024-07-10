package kinoko.provider.reactor;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

import java.util.*;

public final class ReactorTemplate {
    private final int id;
    private final boolean notHitable;
    private final boolean activateByTouch;
    private final String action;
    private final Map<Integer, ReactorState> states;

    public ReactorTemplate(int id, boolean notHitable, boolean activateByTouch, String action, Map<Integer, ReactorState> states) {
        this.id = id;
        this.notHitable = notHitable;
        this.activateByTouch = activateByTouch;
        this.action = action;
        this.states = states;
    }

    public int getId() {
        return id;
    }

    public boolean isNotHitable() {
        return notHitable;
    }

    public boolean isActivateByTouch() {
        return activateByTouch;
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

    public Optional<ReactorEvent> getHitEvent(int state, int skillId) {
        final ReactorState reactorState = states.get(state);
        if (reactorState == null) {
            return Optional.empty();
        }
        for (ReactorEvent event : reactorState.getEvents()) {
            if ((event.getType() == ReactorEventType.HIT && skillId == 0) ||
                    (event.getType() == ReactorEventType.SKILL && event.getSkills().contains(skillId))) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    public Optional<ReactorEvent> getDropEvent(int state, int itemId, int x, int y) {
        final ReactorState reactorState = states.get(state);
        if (reactorState == null) {
            return Optional.empty();
        }
        for (ReactorEvent event : reactorState.getEvents()) {
            if (event.getType() == ReactorEventType.DROP &&
                    event.getItemId() == itemId) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    public static ReactorTemplate from(int reactorId, boolean notHitable, boolean activateByTouch, String action, WzListProperty reactorProp) throws ProviderError {
        // Process states
        final Map<Integer, ReactorState> states = new HashMap<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!(reactorProp.get(String.valueOf(i)) instanceof WzListProperty stateProp)) {
                break;
            }
            int timeOut = 0;
            // Process events
            final List<ReactorEvent> events = new ArrayList<>();
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
                    Collections.unmodifiableList(events),
                    timeOut
            ));
        }
        return new ReactorTemplate(
                reactorId,
                notHitable,
                activateByTouch,
                action,
                Collections.unmodifiableMap(states)
        );
    }
}
