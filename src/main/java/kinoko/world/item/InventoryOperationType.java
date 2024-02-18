package kinoko.world.item;

public enum InventoryOperationType {
    NEW_ITEM(0),
    ITEM_NUMBER(1),
    POSITION(2),
    DEL_ITEM(3),
    EXP(4);

    private final int value;

    InventoryOperationType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
