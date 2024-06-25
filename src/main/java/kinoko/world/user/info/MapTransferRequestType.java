package kinoko.world.user.info;

public enum MapTransferRequestType {
    // MapTransferReq
    DeleteList(0),
    RegisterList(1);

    private final int value;

    MapTransferRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static MapTransferRequestType getByValue(int value) {
        for (MapTransferRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
