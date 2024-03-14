package kinoko.provider.mob;

import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.util.HashMap;
import java.util.Map;

public enum MobSkillType {
    // MOBSKILL
    POWER_UP(100),
    MAGIC_UP(101),
    PGUARD_UP(102),
    MGUARD_UP(103),
    HASTE(104),
    POWER_UP_M(110),
    MAGIC_UP_M(111),
    PGUARD_UP_M(112),
    MGUARD_UP_M(113),
    HEAL_M(114),
    HASTE_M(115),
    SEAL(120),
    DARKNESS(121),
    WEAKNESS(122),
    STUN(123),
    CURSE(124),
    POISON(125),
    SLOW(126),
    DISPEL(127),
    ATTRACT(128),
    BAN_MAP(129),
    AREA_FIRE(130),
    AREA_POISON(131),
    REVERSE_INPUT(132),
    UNDEAD(133),
    STOP_POTION(134),
    STOP_MOTION(135),
    FEAR(136),
    FROZEN(137),
    PHYSICAL_IMMUNE(140),
    MAGIC_IMMUNE(141),
    HARD_SKIN(142),
    PCOUNTER(143),
    MCOUNTER(144),
    PMCOUNTER(145),
    PAD(150),
    MAD(151),
    PDR(152),
    MDR(153),
    ACC(154),
    EVA(155),
    SPEED(156),
    SEAL_SKILL(157),
    BALROG_COUNTER(158),
    SPREAD_SKILL_FROM_USER(160),
    HEAL_BY_DAMAGE(161),
    BIND(162),
    SUMMON(200),
    SUMMON_CUBE(201);

    private static final Map<Integer, MobSkillType> typeMap;

    static {
        typeMap = new HashMap<>();
        for (MobSkillType type : values()) {
            typeMap.put(type.getId(), type);
        }
    }

    private final int id;

    MobSkillType(int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

    public final MobTemporaryStat getMobTemporaryStat() {
        return switch (this) {
            case POWER_UP, POWER_UP_M -> MobTemporaryStat.PowerUp;
            case MAGIC_UP, MAGIC_UP_M -> MobTemporaryStat.MagicUp;
            case PGUARD_UP, PGUARD_UP_M -> MobTemporaryStat.PGuardUp;
            case MGUARD_UP, MGUARD_UP_M -> MobTemporaryStat.MGuardUp;
            case HASTE, HASTE_M, SPEED -> MobTemporaryStat.Speed;
            case PHYSICAL_IMMUNE -> MobTemporaryStat.PImmune;
            case MAGIC_IMMUNE -> MobTemporaryStat.MImmune;
            case PAD -> MobTemporaryStat.PAD;
            case MAD -> MobTemporaryStat.MAD;
            case PDR -> MobTemporaryStat.PDR;
            case MDR -> MobTemporaryStat.MDR;
            case ACC -> MobTemporaryStat.ACC;
            case EVA -> MobTemporaryStat.EVA;
            default -> null;
        };
    }

    public final CharacterTemporaryStat getCharacterTemporaryStat() {
        return switch (this) {
            case SEAL -> CharacterTemporaryStat.Seal;
            case DARKNESS -> CharacterTemporaryStat.Darkness;
            case WEAKNESS -> CharacterTemporaryStat.Weakness;
            case STUN -> CharacterTemporaryStat.Stun;
            case CURSE -> CharacterTemporaryStat.Curse;
            case POISON -> CharacterTemporaryStat.Poison;
            case SLOW -> CharacterTemporaryStat.Slow;
            case ATTRACT -> CharacterTemporaryStat.Attract;
            case REVERSE_INPUT -> CharacterTemporaryStat.ReverseInput;
            case UNDEAD -> CharacterTemporaryStat.Undead;
            case FEAR -> CharacterTemporaryStat.Fear;
            default -> null;
        };
    }

    public static MobSkillType getByValue(int value) {
        return typeMap.get(value);
    }
}
