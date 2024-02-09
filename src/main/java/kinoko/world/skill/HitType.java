package kinoko.world.skill;

/**
 * Enum AttackIndex.
 */
public enum HitType {
    MOB_PHYSICAL(0),
    MOB_MAGIC(-1),
    COUNTER(-2),
    OBSTACLE(-3),
    STAT(-4);

    private final int value;

    HitType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HitType getByValue(int value) {
        for (HitType hitType : values()) {
            if (hitType.getValue() == value) {
                return hitType;
            }
        }
        return null;
    }
}
