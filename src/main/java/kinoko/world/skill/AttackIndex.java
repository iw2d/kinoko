package kinoko.world.skill;

public enum AttackIndex {
    // AttackIndex
    Mob_Physical(0),
    Mob_Magic(-1),
    Counter(-2),
    Obstacle(-3),
    Stat(-4);

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
