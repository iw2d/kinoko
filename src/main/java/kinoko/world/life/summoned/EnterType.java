package kinoko.world.life.summoned;

public enum EnterType {
    // ENTER_TYPE
    DEFAULT(0),
    CREATE_SUMMONED(1),
    REREGISTER_SUMMONED(2);

    private final int value;

    EnterType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
