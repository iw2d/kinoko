package kinoko.provider.item;

import kinoko.world.user.stat.CharacterTemporaryStat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum ItemSpecType {
    // HEAL ------------------------------------------------------------------------------------------------------------
    hp,
    mp,
    hpR,
    mpR,
    inc, // Pet Food
    incFatigue, // Revitalizer (TamedMob)
    seal,
    curse,
    weakness,
    poison,
    darkness,

    // BUFF ------------------------------------------------------------------------------------------------------------
    time,
    str,
    dex,
    int_,
    luk,
    mhpR,
    mmpR,
    pad,
    mad,
    pdd,
    mdd,
    acc,
    eva,
    speed,
    jump,
    booster,
    thaw,
    exp,
    expinc,
    expBuff,
    defenseAtt,
    defenseState,
    itemupbyitem,
    mesoupbyitem,

    mhpRRate,
    mmpRRate,
    padRate,
    madRate,
    pddRate,
    mddRate,
    accRate,
    evaRate,
    speedRate,
    eventRate,
    eventPoint,
    barrier,
    berserk,

    // SCRIPT ----------------------------------------------------------------------------------------------------------
    npc,
    script,
    moveTo,
    ignoreContinent,
    returnMapQR,
    onlyPickup,
    runOnPickup,
    consumeOnPickup,
    repeatEffect,
    uiNumber,
    morph,
    morphRandom,
    ghost,
    screenMsg,
    randomMoveInFieldSet,

    cp,
    party,
    otherParty,
    nuffSkill, // One of the members in the opposing party will have their buffs nullified
    respectFS,
    respectPimmune,
    respectMimmune,
    con,
    prob,
    itemCode,
    itemRange,
    mob,
    mobID,
    mobHp,
    attackMobID,
    attackIndex,
    dojangshield,
    BFSkill,

    effectedOnAlly,
    incPVPDamage,
    bs,
    bsUp,
    randomPickupConsume,
    randomPickup,
    imhp,
    immp,
    indiePad,
    indieMad,
    inflation,
    immortal,
    lifeId,
    recipe,
    recipeUseCount,
    recipeValidDay,
    cosmetic,
    charismaEXP,
    insightEXP,
    willEXP,
    craftEXP,
    senseEXP,
    charmEXP,
    reqSkill,
    reqSkillLevel,
    reqSkillProficiency,
    useLevel,
    slotCount,
    slotPerLine,
    type;

    private static final Map<String, ItemSpecType> nameMap;
    private static final Set<String> ignoredTypes;

    static {
        nameMap = new HashMap<>();
        for (ItemSpecType type : values()) {
            nameMap.put(type == int_ ? "int" : type.name(), type);
        }
        ignoredTypes = Set.of(
                con.name(),
                mob.name(),
                morphRandom.name()
        );
    }

    public final CharacterTemporaryStat getStat() {
        return switch (this) {
            case pad -> CharacterTemporaryStat.PAD;
            case mad -> CharacterTemporaryStat.MAD;
            case pdd -> CharacterTemporaryStat.PDD;
            case mdd -> CharacterTemporaryStat.MDD;
            case acc -> CharacterTemporaryStat.ACC;
            case eva -> CharacterTemporaryStat.EVA;
            case speed -> CharacterTemporaryStat.Speed;
            case jump -> CharacterTemporaryStat.Jump;
            case booster -> CharacterTemporaryStat.Booster;
            case mhpR -> CharacterTemporaryStat.MaxHP;
            case mmpR -> CharacterTemporaryStat.MaxMP;
            case thaw -> CharacterTemporaryStat.Thaw;
            case morph -> CharacterTemporaryStat.Morph;
            case expBuff -> CharacterTemporaryStat.ExpBuffRate;
            // The following require special handling
            case defenseAtt -> CharacterTemporaryStat.DefenseAtt; // n = prob, send element type char
            case defenseState -> CharacterTemporaryStat.DefenseState; // n = prob, send stat type
            case respectPimmune -> CharacterTemporaryStat.RespectPImmune; // n = prob
            case respectMimmune -> CharacterTemporaryStat.RespectMImmune; // n = prob
            case itemupbyitem -> CharacterTemporaryStat.ItemUpByItem; // n = prob, 1 : all,  2 : itemCode, 3 : itemRange
            case mesoupbyitem -> CharacterTemporaryStat.MesoUpByItem; // n = prob
            // Reset stats
            case curse -> CharacterTemporaryStat.Curse;
            case darkness -> CharacterTemporaryStat.Darkness;
            case poison -> CharacterTemporaryStat.Poison;
            case seal -> CharacterTemporaryStat.Seal;
            case weakness -> CharacterTemporaryStat.Weakness;
            default -> null;
        };
    }

    public static boolean isIgnored(String name) {
        return name.matches("[0-9]+") || ignoredTypes.contains(name);
    }

    public static ItemSpecType fromName(String name) {
        return nameMap.get(name);
    }
}
