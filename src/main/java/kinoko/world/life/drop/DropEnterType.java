package kinoko.world.life.drop;

public enum DropEnterType {
    JUST_SHOWING(0),
    CREATE(1),
    ON_THE_FOOTHOLD(2),
    FADING_OUT(3);

    private final int value;

    DropEnterType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
