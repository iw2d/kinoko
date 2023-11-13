package kinoko.provider.item;

import java.util.HashMap;
import java.util.Map;

public enum ItemInfoType {
    CASH,
    PRICE,
    UNIT_PRICE,
    SLOT_MAX,
    DURABILITY,
    ONLY,
    TRAD_BLOCK,
    DROP_BLOCK,
    TRADE_BLOCK,
    TRADE_AVAILABLE,
    EQUIP_TRADE_BLOCK,
    SCAN_TRADE_BLOCK,
    TIME_LIMITED,
    NOT_EXTEND,
    NOT_SALE,
    ONLY_EQUIP,
    QUEST,
    QUEST_ID,
    UI_DATA,
    MOB_POTION,
    MAX_LEVEL,
    SET_ITEM_ID,

    INC_STR,
    INC_DEX,
    INC_INT,
    INC_LUK,
    INC_HP,
    INC_MP,
    INC_MHP,
    INC_MHPR,
    INC_MMP,
    INC_MMPR,
    INC_MAX_HP,
    INC_MAX_MP,
    INC_PAD,
    INC_MAD,
    INC_PDD,
    INC_MDD,
    INC_ACC,
    INC_EVA,
    INC_CRAFT,
    INC_SPEED,
    INC_JUMP,
    INC_SWIM,
    INC_FATIGUE,
    INC_IUC,

    // Elemental Wand/Staff
    ELEM_DEFAULT,
    INC_RMAF,
    INC_RMAI,
    INC_RMAL,
    INC_RMAS,

    LEVEL,
    REQ,
    REQ_STR,
    REQ_DEX,
    REQ_INT,
    REQ_LUK,
    REQ_LEVEL,
    REQ_POP,
    REQ_CUC,
    REQ_RUC,
    REQ_SKILL_LEVEL,
    ENCHANT_CATEGORY,
    TUC,
    IUC_MAX,

    RECOVERY_HP,
    RECOVERY_MP;

    private static final Map<String, ItemInfoType> nameMap = new HashMap<>();

    static {
        for (ItemInfoType type : values()) {
            final String name = normalizeName(type.name());
            nameMap.put(name, type);
        }
    }

    private static String normalizeName(String name) {
        return name.replace("_", "").toLowerCase();
    }

    public static ItemInfoType fromName(String name) {
        return nameMap.get(normalizeName(name));
    }
}
