package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.provider.wz.property.WzVectorProperty;
import kinoko.util.Rect;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class ComputedSkillInfo implements SkillInfo {
    private static final Function ceil = new Function("u", 1) {
        @Override
        public double apply(double... doubles) {
            return Math.ceil(doubles[0]);
        }
    };
    private static final Function floor = new Function("d", 1) {
        @Override
        public double apply(double... doubles) {
            return Math.floor(doubles[0]);
        }
    };

    private final int id;
    private final int maxLevel;
    private final boolean psd;
    private final boolean invisible;
    private final Map<SkillStat, Expression> stats;
    private final Rect rect;

    public ComputedSkillInfo(int id, int maxLevel, boolean psd, boolean invisible, Map<SkillStat, Expression> stats, Rect rect) {
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
        return (int) stats.get(stat)
                .setVariable("x", slv)
                .evaluate();
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    public static ComputedSkillInfo from(int skillId, WzListProperty skillProp) throws ProviderError {
        final Map<SkillStat, Expression> stats = new EnumMap<>(SkillStat.class);
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
                        final WzVectorProperty lt = commonProps.get("lt");
                        final WzVectorProperty rb = commonProps.get("rb");
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
                        final Expression expression = new ExpressionBuilder(WzProvider.getString(entry.getValue()))
                                .functions(ceil, floor)
                                .variables("x")
                                .build();
                        stats.put(stat, expression);
                    }
                }
            }
        }
        return new ComputedSkillInfo(
                skillId,
                maxLevel,
                WzProvider.getInteger(skillProp.get("psd")) != 0,
                WzProvider.getInteger(skillProp.get("invisible")) != 0,
                Collections.unmodifiableMap(stats),
                rect
        );
    }
}
