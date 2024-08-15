package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Rect;
import kinoko.util.Triple;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.util.*;

public final class SkillInfo {
    private final int skillId;
    private final int maxLevel;
    private final boolean invisible;
    private final boolean combatOrders;
    private final boolean psd;
    private final List<Integer> psdSkills;
    private final Map<SkillStat, List<Integer>> stats;
    private final Rect rect;
    private final ElementAttribute elemAttr;
    private final List<Integer> crc;

    public SkillInfo(int skillId, int maxLevel, boolean invisible, boolean combatOrders, boolean psd, List<Integer> psdSkills, Map<SkillStat, List<Integer>> stats, Rect rect, ElementAttribute elemAttr, List<Integer> crc) {
        this.skillId = skillId;
        this.maxLevel = maxLevel;
        this.invisible = invisible;
        this.combatOrders = combatOrders;
        this.psd = psd;
        this.psdSkills = psdSkills;
        this.stats = stats;
        this.rect = rect;
        this.elemAttr = elemAttr;
        this.crc = crc;
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

    public ElementAttribute getElemAttr() {
        return elemAttr;
    }

    public int getCrc(int slv) {
        if (slv < 0 || slv >= crc.size()) {
            return 0;
        }
        return crc.get(slv);
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
                ", stats=" + stats +
                ", rect=" + rect +
                ", elemAttr=" + elemAttr +
                ", crc=" + crc +
                '}';
    }

    public static SkillInfo from(int skillId, WzListProperty skillProp) throws ProviderError {
        // Resolve skill stats
        final Triple<Map<SkillStat, List<Integer>>, Integer, Rect> triple = resolveStats(skillProp);
        final Map<SkillStat, List<Integer>> stats = triple.getFirst();
        final int maxLevel = triple.getSecond();
        final Rect rect = triple.getThird();
        // Resolve psd skills
        final List<Integer> psdSkills = new ArrayList<>();
        if (skillProp.get("psdSkill") instanceof WzListProperty psdProp) {
            for (var entry : psdProp.getItems().entrySet()) {
                psdSkills.add(Integer.parseInt(entry.getKey()));
            }
        }
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
        // Compute CRC
        final List<Integer> crc = new ArrayList<>();
        // TODO
        return new SkillInfo(
                skillId,
                maxLevel,
                WzProvider.getInteger(skillProp.get("invisible"), 0) != 0,
                WzProvider.getInteger(skillProp.get("combatOrders"), 0) != 0,
                WzProvider.getInteger(skillProp.get("psd"), 0) != 0,
                Collections.unmodifiableList(psdSkills),
                Collections.unmodifiableMap(stats),
                rect,
                elemAttr,
                Collections.unmodifiableList(crc)
        );
    }

    private static Triple<Map<SkillStat, List<Integer>>, Integer, Rect> resolveStats(WzListProperty skillProp) throws ProviderError {
        if (skillProp.get("level") instanceof WzListProperty) {
            return resolveStaticStats(skillProp);
        } else {
            return resolveComputedStats(skillProp);
        }
    }

    private static Triple<Map<SkillStat, List<Integer>>, Integer, Rect> resolveStaticStats(WzListProperty skillProp) throws ProviderError {
        final Map<SkillStat, List<Integer>> stats = new EnumMap<>(SkillStat.class);
        int maxLevel = 0;
        Rect rect = null;
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
                        case lt -> {
                            rect = WzProvider.getRect(statProp);
                        }
                        case rb, hs, hit, ball, action, dateExpire -> {
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
            throw new ProviderError("Could not resolve static skill info");
        }
        return Triple.of(
                stats,
                maxLevel,
                rect
        );
    }

    private static Triple<Map<SkillStat, List<Integer>>, Integer, Rect> resolveComputedStats(WzListProperty skillProp) throws ProviderError {
        final Map<SkillStat, SkillExpression> expressions = new EnumMap<>(SkillStat.class);
        Rect rect = null;
        int maxLevel = 0;
        if (skillProp.get("common") instanceof WzListProperty commonProps) {
            for (var entry : commonProps.getItems().entrySet()) {
                final SkillStat stat = SkillStat.fromName(entry.getKey());
                switch (stat) {
                    case maxLevel -> {
                        maxLevel = WzProvider.getInteger(entry.getValue());
                    }
                    case lt -> {
                        rect = WzProvider.getRect(commonProps);
                    }
                    case rb, hs, hit, ball, action, dateExpire -> {
                        // skip; rb is handled by lt
                    }
                    default -> {
                        expressions.put(stat, SkillExpression.from(WzProvider.getString(entry.getValue())));
                    }
                }
            }
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
        return Triple.of(
                stats,
                maxLevel,
                rect
        );
    }
}
