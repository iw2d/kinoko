package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Crc32;
import kinoko.util.Rect;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.skill.ActionType;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.util.*;
import java.util.stream.IntStream;

public final class SkillInfo {
    private final int skillId;
    private final int maxLevel;
    private final boolean invisible;
    private final boolean combatOrders;
    private final boolean psd;
    private final List<Integer> psdSkills;
    private final List<ActionType> action;
    private final Map<SkillStat, List<Integer>> stats;
    private final ActionType statAction;
    private final Rect rect;
    private final ElementAttribute elemAttr;
    private final List<Integer> levelDataCrc;

    public SkillInfo(int skillId, int maxLevel, boolean invisible, boolean combatOrders, boolean psd, List<Integer> psdSkills, List<ActionType> action, Map<SkillStat, List<Integer>> stats, ActionType statAction, Rect rect, ElementAttribute elemAttr) {
        this.skillId = skillId;
        this.maxLevel = maxLevel;
        this.invisible = invisible;
        this.combatOrders = combatOrders;
        this.psd = psd;
        this.psdSkills = psdSkills;
        this.action = action;
        this.stats = stats;
        this.statAction = statAction;
        this.rect = rect;
        this.elemAttr = elemAttr;
        this.levelDataCrc = IntStream.rangeClosed(0, maxLevel + (combatOrders ? 2 : 0))
                .map((slv) -> Crc32.computeCrc32(this, slv))
                .boxed()
                .toList();
    }

    public int getSkillId() {
        return skillId;
    }

    public int getMaxLevel() {
        return maxLevel;
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

    public Rect getRect() {
        return rect;
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
        final int skillId = getSkillId();
        if (skillId == Warrior.SACRIFICE || skillId == Warrior.DRAGON_ROAR || skillId == Pirate.MP_RECOVERY) {
            return user.getMaxHp() * getValue(SkillStat.x, slv) / 100;
        } else if (skillId == Thief.FINAL_CUT) {
            final int percentage = getValue(SkillStat.x, slv) * keyDown / SkillConstants.getMaxGaugeTime(skillId);
            return user.getMaxHp() * percentage / 100;
        }
        return getValue(SkillStat.hpCon, slv);
    }

    public int getMpCon(User user, int slv) {
        // CSkillInfo::CheckConsumeForActiveSkill
        final int incMpCon = 100 + user.getSkillStatValue(SkillConstants.getAmplificationSkill(user.getJob()), SkillStat.x);
        int mpCon = getValue(SkillStat.mpCon, slv) * incMpCon / 100;
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

    public SkillRecord createRecord() {
        final SkillRecord skillRecord = new SkillRecord(getSkillId());
        skillRecord.setSkillLevel(0);
        skillRecord.setMasterLevel(getMaxLevel());
        return skillRecord;
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
                ", stats=" + stats +
                ", statAction=" + statAction +
                ", rect=" + rect +
                ", elemAttr=" + elemAttr +
                ", levelDataCrc=" + levelDataCrc +
                '}';
    }

    public static SkillInfo from(int skillId, WzListProperty skillProp) throws ProviderError {
        if (skillProp.get("level") instanceof WzListProperty) {
            return fromStatic(skillId, skillProp);
        } else {
            return fromComputed(skillId, skillProp);
        }
    }

    private static SkillInfo fromStatic(int skillId, WzListProperty skillProp) throws ProviderError {
        final Map<SkillStat, List<Integer>> stats = new EnumMap<>(SkillStat.class);
        ActionType statAction = null;
        Rect rect = null;
        int maxLevel = 0;
        if (skillProp.get("level") instanceof WzListProperty levelProps) {
            for (int slv = 1; slv < Integer.MAX_VALUE; slv++) {
                if (!(levelProps.get(String.valueOf(slv)) instanceof WzListProperty statProp)) {
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
                        case maxLevel -> {
                            maxLevel = WzProvider.getInteger(entry.getValue());
                        }
                        case action -> {
                            statAction = ActionType.getByName(WzProvider.getString(entry.getValue()));
                        }
                        case lt -> {
                            rect = WzProvider.getRect(statProp);
                        }
                        case rb, hs, hit, ball, dateExpire -> {
                            // skip; rb is handled by lt
                        }
                        default -> {
                            if (!stats.containsKey(stat)) {
                                final List<Integer> levelData = new ArrayList<>();
                                levelData.add(0); // level 0
                                stats.put(stat, levelData);
                            }
                            stats.get(stat).add(WzProvider.getInteger(entry.getValue()));
                        }
                    }
                }
            }
        }
        if (maxLevel == 0) {
            throw new ProviderError("Could not resolve skill max level");
        }
        // Resolve psd skills
        final List<Integer> psdSkills = new ArrayList<>();
        if (skillProp.get("psdSkill") instanceof WzListProperty psdProp) {
            for (var entry : psdProp.getItems().entrySet()) {
                psdSkills.add(Integer.parseInt(entry.getKey()));
            }
        }
        // Resolve action
        final List<ActionType> action = new ArrayList<>();
        if (skillProp.get("action") instanceof WzListProperty actionProp) {
            for (var entry : actionProp.getItems().entrySet()) {
                action.add(ActionType.getByName(WzProvider.getString(entry.getValue())));
            }
        }
        // Resolve element attribute
        final ElementAttribute elemAttr;
        final String elemAttrString = skillProp.get("elemAttr");
        if (elemAttrString != null) {
            if (elemAttrString.length() != 1) {
                throw new ProviderError("Failed to resolve skill element attribute");
            }
            elemAttr = ElementAttribute.getByValue(elemAttrString.charAt(0));
        } else {
            elemAttr = ElementAttribute.PHYSICAL;
        }
        return new SkillInfo(
                skillId,
                maxLevel,
                WzProvider.getInteger(skillProp.get("invisible"), 0) != 0,
                WzProvider.getInteger(skillProp.get("combatOrders"), 0) != 0,
                WzProvider.getInteger(skillProp.get("psd"), 0) != 0,
                Collections.unmodifiableList(psdSkills),
                Collections.unmodifiableList(action),
                Collections.unmodifiableMap(stats),
                statAction,
                rect,
                elemAttr
        );
    }

    private static SkillInfo fromComputed(int skillId, WzListProperty skillProp) throws ProviderError {
        final Map<SkillStat, SkillExpression> expressions = new EnumMap<>(SkillStat.class);
        ActionType statAction = null;
        Rect rect = null;
        int maxLevel = 0;
        if (skillProp.get("common") instanceof WzListProperty commonProps) {
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
            stats.put(stat, levelData);
        }
        // Resolve psd skills
        final List<Integer> psdSkills = new ArrayList<>();
        if (skillProp.get("psdSkill") instanceof WzListProperty psdProp) {
            for (var entry : psdProp.getItems().entrySet()) {
                psdSkills.add(Integer.parseInt(entry.getKey()));
            }
        }
        // Resolve action
        final List<ActionType> action = new ArrayList<>();
        if (skillProp.get("action") instanceof WzListProperty actionProp) {
            for (var entry : actionProp.getItems().entrySet()) {
                action.add(ActionType.getByName(WzProvider.getString(entry.getValue())));
            }
        }
        // Resolve element attribute
        final ElementAttribute elemAttr;
        final String elemAttrString = skillProp.get("elemAttr");
        if (elemAttrString != null) {
            if (elemAttrString.length() != 1) {
                throw new ProviderError("Failed to resolve skill element attribute");
            }
            elemAttr = ElementAttribute.getByValue(elemAttrString.charAt(0));
        } else {
            elemAttr = ElementAttribute.PHYSICAL;
        }
        return new SkillInfo(
                skillId,
                maxLevel,
                WzProvider.getInteger(skillProp.get("invisible"), 0) != 0,
                WzProvider.getInteger(skillProp.get("combatOrders"), 0) != 0,
                WzProvider.getInteger(skillProp.get("psd"), 0) != 0,
                Collections.unmodifiableList(psdSkills),
                Collections.unmodifiableList(action),
                Collections.unmodifiableMap(stats),
                statAction,
                rect,
                elemAttr
        );
    }
}
