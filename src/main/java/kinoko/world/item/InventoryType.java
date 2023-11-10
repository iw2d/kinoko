package kinoko.world.item;

public enum InventoryType {
    EQUIP,
    CONSUME,
    INSTALL,
    ETC,
    CASH;

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
}
