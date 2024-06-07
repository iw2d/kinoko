package kinoko.provider.reactor;

public enum ReactorEventType {
    // Guessed type names from WZ.
    HIT(0),
    UNK_1(1),
    UNK_2(2),
    SKILL(5),
    ENTER(6), // not sure
    LEAVE(7), // not sure
    DROP(100),
    TIME_OUT(101);

    private final int value;

    ReactorEventType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static ReactorEventType getByValue(int value) {
        for (ReactorEventType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
