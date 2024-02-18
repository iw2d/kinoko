package kinoko.world.drop;

public enum DropOwnType {
    USER_OWN(0),
    PARTY_OWN(1),
    NO_OWN(2),
    EXPLOSIVE_NO_OWN(3);

    private final int value;

    DropOwnType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
