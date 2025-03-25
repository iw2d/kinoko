package kinoko.provider.item;

import java.util.HashMap;
import java.util.Map;

public enum ItemInfoType {
    cash,
    price,
    unitPrice,
    slotMax,
    durability,
    only,
    tradBlock, // [sic]
    dropBlock,
    tradeBlock,
    tragetBlock, // [sic]
    pickUpBlock,
    tradeAvailable,
    equipTradeBlock,
    scanTradeBlock,
    accountSharable,
    timeLimited,
    noExpend,
    notExtend,
    notSale,
    onlyEquip,
    quest,
    questId,
    uiData,
    mobPotion,
    maxLevel,
    setItemID,

    // Pet
    life,
    hungry,
    chatBalloon,
    nameTag,
    permanent,
    consumeHP,
    consumeMP,
    pickupItem,
    sweepForDrop,
    evol,

    incSTR,
    incDEX,
    incINT,
    incLUK,
    incHP,
    incMP,
    incMHP,
    incMHPr,
    incMMP,
    incMMPr,
    incMaxHP,
    incMaxMP,
    incPAD,
    incMAD,
    incPDD,
    incMDD,
    incACC,
    incEVA,
    incCraft,
    incSpeed,
    incJump,
    incSwim,
    incFatigue,
    incIUC,
    incLEV,
    incReqLevel,
    incRandVol,
    incPERIOD,

    success,
    cursed,
    recover,
    randstat,
    preventslip,
    warmsupport,

    // Elemental Wand/Staff - unused in v95
    elemDefault,
    incRMAF,
    incRMAI,
    incRMAL,
    incRMAS,

    reqSTR,
    reqDEX,
    reqINT,
    reqLUK,
    reqLEVEL,
    reqPOP,
    reqCUC,
    reqRUC,
    reqJob,
    reqLevel,
    reqSkillLevel,
    masterLevel,
    skill,
    reqQuestOnProgress,
    enchantCategory,
    tuc,
    IUCMax,
    setKey,
    addition,
    npc,
    keywordEffect,
    stateChangeItem, // weather effect
    lt, // consume effect
    rb,
    lv, // monster crystal level
    randOption, // black crystal
    randStat, // dark crystal
    time,

    recoveryHP,
    recoveryMP,
    incAllStat,
    incPVPDamage,
    Option;

    private static final Map<String, ItemInfoType> nameMap;

    static {
        nameMap = new HashMap<>();
        for (ItemInfoType type : values()) {
            nameMap.put(type.name(), type);
        }
    }

    public static boolean isIgnored(String name) {
        return !nameMap.containsKey(name);
    }

    public static ItemInfoType fromName(String name) {
        return nameMap.get(name);
    }
}
