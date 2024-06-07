package kinoko.packet.user.effect;

public enum EffectType {
    // UserEffect
    LevelUp(0),
    SkillUse(1),
    SkillAffected(2),
    SkillAffected_Select(3),
    SkillSpecialAffected(4),
    Quest(5),
    Pet(6),
    SkillSpecial(7),
    ProtectOnDieItemUse(8),
    PlayPortalSE(9),
    JobChanged(10),
    QuestComplete(11),
    IncDecHPEffect(12),
    BuffItemEffect(13),
    SquibEffect(14),
    MonsterBookCardGet(15),
    LotteryUse(16),
    ItemLevelUp(17),
    ItemMaker(18),
    ExpItemConsumed(19),
    ReservedEffect(20),
    Buff(21),
    ConsumeEffect(22),
    UpgradeTombItemUse(23),
    BattlefieldItemUse(24),
    AvatarOriented(25),
    IncubatorUse(26),
    PlaySoundWithMuteBGM(27),
    SoulStoneUse(28),
    IncDecHPEffect_EX(29),
    DeliveryQuestItemUse(30),
    RepeatEffectRemove(31),
    EvolRing(32);

    private final int value;

    EffectType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
