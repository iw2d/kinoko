package kinoko.provider.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum ItemOptionStat {
    prop,
    time,
    incSTR,
    incDEX,
    incINT,
    incLUK,
    HP,
    MP,
    incMHP,
    incMMP,
    incACC,
    incEVA,
    incSpeed,
    incJump,
    incPAD,
    incMAD,
    incPDD,
    incMDD,

    incSTRr,
    incDEXr,
    incINTr,
    incLUKr,
    incMHPr,
    incMMPr,
    incACCr,
    incEVAr,
    incPADr,
    incMADr,
    incPDDr,
    incMDDr,
    incCr,

    incCDr,
    incMAMr,
    incSkill,
    incAllskill,
    RecoveryHP,
    RecoveryMP,
    RecoveryUP,
    mpconReduce,
    mpRestore,
    ignoreTargetDEF,
    ignoreDAM,
    ignoreDAMr,
    incDAMr,
    DAMreflect,
    attackType,
    incMesoProp,
    incRewardProp,
    level,
    boss,
    face;

    private static final Map<String, ItemOptionStat> nameMap;
    private static final Set<String> ignoredTypes;

    static {
        nameMap = new HashMap<>();
        for (ItemOptionStat type : values()) {
            nameMap.put(type.name(), type);
        }
        ignoredTypes = Set.of(
                face.name()
        );
    }

    public static boolean isIgnored(String name) {
        return ignoredTypes.contains(name);
    }

    public static ItemOptionStat fromName(String name) {
        return nameMap.get(name);
    }
}
