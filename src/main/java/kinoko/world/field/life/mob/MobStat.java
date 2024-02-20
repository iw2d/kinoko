package kinoko.world.field.life.mob;

import kinoko.util.BitIndex;

import java.util.List;

public enum MobStat implements BitIndex {
    PAD(0),
    PDR(1),
    MAD(2),
    MDR(3),
    ACC(4),
    EVA(5),
    Speed(6),
    Stun(7),
    Freeze(8),
    Poison(9),
    Seal(10),
    Darkness(11),
    PowerUp(12),
    MagicUp(13),
    PGuardUp(14),
    MGuardUp(15),
    Doom(16),
    Web(17),
    PImmune(18),
    MImmune(19),
    Showdown(20),
    HardSkin(21),
    Ambush(22),
    DamagedElemAttr(23),
    Venom(24),
    Blind(25),
    SealSkill(26),
    Burned(27),
    Dazzle(28),
    PCounter(29),
    MCounter(30),
    Disable(31),
    RiseByToss(32),
    BodyPressure(33),
    Weakness(34),
    TimeBomb(35),
    MagicCrash(36),
    HealByDamage(37);

    public static final int FLAG_SIZE = 128;
    public static final List<MobStat> ENCODE_ORDER = List.of(
            PAD, PDR, MAD, MDR, ACC, EVA, Speed, Stun, Freeze, Poison, Seal, Darkness, PowerUp, MagicUp, PGuardUp,
            MGuardUp, PImmune, MImmune, Doom, Web, HardSkin, Ambush, Venom, Blind, SealSkill, Dazzle, PCounter,
            MCounter, RiseByToss, BodyPressure, Weakness, TimeBomb, Showdown, MagicCrash, DamagedElemAttr, HealByDamage
    );

    private final int value;
    private final int arrayIndex;
    private final int bitPosition;

    MobStat(int value) {
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
