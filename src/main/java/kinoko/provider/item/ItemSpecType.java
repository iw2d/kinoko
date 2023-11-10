package kinoko.provider.item;

import java.util.HashMap;
import java.util.Map;

public enum ItemSpecType {
    // HEAL ------------------------------------------------------------------------------------------------------------
    HP,
    MP,
    HPR,
    MPR,
    INC, // Pet Food
    INC_FATIGUE, // Revitalizer (TamedMob)
    SEAL,
    CURSE,
    WEAKNESS,
    POISON,
    DARKNESS,

    // BUFF ------------------------------------------------------------------------------------------------------------
    TIME,
    STR,
    DEX,
    INT,
    LUK,
    MHPR,
    MMPR,
    PAD,
    MAD,
    PDD,
    MDD,
    ACC,
    EVA,
    SPEED,
    JUMP,
    BOOSTER,
    EXP,
    EXP_BUFF,
    ITEM_UP_BY_ITEM,
    MESO_UP_BY_ITEM,

    MHPR_RATE,
    MMPR_RATE,
    PAD_RATE,
    MAD_RATE,
    PDD_RATE,
    MDD_RATE,
    ACC_RATE,
    EVA_RATE,
    SPEED_RATE,
    EVENT_RATE,
    EVENT_POINT,
    BARRIER,
    BERSERK,

    // SCRIPT ----------------------------------------------------------------------------------------------------------
    NPC,
    SCRIPT,
    MOVE_TO,
    IGNORE_CONTINENT,
    RETURN_MAP_QR,
    ONLY_PICKUP,
    RUN_ON_PICKUP,
    CONSUME_ON_PICKUP,
    REPEAT_EFFECT,
    UI_NUMBER,
    MORPH,
    MORPH_RANDOM,
    GHOST,
    RANDOM_MOVE_IN_FIELD_SET,

    CP,
    PARTY,
    OTHER_PARTY,
    NUFF_SKILL, // One of the members in the opposing party will have their buffs nullified
    RESPECT_FS,
    RESPECT_PIMMUNE,
    RESPECT_MIMMUNE,
    DEFENSE_ATT,
    DEFENSE_STATE,
    CON,
    THAW,
    PROB,
    ITEM_CODE,
    ITEM_RANGE,
    MOB,
    MOB_ID,
    MOB_HP,
    ATTACK_MOB_ID,
    ATTACK_INDEX,
    DOJANG_SHIELD,
    BF_SKILL;

    private static final Map<String, ItemSpecType> nameMap = new HashMap<>();
    static {
        for (ItemSpecType type : values()) {
            final String name = normalizeName(type.name());
            nameMap.put(name, type);
        }
    }

    private static String normalizeName(String name) {
        return name.replace("_", "").toLowerCase();
    }

    public static ItemSpecType fromName(String name) {
        return nameMap.get(normalizeName(name));
    }
}
