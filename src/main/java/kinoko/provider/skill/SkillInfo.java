package kinoko.provider.skill;

import kinoko.meta.SkillId;
import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.util.Crc32;
import kinoko.util.Rect;
import kinoko.world.field.summoned.SummonedActionType;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.skill.ActionType;
import kinoko.world.skill.SkillConstants;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.util.*;
import java.util.stream.IntStream;

public final class SkillInfo {
    private final SkillId skillId;
    private final int maxLevel;
    private final int masterLevel;
    private final boolean invisible;
    private final boolean combatOrders;
    private final boolean psd;
    private final List<Integer> psdSkills;
    private final List<ActionType> action;
    private final ActionType statAction;
    private final Map<SkillStat, List<Integer>> stats;
    private final List<Rect> rects;
    private final ElementAttribute elemAttr;
    private final Map<SummonedActionType, SummonedAttackInfo> summonedAttack;

    private final int skillEntryCrc;
    private final List<Integer> levelDataCrc;

    public SkillInfo(SkillId skillId, int maxLevel, int masterLevel, boolean invisible, boolean combatOrders, boolean psd, List<Integer> psdSkills, List<ActionType> action, ActionType statAction, Map<SkillStat, List<Integer>> stats, List<Rect> rects, ElementAttribute elemAttr, Map<SummonedActionType, SummonedAttackInfo> summonedAttack) {
        this.skillId = skillId;
        this.maxLevel = maxLevel;
        this.masterLevel = masterLevel;
        this.invisible = invisible;
        this.combatOrders = combatOrders;
        this.psd = psd;
        this.psdSkills = psdSkills;
        this.action = action;
        this.statAction = statAction;
        this.stats = stats;
        this.rects = rects;
        this.elemAttr = elemAttr;
        this.summonedAttack = summonedAttack;

        // Compute Skill CRC
        this.skillEntryCrc = Crc32.computeCrcSkillEntry(this);
        this.levelDataCrc = IntStream.rangeClosed(0, maxLevel + (combatOrders ? 2 : 0))
                .map((slv) -> Crc32.computeCrcSkillLevelData(this, slv))
                .boxed()
                .toList();
    }

    public SkillId getSkillId() {
        return skillId;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public boolean isCombatOrders() {
        return combatOrders;
    }

    public boolean isPsd() {
        return psd;
    }

    public List<Integer> getPsdSkills() {
        return psdSkills;
    }

    public int getValue(SkillStat stat, int slv) {
        final List<Integer> levelData = stats.get(stat);
        if (levelData == null || slv < 0 || slv >= levelData.size()) {
            return 0;
        }
        return levelData.get(slv);
    }

    public Rect getRect(int slv) {
        return rects.get(slv);
    }

    public List<ActionType> getAction() {
        return action;
    }

    public ActionType getStatAction() {
        return statAction;
    }

    public ElementAttribute getElemAttr() {
        return elemAttr;
    }

    public Map<SummonedActionType, SummonedAttackInfo> getSummonedAttack() {
        return summonedAttack;
    }

    public int getSkillEntryCrc() {
        return skillEntryCrc;
    }

    public int getLevelDataCrc(int slv) {
        if (slv < 0 || slv >= levelDataCrc.size()) {
            return 0;
        }
        return levelDataCrc.get(slv);
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public int getDuration(int slv) {
        return getValue(SkillStat.time, slv) * 1000;
    }

    public int getBulletCon(int slv) {
        if (SkillConstants.isShootSkillNotConsumingBullet(getSkillId())) {
            return 0;
        }
        return Math.max(getValue(SkillStat.bulletConsume, slv), getValue(SkillStat.bulletCount, slv));
    }

    public int getHpCon(User user, int slv, int keyDown) {
        final SkillId skillId = getSkillId();
        if (skillId == SkillId.DK_SACRIFICE || skillId == SkillId.DK_DRAGON_ROAR || skillId == SkillId.BRAWLER_MP_RECOVERY) {
            return user.getMaxHp() * getValue(SkillStat.x, slv) / 100;
        } else if (skillId == SkillId.DB5_FINAL_CUT) {
            final int percentage = getValue(SkillStat.x, slv) * keyDown / SkillConstants.getMaxGaugeTime(skillId);
            return user.getMaxHp() * percentage / 100;
        }
        return getValue(SkillStat.hpCon, slv);
    }

    public int getMpCon(User user, int slv) {
        // CSkillInfo::CheckConsumeForActiveSkill
        int mpCon = getValue(SkillStat.mpCon, slv);
        // Check element amplification
        final SkillId amplificationSkill = SkillConstants.getAmplificationSkill(user.getJob());
        if (!amplificationSkill.isNone()) {
            final int incMpCon = user.getSkillStatValue(amplificationSkill, SkillStat.x);
            if (incMpCon > 0) {
                mpCon = incMpCon * mpCon / 100;
            }
        }
        // Check CTS affecting mpCon
        final SecondaryStat ss = user.getSecondaryStat();
        if (ss.hasOption(CharacterTemporaryStat.Infinity)) {
            mpCon = 0;
        }
        if (ss.hasOption(CharacterTemporaryStat.Concentration)) {
            final int percentage = 100 - ss.getOption(CharacterTemporaryStat.Concentration).nOption;
            mpCon = (int) (percentage * mpCon / 100.0 + 0.99);
        }
        if (SkillConstants.isTeleportSkill(getSkillId()) && ss.hasOption(CharacterTemporaryStat.TeleportMasteryOn)) {
            mpCon += ss.getOption(CharacterTemporaryStat.TeleportMasteryOn).nOption;
        }
        return mpCon;
    }

    @Override
    public String toString() {
        return "SkillInfo{" +
                "skillId=" + skillId +
                ", maxLevel=" + maxLevel +
                ", invisible=" + invisible +
                ", combatOrders=" + combatOrders +
                ", psd=" + psd +
                ", psdSkills=" + psdSkills +
                ", action=" + action +
                ", statAction=" + statAction +
                ", stats=" + stats +
                ", rects=" + rects +
                ", elemAttr=" + elemAttr +
                ", summonedAttack=" + summonedAttack +
                ", skillEntryCrc=" + skillEntryCrc +
                ", levelDataCrc=" + levelDataCrc +
                '}';
    }

    public static SkillInfo from(int skillId, WzProperty skillProp) throws ProviderError {
        if (skillProp.get("level") instanceof WzProperty) {
            return fromStatic(skillId, skillProp);
        } else {
            return fromComputed(skillId, skillProp);
        }
    }

    private static SkillInfo fromStatic(int skillId, WzProperty skillProp) throws ProviderError {
        final Map<SkillStat, Map<Integer, Integer>> statMap = new EnumMap<>(SkillStat.class);
        final Map<Integer, Rect> rectMap = new HashMap<>();
        ActionType statAction = null;
        int maxLevel = 0;
        if (skillProp.get("level") instanceof WzProperty levelProps) {
            for (int slv = 1; slv < Integer.MAX_VALUE; slv++) {
                if (!(levelProps.get(String.valueOf(slv)) instanceof WzProperty statProp)) {
                    maxLevel = slv - 1;
                    break;
                }
                for (var entry : statProp.getItems().entrySet()) {
                    final SkillStat stat = SkillStat.fromName(entry.getKey());
                    if (stat == null) {
                        // unhandled SkillStats in MobSkill.img
                        continue;
                    }
                    switch (stat) {
                        case action -> {
                            statAction = ActionType.getByName(WzProvider.getString(entry.getValue()));
                        }
                        case lt -> {
                            rectMap.put(slv, WzProvider.getRect(statProp));
                        }
                        case rb, hs, hit, ball, dateExpire -> {
                            // skip; rb is handled by lt
                        }
                        default -> {
                            if (!statMap.containsKey(stat)) {
                                statMap.put(stat, new HashMap<>());
                            }
                            statMap.get(stat).put(slv, WzProvider.getInteger(entry.getValue()));
                        }
                    }
                }
            }
        }
        if (maxLevel == 0) {
            throw new ProviderError("Could not resolve skill max level");
        }
        final Map<SkillStat, List<Integer>> stats = new HashMap<>();
        for (var entry : statMap.entrySet()) {
            final List<Integer> levelData = new ArrayList<>();
            for (int slv = 0; slv <= maxLevel; slv++) {
                levelData.add(entry.getValue().getOrDefault(slv, 0));
            }
            stats.put(entry.getKey(), Collections.unmodifiableList(levelData));
        }
        final List<Rect> rects = new ArrayList<>();
        for (int slv = 0; slv <= maxLevel; slv++) {
            rects.add(rectMap.getOrDefault(slv, null));
        }
        final List<Integer> psdSkills = resolvePsdSkills(skillProp);
        final List<ActionType> action = resolveAction(skillProp);
        final ElementAttribute elemAttr = resolveElemAttr(skillProp);
        final Map<SummonedActionType, SummonedAttackInfo> summonedAttack = resolveSummonedAttack(skillProp);
        return new SkillInfo(
                SkillId.fromValue(skillId),
                maxLevel,
                WzProvider.getInteger(skillProp.get("masterLevel"), 0),
                WzProvider.getInteger(skillProp.get("invisible"), 0) != 0,
                WzProvider.getInteger(skillProp.get("combatOrders"), 0) != 0,
                WzProvider.getInteger(skillProp.get("psd"), 0) != 0,
                Collections.unmodifiableList(psdSkills),
                Collections.unmodifiableList(action),
                statAction,
                Collections.unmodifiableMap(stats),
                Collections.unmodifiableList(rects),
                elemAttr,
                summonedAttack
        );
    }

    private static SkillInfo fromComputed(int skillId, WzProperty skillProp) throws ProviderError {
        final Map<SkillStat, SkillExpression> expressions = new EnumMap<>(SkillStat.class);
        ActionType statAction = null;
        Rect rect = null;
        int maxLevel = 0;
        if (skillProp.get("common") instanceof WzProperty commonProps) {
            for (var entry : commonProps.getItems().entrySet()) {
                final SkillStat stat = SkillStat.fromName(entry.getKey());
                switch (stat) {
                    case maxLevel -> {
                        maxLevel = WzProvider.getInteger(entry.getValue());
                    }
                    case action -> {
                        statAction = ActionType.getByName(WzProvider.getString(entry.getValue()));
                    }
                    case lt -> {
                        rect = WzProvider.getRect(commonProps);
                    }
                    case rb, hs, hit, ball, dateExpire -> {
                        // skip; rb is handled by lt
                    }
                    default -> {
                        expressions.put(stat, SkillExpression.from(WzProvider.getString(entry.getValue())));
                    }
                }
            }
        }
        if (maxLevel == 0) {
            throw new ProviderError("Could not resolve skill max level");
        }
        // Resolve maximum level required
        final boolean combatOrders = WzProvider.getInteger(skillProp.get("combatOrders"), 0) != 0;
        final int statMaxLevel = maxLevel + (combatOrders ? 2 : 0);
        // Compute skill stat values
        final Map<SkillStat, List<Integer>> stats = new EnumMap<>(SkillStat.class);
        for (var entry : expressions.entrySet()) {
            final SkillStat stat = entry.getKey();
            final SkillExpression ex = entry.getValue();
            final List<Integer> levelData = new ArrayList<>();
            for (int i = 0; i <= statMaxLevel; i++) {
                levelData.add(ex.evaluate(i));
            }
            stats.put(stat, Collections.unmodifiableList(levelData));
        }
        final List<Integer> psdSkills = resolvePsdSkills(skillProp);
        final List<ActionType> action = resolveAction(skillProp);
        final ElementAttribute elemAttr = resolveElemAttr(skillProp);
        final Map<SummonedActionType, SummonedAttackInfo> summonedAttack = resolveSummonedAttack(skillProp);
        return new SkillInfo(
                SkillId.fromValue(skillId),
                maxLevel,
                WzProvider.getInteger(skillProp.get("masterLevel"), 0),
                WzProvider.getInteger(skillProp.get("invisible"), 0) != 0,
                WzProvider.getInteger(skillProp.get("combatOrders"), 0) != 0,
                WzProvider.getInteger(skillProp.get("psd"), 0) != 0,
                Collections.unmodifiableList(psdSkills),
                Collections.unmodifiableList(action),
                statAction,
                Collections.unmodifiableMap(stats),
                Collections.nCopies(statMaxLevel + 1, rect),
                elemAttr,
                summonedAttack);
    }

    private static List<Integer> resolvePsdSkills(WzProperty skillProp) {
        final List<Integer> psdSkills = new ArrayList<>();
        if (skillProp.get("psdSkill") instanceof WzProperty psdProp) {
            for (var entry : psdProp.getItems().entrySet()) {
                psdSkills.add(Integer.parseInt(entry.getKey()));
            }
        }
        return psdSkills;
    }

    private static List<ActionType> resolveAction(WzProperty skillProp) {
        final List<ActionType> action = new ArrayList<>();
        if (skillProp.get("action") instanceof WzProperty actionProp) {
            for (var entry : actionProp.getItems().entrySet()) {
                action.add(ActionType.getByName(WzProvider.getString(entry.getValue())));
            }
        }
        return action;
    }

    private static ElementAttribute resolveElemAttr(WzProperty skillProp) throws ProviderError {
        final String elemAttrString = skillProp.get("elemAttr");
        if (elemAttrString != null) {
            if (elemAttrString.length() != 1) {
                throw new ProviderError("Failed to resolve skill element attribute");
            }
            return ElementAttribute.getByValue(elemAttrString.charAt(0));
        } else {
            return ElementAttribute.PHYSICAL;
        }
    }

    private static Map<SummonedActionType, SummonedAttackInfo> resolveSummonedAttack(WzProperty skillProp) {
        final Map<SummonedActionType, SummonedAttackInfo> summonedAttack = new EnumMap<>(SummonedActionType.class);
        if (skillProp.get("summon") instanceof WzProperty summonProp) {
            for (var summonEntry : summonProp.getItems().entrySet()) {
                if (!(summonEntry.getValue() instanceof WzProperty attackProp) ||
                        !(attackProp.get("info") instanceof WzProperty infoProp)) {
                    continue;
                }
                final SummonedActionType actionType = SummonedActionType.getByName(summonEntry.getKey());
                if (actionType == null) {
                    throw new ProviderError("Failed to resolve summoned action type %s", summonEntry.getKey());
                }
                summonedAttack.put(actionType, SummonedAttackInfo.from(infoProp));
            }
        }
        return summonedAttack;
    }
}
