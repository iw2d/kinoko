package kinoko.provider.skill;

import java.util.HashMap;
import java.util.Map;

// SKILLLEVELDATA::LoadLevelData
public enum SkillStat {
    // SKILL -----------------------------------------------------------------------------------------------------------
    x,
    y,
    z,
    u,
    v,
    w,
    t,
    prop,
    subProp,
    time,
    subTime,
    cooltime,
    range,
    damage,
    damagepc,
    fixdamage,
    attackCount,
    mobCount,
    dot,
    dotInterval,
    dotTime,
    hpCon,
    mpCon,
    bulletCount,
    bulletConsume,
    itemCon,
    itemConNo,
    itemConsume,
    moneyCon,
    selfDestruction,

    // STAT ------------------------------------------------------------------------------------------------------------
    hp,
    mp,
    pad,
    mad,
    pdd,
    mdd,
    acc,
    eva,
    er,
    cr,
    criticaldamageMin,
    criticaldamageMax,
    mastery,
    morph,
    speed,
    jump,
    psdSpeed,
    psdJump,

    emhp,
    emmp,
    epad,
    emad,
    epdd,
    emdd,
    padX,
    madX,

    mhpR,
    mmpR,
    ar,
    pdr,
    mdr,
    accR,
    evaR,
    padR,
    madR,
    pddR,
    mddR,
    asrR,
    terR,
    damR,
    ignoreMobDamR,
    ignoreMobpdpR,
    mesoR,
    expR,
    overChargeR,
    disCountR,

    // SPECIAL ---------------------------------------------------------------------------------------------------------
    lt,
    rb,
    hs,
    hit,
    ball,
    action,
    dateExpire,
    maxLevel,
    interval;

    private static final Map<String, SkillStat> nameMap = new HashMap<>();

    static {
        for (SkillStat stat : values()) {
            nameMap.put(stat.name(), stat);
        }
    }

    public static SkillStat fromName(String name) {
        return nameMap.get(name);
    }
}
