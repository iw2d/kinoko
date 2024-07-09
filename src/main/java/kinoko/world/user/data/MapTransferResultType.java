package kinoko.world.user.data;

public enum MapTransferResultType {
    // MapTransferRes
    DeleteList(2),
    RegisterList(3),
    Use(4),
    Unknown(5),
    TargetNotExist(6),
    TargetDied(7),
    NotAllowed(8),
    AlreadyInMap(9),
    RegisterFail(10),
    LevelLimit(11);

    private final int value;

    MapTransferResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
