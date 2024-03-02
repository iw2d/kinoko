package kinoko.server.dialog.trunk;

public enum TrunkRequestType {
    // TrunkReq
    LOAD(0),
    SAVE(1),
    CLOSE(2),
    CHECK_SSN2(3),
    GET_ITEM(4),
    PUT_ITEM(5),
    SORT_ITEM(6),
    MONEY(7),
    CLOSE_DIALOG(8);

    private final int value;

    TrunkRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static TrunkRequestType getByValue(int value) {
        for (TrunkRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
