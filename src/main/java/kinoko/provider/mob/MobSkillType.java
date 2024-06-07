package kinoko.provider.mob;

import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.util.HashMap;
import java.util.Map;

public enum MobSkillType {
    // MOBSKILL
    POWERUP(100),
    MAGICUP(101),
    PGUARDUP(102),
    MGUARDUP(103),
    HASTE(104),
    POWERUP_M(110),
    MAGICUP_M(111),
    PGUARDUP_M(112),
    MGUARDUP_M(113),
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
    BANMAP(129),
    AREA_FIRE(130),
    AREA_POISON(131),
    REVERSE_INPUT(132),
    UNDEAD(133),
    STOPPORTION(134), // [sic]
    STOPMOTION(135),
    FEAR(136),
    FROZEN(137),
    PHYSICALIMMUNE(140),
    MAGICIMMUNE(141),
    HARDSKIN(142),
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
    SEALSKILL(157),
    BALROGCOUNTER(158),
    SPREADSKILLFROMUSER(160),
    HEALBYDAMAGE(161),
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
            case POWERUP, POWERUP_M -> MobTemporaryStat.PowerUp;
            case MAGICUP, MAGICUP_M -> MobTemporaryStat.MagicUp;
            case PGUARDUP, PGUARDUP_M -> MobTemporaryStat.PGuardUp;
            case MGUARDUP, MGUARDUP_M -> MobTemporaryStat.MGuardUp;
            case HASTE, HASTE_M, SPEED -> MobTemporaryStat.Speed;
            case PHYSICALIMMUNE -> MobTemporaryStat.PImmune;
            case MAGICIMMUNE -> MobTemporaryStat.MImmune;
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
            case BANMAP -> CharacterTemporaryStat.BanMap;
            case REVERSE_INPUT -> CharacterTemporaryStat.ReverseInput;
            // case UNDEAD -> CharacterTemporaryStat.Undead;
            case STOPPORTION -> CharacterTemporaryStat.StopPortion;
            case STOPMOTION -> CharacterTemporaryStat.StopMotion;
            case FEAR -> CharacterTemporaryStat.Fear;
            case FROZEN -> CharacterTemporaryStat.Frozen;
            default -> null;
        };
    }

    public static MobSkillType getByValue(int value) {
        return typeMap.get(value);
    }
}
