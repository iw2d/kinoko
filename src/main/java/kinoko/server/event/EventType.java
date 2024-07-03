package kinoko.server.event;

public enum EventType {
    CM_VICTORIA,
    CM_LUDIBRIUM,
    CM_LEAFRE,
    CM_ARIANT,
    CM_ELEVATOR,
    CM_SUBWAY;

    public static EventType getByName(String name) {
        for (EventType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
