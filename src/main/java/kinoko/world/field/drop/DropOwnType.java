package kinoko.world.field.drop;

public enum DropOwnType {
    // DROP
    USEROWN(0),
    PARTYOWN(1),
    NOOWN(2),
    EXPLOSIVE_NOOWN(3);

    private final int value;

    DropOwnType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
