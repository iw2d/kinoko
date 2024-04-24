package kinoko.packet.user.effect;

public enum EffectType {
    // UserEffect
    LEVEL_UP(0),
    SKILL_USE(1),
    SKILL_AFFECTED(2),
    SKILL_AFFECTED_SELECT(3),
    SKILL_SPECIAL_AFFECTED(4),
    QUEST(5),
    PET(6),
    SKILL_SPECIAL(7),
    PROTECT_ON_DIE_ITEM_USE(8),
    PLAY_PORTAL_SE(9),
    JOB_CHANGED(10),
    QUEST_COMPLETE(11),
    INC_DEC_HP_EFFECT(12),
    BUFF_ITEM_EFFECT(13),
    SQUIB_EFFECT(14),
    MOBSTER_BOOK_CARD_GET(15),
    LOTTERY_USE(16),
    ITEM_LEVEL_UP(17),
    ITEM_MAKER(18),
    EXP_ITEM_CONSUMED(19),
    RESERVED_EFFECT(20),
    BUFF(21),
    CONSUME_EFFECT(22),
    UPGRADE_TOMB_ITEM_USE(23),
    BATTLEFIELD_ITEM_USE(24),
    AVATAR_ORIENTED(25),
    INCUBATOR_USE(26),
    PLAY_SOUND_WITH_MUTE_BGM(27),
    SOUL_STONE_USE(28),
    INC_DEC_HP_EFFECT_EX(29),
    DELIVERY_QUEST_ITEM_USE(30),
    REPEAT_EFFECT_REMOVE(31),
    EVOL_RING(32);

    private final int value;

    EffectType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
