package kinoko.server.whisper;

public enum LocationResultType {
    // LR
    NONE(0),
    GAMESVR(1),
    SHOPSVR(2),
    OTHER_CHANNEL(3),
    ADMIN(4);

    private final int value;

    LocationResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
