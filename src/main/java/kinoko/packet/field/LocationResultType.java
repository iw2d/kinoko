package kinoko.packet.field;

public enum LocationResultType {
    // LR
    None(0),
    GameSvr(1),
    ShopSvr(2),
    OtherChannel(3),
    Admin(4);

    private final int value;

    LocationResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
