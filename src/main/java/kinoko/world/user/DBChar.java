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
    QUEST_RECORD(0x200),
    MINIGAME_RECORD(0x400),
    COUPLE_RECORD(0x800),
    MAP_TRANSFER(0x1000),

    QUEST_COMPLETE(0x4000),
    SKILL_COOLTIME(0x8000),
    MONSTER_BOOK_CARD(0x10000),
    MONSTER_BOOK_COVER(0x20000),
    NEW_YEAR_CARD(0x40000),
    QUEST_RECORD_EX(0x80000),
    EQUIP_EXT_EXPIRE(0x100000),
    WILD_HUNTER_INFO(0x200000),
    QUEST_COMPLETE_OLD(0x400000),
    VISITOR_QUEST_LOG(0x400000),

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
