package kinoko.world.field.summoned;

public enum SummonedMoveAbility {
    // MOVEABILITY
    STOP(0),
    WALK(1),
    WALK_RANDOM(2),
    JUMP(3),
    FLY(4),
    FLY_RANDOM(5),
    ESCORT(6);

    private final int value;

    SummonedMoveAbility(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static SummonedMoveAbility getByValue(int value) {
        for (SummonedMoveAbility type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
