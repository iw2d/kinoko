package kinoko.world.field.summoned;

public enum SummonedEnterType {
    // ENTER_TYPE
    DEFAULT(0),
    CREATE_SUMMONED(1),
    REREGISTER_SUMMONED(2);

    private final int value;

    SummonedEnterType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
