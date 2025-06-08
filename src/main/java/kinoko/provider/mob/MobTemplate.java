package kinoko.provider.mob;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.skill.ElementAttribute;
import kinoko.provider.wz.serialize.WzCanvas;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.provider.wz.serialize.WzUol;
import kinoko.util.Util;

import java.util.*;

public final class MobTemplate {
    private final int id;
    private final int level;
    private final int exp;
    private final int maxHp;
    private final int maxMp;
    private final int pad;
    private final int pdr;
    private final int mad;
    private final int mdr;
    private final int acc;
    private final int eva;
    private final int hpRecovery;
    private final int mpRecovery;
    private final int fixedDamage;
    private final int removeAfter;
    private final int dropItemPeriod;
    private final int hpTagColor;
    private final int hpTagBgColor;
    private final boolean boss;
    private final boolean noFlip;
    private final boolean pickUpDrop;
    private final boolean firstAttack;
    private final boolean damagedByMob;
    private final boolean onlyNormalAttack;
    private final Map<Integer, MobAttack> attacks;
    private final Map<Integer, MobSkill> skills;
    private final Map<ElementAttribute, DamagedAttribute> damagedElemAttr;
    private final Set<Integer> damagedBySkill;
    private final List<Integer> revives;
    private final int reviveDelay;

    public MobTemplate(int id, int level, int exp, int maxHp, int maxMp, int pad, int pdr, int mad, int mdr, int acc, int eva, int hpRecovery, int mpRecovery, int fixedDamage, int removeAfter, int dropItemPeriod, int hpTagColor, int hpTagBgColor, boolean boss, boolean noFlip, boolean pickUpDrop, boolean firstAttack, boolean damagedByMob, boolean onlyNormalAttack, Map<Integer, MobAttack> attacks, Map<Integer, MobSkill> skills, Map<ElementAttribute, DamagedAttribute> damagedElemAttr, Set<Integer> damagedBySkill, List<Integer> revives, int reviveDelay) {
        this.id = id;
        this.level = level;
        this.exp = exp;
        this.maxHp = maxHp;
        this.maxMp = maxMp;
        this.pad = pad;
        this.pdr = pdr;
        this.mad = mad;
        this.mdr = mdr;
        this.acc = acc;
        this.eva = eva;
        this.hpRecovery = hpRecovery;
        this.mpRecovery = mpRecovery;
        this.fixedDamage = fixedDamage;
        this.removeAfter = removeAfter;
        this.dropItemPeriod = dropItemPeriod;
        this.hpTagColor = hpTagColor;
        this.hpTagBgColor = hpTagBgColor;
        this.boss = boss;
        this.noFlip = noFlip;
        this.pickUpDrop = pickUpDrop;
        this.firstAttack = firstAttack;
        this.damagedByMob = damagedByMob;
        this.onlyNormalAttack = onlyNormalAttack;
        this.attacks = attacks;
        this.skills = skills;
        this.damagedElemAttr = damagedElemAttr;
        this.damagedBySkill = damagedBySkill;
        this.revives = revives;
        this.reviveDelay = reviveDelay;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public int getPad() {
        return pad;
    }

    public int getPdr() {
        return pdr;
    }

    public int getMad() {
        return mad;
    }

    public int getMdr() {
        return mdr;
    }

    public int getAcc() {
        return acc;
    }

    public int getEva() {
        return eva;
    }

    public int getHpRecovery() {
        return hpRecovery;
    }

    public int getMpRecovery() {
        return mpRecovery;
    }

    public int getFixedDamage() {
        return fixedDamage;
    }

    public int getRemoveAfter() {
        return removeAfter;
    }

    public int getDropItemPeriod() {
        return dropItemPeriod;
    }

    public int getHpTagColor() {
        return hpTagColor;
    }

    public int getHpTagBgColor() {
        return hpTagBgColor;
    }

    public boolean isBoss() {
        return boss;
    }

    public boolean isNoFlip() {
        return noFlip;
    }

    public boolean isPickUpDrop() {
        return pickUpDrop;
    }

    public boolean isFirstAttack() {
        return firstAttack;
    }

    public boolean isDamagedByMob() {
        return damagedByMob;
    }

    public boolean isOnlyNormalAttack() {
        return onlyNormalAttack;
    }

    public boolean isVulnerableTo(int skillId) {
        if (onlyNormalAttack && skillId != 0) {
            return false;
        }
        if (damagedBySkill.isEmpty()) {
            return true;
        }
        return damagedBySkill.contains(skillId);
    }

    public Map<Integer, MobAttack> getAttacks() {
        return attacks;
    }

    public Optional<MobAttack> getAttack(int attackIndex) {
        return Optional.ofNullable(attacks.get(attackIndex));
    }

    public Map<Integer, MobSkill> getSkills() {
        return skills;
    }

    public Optional<MobSkill> getSkill(int skillId) {
        return Optional.ofNullable(skills.get(skillId));
    }

    public Map<ElementAttribute, DamagedAttribute> getDamagedElemAttr() {
        return damagedElemAttr;
    }

    public Set<Integer> getDamagedBySkill() {
        return damagedBySkill;
    }

    public List<Integer> getRevives() {
        return revives;
    }

    public int getReviveDelay() {
        return reviveDelay;
    }

    @Override
    public String toString() {
        return "MobTemplate{" +
                "id=" + id +
                ", level=" + level +
                ", exp=" + exp +
                ", maxHp=" + maxHp +
                ", maxMp=" + maxMp +
                ", pad=" + pad +
                ", pdr=" + pdr +
                ", mad=" + mad +
                ", mdr=" + mdr +
                ", acc=" + acc +
                ", eva=" + eva +
                ", hpRecovery=" + hpRecovery +
                ", mpRecovery=" + mpRecovery +
                ", fixedDamage=" + fixedDamage +
                ", removeAfter=" + removeAfter +
                ", dropItemPeriod=" + dropItemPeriod +
                ", hpTagColor=" + hpTagColor +
                ", hpTagBgColor=" + hpTagBgColor +
                ", boss=" + boss +
                ", noFlip=" + noFlip +
                ", pickUpDrop=" + pickUpDrop +
                ", firstAttack=" + firstAttack +
                ", damagedByMob=" + damagedByMob +
                ", onlyNormalAttack=" + onlyNormalAttack +
                ", attacks=" + attacks +
                ", skills=" + skills +
                ", damagedElemAttr=" + damagedElemAttr +
                ", damagedBySkill=" + damagedBySkill +
                ", revives=" + revives +
                ", reviveDelay=" + reviveDelay +
                '}';
    }

    public static MobTemplate from(int mobId, WzProperty mobProp, WzProperty infoProp) throws ProviderError {
        int level = 0;
        int exp = 0;
        int maxHP = 0;
        int maxMP = 0;
        int pad = 0;
        int pdr = 0;
        int mad = 0;
        int mdr = 0;
        int acc = 0;
        int eva = 0;
        int hpRecovery = 0;
        int mpRecovery = 0;
        int fixedDamage = 0;
        int removeAfter = 0;
        int dropItemPeriod = 0;
        int hpTagColor = 0;
        int hpTagBgColor = 0;
        boolean boss = false;
        boolean noFlip = false;
        boolean pickUpDrop = false;
        boolean firstAttack = false;
        boolean damagedByMob = false;
        boolean onlyNormalAttack = false;
        final Map<Integer, MobAttack> attacks = new HashMap<>();
        final Map<Integer, MobSkill> skills = new HashMap<>();
        final Map<ElementAttribute, DamagedAttribute> damagedElemAttr = new EnumMap<>(ElementAttribute.class);
        final Set<Integer> damagedBySkill = new HashSet<>();
        final List<Integer> revives = new ArrayList<>();
        // Process attacks
        for (var entry : mobProp.getItems().entrySet()) {
            if (entry.getKey().startsWith("attack")) {
                final int attackIndex = Integer.parseInt(entry.getKey().replace("attack", "")) - 1;
                if (!(entry.getValue() instanceof WzProperty attackProp) ||
                        !(attackProp.get("info") instanceof WzProperty attackInfoProp)) {
                    throw new ProviderError("Failed to resolve attack info for mob : %d", mobId);
                }
                int skillId = 0;
                int skillLevel = 0;
                int conMp = 0;
                int mpBurn = 0;
                boolean magic = false;
                boolean deadlyAttack = false;
                for (var attackInfoEntry : attackInfoProp.getItems().entrySet()) {
                    switch (attackInfoEntry.getKey()) {
                        case "disease" -> {
                            skillId = WzProvider.getInteger(attackInfoEntry.getValue());
                        }
                        case "level" -> {
                            skillLevel = WzProvider.getInteger(attackInfoEntry.getValue());
                        }
                        case "conMP" -> {
                            conMp = WzProvider.getInteger(attackInfoEntry.getValue());
                        }
                        case "mpBurn" -> {
                            mpBurn = WzProvider.getInteger(attackInfoEntry.getValue());
                        }
                        case "magic" -> {
                            magic = WzProvider.getInteger(attackInfoEntry.getValue()) != 0;
                        }
                        case "deadlyAttack" -> {
                            deadlyAttack = WzProvider.getInteger(attackInfoEntry.getValue()) != 0;
                        }
                        default -> {
                            // System.err.printf("Unhandled mob attack info %s in mob %d%n", infoEntry.getKey(), mobId);
                        }
                    }
                }
                attacks.put(attackIndex, new MobAttack(
                        skillId,
                        skillLevel,
                        conMp,
                        mpBurn,
                        magic,
                        deadlyAttack
                ));
            }
        }
        // Process info
        for (var infoEntry : infoProp.getItems().entrySet()) {
            switch (infoEntry.getKey()) {
                case "level" -> {
                    level = WzProvider.getInteger(infoEntry.getValue());
                }
                case "exp" -> {
                    exp = WzProvider.getInteger(infoEntry.getValue());
                }
                case "maxHP" -> {
                    maxHP = WzProvider.getInteger(infoEntry.getValue());
                }
                case "maxMP" -> {
                    maxMP = WzProvider.getInteger(infoEntry.getValue());
                }
                case "PADamage" -> {
                    pad = WzProvider.getInteger(infoEntry.getValue());
                }
                case "PDRate" -> {
                    pdr = WzProvider.getInteger(infoEntry.getValue());
                }
                case "MADamage" -> {
                    mad = WzProvider.getInteger(infoEntry.getValue());
                }
                case "MDRate" -> {
                    mdr = WzProvider.getInteger(infoEntry.getValue());
                }
                case "acc" -> {
                    acc = WzProvider.getInteger(infoEntry.getValue());
                }
                case "eva" -> {
                    eva = WzProvider.getInteger(infoEntry.getValue());
                }
                case "hpRecovery" -> {
                    hpRecovery = WzProvider.getInteger(infoEntry.getValue());
                }
                case "mpRecovery" -> {
                    mpRecovery = WzProvider.getInteger(infoEntry.getValue());
                }
                case "fixedDamage" -> {
                    fixedDamage = WzProvider.getInteger(infoEntry.getValue());
                }
                case "removeAfter" -> {
                    removeAfter = WzProvider.getInteger(infoEntry.getValue());
                }
                case "dropItemPeriod" -> {
                    dropItemPeriod = WzProvider.getInteger(infoEntry.getValue());
                }
                case "hpTagColor" -> {
                    hpTagColor = WzProvider.getInteger(infoEntry.getValue());
                }
                case "hpTagBgcolor" -> {
                    hpTagBgColor = WzProvider.getInteger(infoEntry.getValue());
                }
                case "boss" -> {
                    boss = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "noFlip" -> {
                    noFlip = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "pickUp" -> {
                    pickUpDrop = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "firstAttack" -> {
                    firstAttack = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "damagedByMob" -> {
                    damagedByMob = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "onlyNormalAttack" -> {
                    onlyNormalAttack = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "skill" -> {
                    if (!(infoEntry.getValue() instanceof WzProperty skillEntries)) {
                        throw new ProviderError("Failed to resolve mob skills for mob : %d", mobId);
                    }
                    for (var skillEntry : skillEntries.getItems().entrySet()) {
                        if (!(skillEntry.getValue() instanceof WzProperty skillProp)) {
                            throw new ProviderError("Failed to resolve mob skills for mob : %d", mobId);
                        }
                        final int skillId = WzProvider.getInteger(skillProp.get("skill"));
                        final MobSkillType skillType = MobSkillType.getByValue(skillId);
                        if (skillType == null) {
                            throw new ProviderError("Failed to resolve mob skill : %d", skillId);
                        }
                        final int skillLevel = WzProvider.getInteger(skillProp.get("level"));
                        skills.put(skillId, new MobSkill(
                                skillType,
                                skillId,
                                skillLevel
                        ));
                    }
                }
                case "elemAttr" -> {
                    final String value = WzProvider.getString(infoEntry.getValue());
                    for (int i = 0; i < value.length(); i += 2) {
                        final ElementAttribute elemAttr = ElementAttribute.getByValue(value.charAt(i));
                        final DamagedAttribute damagedAttr = DamagedAttribute.getByValue(value.charAt(i + 1));
                        damagedElemAttr.put(elemAttr, damagedAttr);
                    }
                }
                case "damagedBySelectedSkill" -> {
                    if (!(infoEntry.getValue() instanceof WzProperty skillEntries)) {
                        throw new ProviderError("Failed to resolve damagedBySelectedSkill for mob : %d", mobId);
                    }
                    for (var skillEntry : skillEntries.getItems().entrySet()) {
                        final int skillId = WzProvider.getInteger(skillEntry.getValue());
                        damagedBySkill.add(skillId);
                    }
                }
                case "revive" -> {
                    if (!(infoEntry.getValue() instanceof WzProperty reviveList)) {
                        throw new ProviderError("Failed to resolve revives for mob : %d", mobId);
                    }
                    for (var reviveEntry : reviveList.getItems().entrySet()) {
                        final int reviveId = WzProvider.getInteger(reviveEntry.getValue());
                        revives.add(reviveId); // validate in MobProvider
                    }
                }
                default -> {
                    // System.err.printf("Unhandled info %s in mob %d%n", infoEntry.getKey(), mobId);
                }
            }
        }
        // Process revive delay
        int reviveDelay = 0;
        if (mobId == 9400584) {
            // Death animation for leprechaun is broken
            reviveDelay = 1440;
        } else if (!revives.isEmpty()) {
            if (!(mobProp.get("die1") instanceof WzProperty dieAnimationProp)) {
                throw new ProviderError("Failed to resolve revive delay for mob : %d", mobId);
            }
            reviveDelay += getAnimationDelay(mobId, dieAnimationProp);
        }
        return new MobTemplate(
                mobId,
                level,
                exp,
                maxHP,
                maxMP,
                pad,
                pdr,
                mad,
                mdr,
                acc,
                eva,
                hpRecovery,
                mpRecovery,
                fixedDamage,
                removeAfter,
                dropItemPeriod,
                hpTagColor,
                hpTagBgColor,
                boss,
                noFlip,
                pickUpDrop,
                firstAttack,
                damagedByMob,
                onlyNormalAttack,
                Collections.unmodifiableMap(attacks),
                Collections.unmodifiableMap(skills),
                Collections.unmodifiableMap(damagedElemAttr),
                Collections.unmodifiableSet(damagedBySkill),
                Collections.unmodifiableList(revives),
                reviveDelay
        );
    }

    private static int getAnimationDelay(int mobId, WzProperty animationProp) {
        // Compute frame delays
        int animationDelay = 0;
        final Map<String, Integer> frameDelays = new HashMap<>();
        final List<String> frameUols = new ArrayList<>();
        for (var dieFrameEntry : animationProp.getItems().entrySet()) {
            final String key = dieFrameEntry.getKey();
            if (!Util.isInteger(key)) {
                continue; // speak
            }
            if (dieFrameEntry.getValue() instanceof WzUol dieUolProp) {
                if (!Util.isInteger(dieUolProp.getUol())) {
                    throw new ProviderError("Found relative UOL while resolving revive delay for mob : %d", mobId); // pain to implement
                }
                frameUols.add(dieUolProp.getUol());
                continue;
            }
            if (!(dieFrameEntry.getValue() instanceof WzCanvas dieCanvasProp)) {
                throw new ProviderError("Failed to resolve die animation frame for mob : %d", mobId);
            }
            final int delay = WzProvider.getInteger(dieCanvasProp.getProperty().get("delay"), 0);
            frameDelays.put(key, delay);
            animationDelay += delay;
        }
        // Add up delays from UOLs
        for (String uol : frameUols) {
            animationDelay += frameDelays.get(uol);
        }
        return animationDelay;
    }
}
