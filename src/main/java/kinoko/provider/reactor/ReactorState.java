package kinoko.provider.reactor;

import java.util.List;

public final class ReactorState {
    private final List<ReactorEvent> events;
    private final int timeOut;

    public ReactorState(List<ReactorEvent> events, int timeOut) {
        this.events = events;
        this.timeOut = timeOut;
    }

    public List<ReactorEvent> getEvents() {
        return events;
    }

    public int getTimeOut() {
        return timeOut;
    }
}
