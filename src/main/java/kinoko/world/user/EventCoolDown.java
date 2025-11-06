package kinoko.world.user;

import kinoko.server.event.EventType;

public final class EventCoolDown {
    private EventType eventType;
    private int amountDone;
    private long nextResetTime;

    public EventCoolDown(EventType eventType, int amountDone, long nextResetTime) {
        this.eventType = eventType;
        this.amountDone = amountDone;
        this.nextResetTime = nextResetTime;
    }
    public EventType getEventType() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    public int getAmountDone() {
        return amountDone;
    }
    public void setAmountDone(int amountDone) {
        this.amountDone = amountDone;
    }
    public long getNextResetTime() {
        return nextResetTime;
    }
    public void setNextResetTime(long nextResetTime) {
        this.nextResetTime = nextResetTime;
    }
}
