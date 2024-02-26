package kinoko.provider.reactor;

import java.util.Set;

public final class ReactorState {
    private final Set<ReactorEvent> events;
    private final int timeOut;

    public ReactorState(Set<ReactorEvent> events, int timeOut) {
        this.events = events;
        this.timeOut = timeOut;
    }

    public Set<ReactorEvent> getEvents() {
        return events;
    }

    public int getTimeOut() {
        return timeOut;
    }
}
