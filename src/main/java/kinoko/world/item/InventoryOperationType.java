package kinoko.world.item;

public enum InventoryOperationType {
    // ChangeLog
    NewItem(0),
    ItemNumber(1),
    Position(2),
    DelItem(3),
    EXP(4);

    private final int value;

    InventoryOperationType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
