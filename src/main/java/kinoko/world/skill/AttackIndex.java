package kinoko.world.skill;

public enum AttackIndex {
    // AttackIndex
    MOB_PHYSICAL(0),
    MOB_MAGIC(-1),
    COUNTER(-2),
    OBSTACLE(-3),
    STAT(-4);

    private final int value;

    AttackIndex(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static AttackIndex getByValue(int value) {
        for (AttackIndex type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
