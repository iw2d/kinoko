package kinoko.server.dialog.trunk;

public enum TrunkRequestType {
    // TrunkReq
    Load(0),
    Save(1),
    Close(2),
    CheckSSN2(3),
    GetItem(4),
    PutItem(5),
    SortItem(6),
    Money(7),
    CloseDialog(8);

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
