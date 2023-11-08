package kinoko.world.item;

public enum ItemType {
    EQUIP(1),
    BUNDLE(2),
    PET(3);

    private final int value;

    ItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ItemType getByValue(int value) {
        for (ItemType itemType : values()) {
            if (itemType.getValue() == value) {
                return itemType;
            }
        }
        return null;
    }
}
