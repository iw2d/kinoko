package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Rect;

import java.util.*;

public final class StaticSkillInfo implements SkillInfo {
    private final int id;
    private final int maxLevel;
    private final boolean invisible;
    private final boolean psd;
    private final List<Integer> psdSkills;
    private final Map<SkillStat, List<Integer>> stats;
    private final Rect rect;

    public StaticSkillInfo(int id, int maxLevel, boolean invisible, boolean psd, List<Integer> psdSkills, Map<SkillStat, List<Integer>> stats, Rect rect) {
        this.id = id;
        this.maxLevel = maxLevel;
        this.invisible = invisible;
        this.psd = psd;
        this.psdSkills = psdSkills;
        this.stats = stats;
        this.rect = rect;
    }

    public Map<SkillStat, List<Integer>> getStats() {
        return stats;
    }

    @Override
    public int getSkillId() {
        return id;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public boolean isInvisible() {
        return invisible;
    }

    @Override
    public boolean isPsd() {
        return psd;
    }

    @Override
    public List<Integer> getPsdSkills() {
        return psdSkills;
    }

    @Override
    public int getValue(SkillStat stat, int slv) {
        return stats.get(stat).get(slv - 1);
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public String toString() {
        return "StaticSkillInfo[" +
                "id=" + id + ", " +
                "maxLevel=" + maxLevel + ", " +
                "invisible=" + invisible + ", " +
                "psd=" + psd + ", " + ']';
    }

    public static StaticSkillInfo from(int skillId, WzListProperty skillProp) throws ProviderError {
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
                                stats.put(stat, new ArrayList<>());
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
        final List<Integer> psdSkills = new ArrayList<>();
        if (skillProp.get("psdSkill") instanceof WzListProperty psdProp) {
            for (var entry : psdProp.getItems().entrySet()) {
                psdSkills.add(Integer.parseInt(entry.getKey()));
            }
        }
        return new StaticSkillInfo(
                skillId,
                maxLevel,
                WzProvider.getInteger(skillProp.get("invisible"), 0) != 0,
                WzProvider.getInteger(skillProp.get("psd"), 0) != 0,
                Collections.unmodifiableList(psdSkills),
                stats,
                rect
        );
    }
}
