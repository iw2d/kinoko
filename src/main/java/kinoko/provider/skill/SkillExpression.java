package kinoko.provider.skill;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public final class SkillExpression {
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

    private final Expression expression;

    public SkillExpression(Expression expression) {
        this.expression = expression;
    }

    public int evaluate(int x) {
        return (int) expression.setVariable("x", x).evaluate();
    }

    public static SkillExpression from(String string) {
        final Expression expression = new ExpressionBuilder(string)
                .functions(ceil, floor)
                .variables("x")
                .build();
        return new SkillExpression(expression);
    }
}
