package kinoko.world.item;

import kinoko.world.user.DBChar;

public enum InventoryType {
    EQUIPPED(0),
    EQUIP(1),
    CONSUME(2),
    INSTALL(3),
    ETC(4),
    CASH(5);

    private final int value;

    InventoryType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public final DBChar getFlag() {
        return switch (this) {
            case EQUIPPED, EQUIP -> DBChar.ITEM_SLOT_EQUIP;
            case CONSUME -> DBChar.ITEM_SLOT_CONSUME;
            case INSTALL -> DBChar.ITEM_SLOT_INSTALL;
            case ETC -> DBChar.ITEM_SLOT_ETC;
            case CASH -> DBChar.ITEM_SLOT_CASH;
        };
    }

    public static InventoryType getByItemId(int itemId) {
        if (ItemConstants.isEquip(itemId)) {
            return EQUIP;
        } else if (ItemConstants.isConsume(itemId)) {
            return CONSUME;
        } else if (ItemConstants.isInstall(itemId)) {
            return INSTALL;
        } else if (ItemConstants.isEtc(itemId)) {
            return ETC;
        } else {
            return CASH;
        }
    }

    public static InventoryType getByValue(int value) {
        for (InventoryType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    public static InventoryType getByPosition(InventoryType type, int position) {
        return (type == EQUIP && position < 0) ? EQUIPPED : type;
    }
}
