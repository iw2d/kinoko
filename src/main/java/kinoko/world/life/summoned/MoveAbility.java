package kinoko.world.life.summoned;

public enum MoveAbility {
    // MOVEABILITY
    STOP(0),
    WALK(1),
    WALK_RANDOM(2),
    JUMP(3),
    FLY(4),
    FLY_RANDOM(5),
    ESCORT(6);

    private final int value;

    MoveAbility(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
