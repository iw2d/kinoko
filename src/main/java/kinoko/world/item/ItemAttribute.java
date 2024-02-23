package kinoko.world.item;

public enum ItemAttribute {
    // GW_ItemSlotEquip
    EQUIP_PROTECTED(0x1),
    EQUIP_PREVENT_SLIP(0x2),
    EQUIP_SUPPORT_WARM(0x4),
    EQUIP_BINDED(0x8),
    EQUIP_POSSIBLE_TRADING(0x10),

    // GW_ItemSlotBundle
    BUNDLE_PROTECTED(0x1),
    BUNDLE_POSSIBLE_TRADING(0x02),

    // GW_ItemSlotPet
    PET_POSSIBLE_TRADING(0x01);

    private final short value;

    ItemAttribute(int value) {
        this.value = (short) value;
    }

    public final short getValue() {
        return value;
    }
}