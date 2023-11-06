package kinoko.world.user;

public enum DBChar {
    CHARACTER(0x1),
    MONEY(0x2),
    ITEM_SLOT_EQUIP(0x4),
    ITEM_SLOT_CONSUME(0x8),
    ITEM_SLOT_INSTALL(0x10),
    ITEM_SLOT_ETC(0x20),
    ITEM_SLOT_CASH(0x40),
    INVENTORY_SIZE(0x80),
    SKILL_RECORD(0x100),
    EQUIP_EXT_EXPIRE(0x100000),
    ALL(0xFFFFFFFFFFFFFFFFL);

    private final long value;

    DBChar(long value) {
        this.value = value;
    }

    public final long getValue() {
        return value;
    }

    public final boolean hasFlag(DBChar other) {
        return (getValue() & other.getValue()) != 0;
    }
}
