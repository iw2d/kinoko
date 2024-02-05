package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.provider.wz.property.WzVectorProperty;
import kinoko.util.Rect;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class StaticSkillInfo implements SkillInfo {
    private final int id;
    private final int maxLevel;
    private final boolean psd;
    private final boolean invisible;
    private final Map<SkillStat, List<Integer>> stats;
    private final Rect rect;

    public StaticSkillInfo(int id, int maxLevel, boolean psd, boolean invisible, Map<SkillStat, List<Integer>> stats, Rect rect) {
        this.id = id;
        this.maxLevel = maxLevel;
        this.psd = psd;
        this.invisible = invisible;
        this.stats = stats;
        this.rect = rect;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public boolean isPsd() {
        return psd;
    }

    @Override
    public boolean isInvisible() {
        return invisible;
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
                "psd=" + psd + ", " +
                "invisible=" + invisible + ']';
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
                            final WzVectorProperty lt = statProp.get("lt");
                            final WzVectorProperty rb = statProp.get("rb");
                            rect = new Rect(
                                    lt.getX(),
                                    lt.getY(),
                                    rb.getX(),
                                    rb.getY()
                            );
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
        return new StaticSkillInfo(
                skillId,
                maxLevel,
                WzProvider.getInteger(skillProp.get("psd")) != 0,
                WzProvider.getInteger(skillProp.get("invisible")) != 0,
                stats,
                rect
        );
    }
}
