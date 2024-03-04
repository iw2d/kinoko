package kinoko.world.user.stat;

import kinoko.util.BitIndex;

import java.util.List;

public enum CharacterTemporaryStat implements BitIndex {
    PAD(0),
    PDD(1),
    MAD(2),
    MDD(3),
    ACC(4),
    EVA(5),
    Craft(6),
    Speed(7),
    Jump(8),
    MagicGuard(9),
    DarkSight(10),
    Booster(11),
    PowerGuard(12),
    MaxHP(13),
    MaxMP(14),
    Invincible(15),
    SoulArrow(16),
    Stun(17),
    Poison(18),
    Seal(19),
    Darkness(20),
    ComboCounter(21),
    WeaponCharge(22),
    DragonBlood(23),
    HolySymbol(24),
    MesoUp(25),
    ShadowPartner(26),
    PickPocket(27),
    MesoGuard(28),
    Thaw(29),
    Weakness(30),
    Curse(31),
    Slow(32),
    Morph(33),
    Regen(34),
    BasicStatUp(35),
    Stance(36),
    SharpEyes(37),
    ManaReflection(38),
    Attract(39),
    SpiritJavelin(40),
    Infinity(41),
    Holyshield(42),
    HamString(43),
    Blind(44),
    Concentration(45),
    BanMap(46),
    MaxLevelBuff(47),
    MesoUpByItem(48),
    Ghost(49),
    Barrier(50),
    ReverseInput(51),
    ItemUpByItem(52),
    RespectPImmune(53),
    RespectMImmune(54),
    DefenseAtt(55),
    DefenseState(56),
    IncEffectHPPotion(57),
    IncEffectMPPotion(58),
    DojangBerserk(59),
    DojangInvincible(60),
    Spark(61),
    DojangShield(62),
    SoulMasterFinal(63),
    WindBreakerFinal(64),
    ElementalReset(65),
    WindWalk(66),
    EventRate(67),
    ComboAbilityBuff(68),
    ComboDrain(69),
    ComboBarrier(70),
    BodyPressure(71),
    SmartKnockback(72),
    RepeatEffect(73),
    ExpBuffRate(74),
    StopPortion(75),
    StopMotion(76),
    Fear(77),
    EvanSlow(78),
    MagicShield(79),
    MagicResistance(80),
    SoulStone(81),
    Flying(82),
    Frozen(83),
    AssistCharge(84),
    Enrage(85),
    SuddenDeath(86),
    NotDamaged(87),
    FinalCut(88),
    ThornsEffect(89),
    SwallowAttackDamage(90),
    MorewildDamageUp(91),
    Mine(92),
    EMHP(93),
    EMMP(94),
    EPAD(95),
    EPDD(96),
    EMDD(97),
    Guard(98),
    SafetyDamage(99),
    SafetyAbsorb(100),
    Cyclone(101),
    SwallowCritical(102),
    SwallowMaxMP(103),
    SwallowDefence(104),
    SwallowEvasion(105),
    Conversion(106),
    Revive(107),
    Sneak(108),
    Mechanic(109),
    Aura(110),
    DarkAura(111),
    BlueAura(112),
    YellowAura(113),
    SuperBody(114),
    MorewildMaxHP(115),
    Dice(116),
    BlessingArmor(117),
    DamR(118),
    TeleportMasteryOn(119),
    CombatOrders(120),
    Beholder(121),
    EnergyCharged(122),
    Dash_Speed(123),
    Dash_Jump(124),
    RideVehicle(125),
    PartyBooster(126),
    GuidedBullet(127),
    Undead(128),
    SummonBomb(129);

    public static final int FLAG_SIZE = 128;
    public static final List<CharacterTemporaryStat> LOCAL_ENCODE_ORDER = List.of(
            PAD, PDD, MAD, MDD, ACC, EVA, Craft, Speed, Jump, EMHP, EMMP, EPAD, EPDD, EMDD, MagicGuard, DarkSight,
            Booster, PowerGuard, Guard, SafetyDamage, SafetyAbsorb, MaxHP, MaxMP, Invincible, SoulArrow, Stun, Poison,
            Seal, Darkness, ComboCounter, WeaponCharge, DragonBlood, HolySymbol, MesoUp, ShadowPartner, PickPocket,
            MesoGuard, Thaw, Weakness, Curse, Slow, Morph, Ghost, Regen, BasicStatUp, Stance, SharpEyes, ManaReflection,
            Attract, SpiritJavelin, Infinity, Holyshield, HamString, Blind, Concentration, BanMap, MaxLevelBuff,
            Barrier, DojangShield, ReverseInput, MesoUpByItem, ItemUpByItem, RespectPImmune, RespectMImmune, DefenseAtt,
            DefenseState, DojangBerserk, DojangInvincible, Spark, SoulMasterFinal, WindBreakerFinal, ElementalReset,
            WindWalk, EventRate, ComboAbilityBuff, ComboDrain, ComboBarrier, BodyPressure, SmartKnockback, RepeatEffect,
            ExpBuffRate, IncEffectHPPotion, IncEffectMPPotion, StopPortion, StopMotion, Fear, EvanSlow, MagicShield,
            MagicResistance, SoulStone, Flying, Frozen, AssistCharge, Enrage, SuddenDeath, NotDamaged, FinalCut,
            ThornsEffect, SwallowAttackDamage, MorewildDamageUp, Mine, Cyclone, SwallowCritical, SwallowMaxMP,
            SwallowDefence, SwallowEvasion, Conversion, Revive, Sneak, Mechanic, Aura, DarkAura, BlueAura, YellowAura,
            SuperBody, MorewildMaxHP, Dice, BlessingArmor, DamR, TeleportMasteryOn, CombatOrders, Beholder, SummonBomb
    );
    public static final List<CharacterTemporaryStat> REMOTE_ENCODE_ORDER = List.of(
            Speed, ComboCounter, WeaponCharge, Stun, Darkness, Seal, Weakness, Curse, Poison, ShadowPartner, DarkSight,
            SoulArrow, Morph, Ghost, Attract, SpiritJavelin, BanMap, Barrier, DojangShield, ReverseInput,
            RespectPImmune, RespectMImmune, DefenseAtt, DefenseState, DojangBerserk, DojangInvincible, WindWalk,
            RepeatEffect, StopPortion, StopMotion, Fear, MagicShield, Flying, Frozen, SuddenDeath, FinalCut, Cyclone,
            Sneak, MorewildDamageUp, Mechanic, DarkAura, BlueAura, YellowAura, BlessingArmor
    );
    public static final List<CharacterTemporaryStat> TWO_STATE_ORDER = List.of(
            EnergyCharged, Dash_Speed, Dash_Jump, RideVehicle, PartyBooster, GuidedBullet // Undead doesn't fit in the mask...
    );
    public static final List<CharacterTemporaryStat> SWALLOW_BUFF_STAT = List.of(
            SwallowAttackDamage, SwallowDefence, SwallowCritical, SwallowMaxMP, SwallowEvasion
    );
    public static final List<CharacterTemporaryStat> MOVEMENT_AFFECTING_STAT = List.of(
            Speed, Jump, Stun, Weakness, Slow, Morph, Ghost, BasicStatUp, Attract, RideVehicle, Dash_Speed, Dash_Jump,
            Flying, Frozen, YellowAura
    );

    private final int value;
    private final int arrayIndex;
    private final int bitPosition;

    CharacterTemporaryStat(int value) {
        this.value = value;
        this.arrayIndex = value / 32;
        this.bitPosition = 1 << (31 - value % 32);
    }

    @Override
    public final int getValue() {
        return value;
    }

    @Override
    public int getArrayIndex() {
        return arrayIndex;
    }

    @Override
    public int getBitPosition() {
        return bitPosition;
    }
}
