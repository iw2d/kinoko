package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.provider.wz.property.WzVectorProperty;
import kinoko.util.Rect;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.*;

public record SkillInfo(int id, Map<SkillStat, Expression> stats, List<Rect> rects, int maxLevel, boolean psd,
                        boolean invisible) {
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

    public int getValue(SkillStat stat, int slv) {
        return (int) stats.get(stat)
                .setVariable("x", slv)
                .evaluate();
    }

    public static SkillInfo from(int skillId, WzListProperty skillProp) throws ProviderError {
        final Map<SkillStat, Expression> stats = new EnumMap<>(SkillStat.class);
        final List<Rect> rects = new ArrayList<>();
        int maxLevel = 0;
        if (skillProp.get("common") instanceof WzListProperty commonProps) {
            for (var entry : commonProps.getItems().entrySet()) {
                final SkillStat stat = SkillStat.fromName(entry.getKey());
                switch (stat) {
                    case maxLevel -> {
                        maxLevel = getInteger(entry.getValue());
                    }
                    case lt -> {
                        final WzVectorProperty lt = (WzVectorProperty) commonProps.get("lt");
                        final WzVectorProperty rb = (WzVectorProperty) commonProps.get("rb");
                        rects.add(new Rect(
                                lt.getX(),
                                lt.getY(),
                                rb.getX(),
                                rb.getY()
                        ));
                    }
                    case action, rb -> {
                        // rb handled by lt
                    }
                    default -> {
                        final Expression expression = new ExpressionBuilder(getString(entry.getValue()))
                                .functions(ceil, floor)
                                .variables("x")
                                .build();
                        stats.put(stat, expression);
                    }
                }
            }
        }
        return new SkillInfo(
                skillId,
                Collections.unmodifiableMap(stats),
                Collections.unmodifiableList(rects),
                maxLevel,
                getInteger(skillProp.get("psd")) != 0,
                getInteger(skillProp.get("invisible")) != 0
        );
    }

    private static int getInteger(Object object) {
        if (object instanceof Integer value) {
            return value;
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    private static String getString(Object object) {
        if (object instanceof Integer value) {
            return String.valueOf(value);
        } else if (object instanceof String value) {
            return value;
        }
        return "";
    }
}
