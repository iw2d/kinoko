package kinoko.packet.field;

public enum TransferFieldType {
    // TF
    DONE(0),
    DISABLED_PORTAL(1),
    NOT_CONNECTED_AREA(2),
    NOT_ALLOWED_LEVEL(3),
    NOT_ALLOWED_LEVEL_ITEM(4),
    NOT_ALLOWED_LEVEL_MD(5),
    PARTY_ONLY(6),
    EXPEDITION_ONLY(7),
    NOT_AVAILABLE_SHOP(8);

    private final int value;

    TransferFieldType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
