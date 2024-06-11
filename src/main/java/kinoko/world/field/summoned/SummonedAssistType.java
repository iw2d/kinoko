package kinoko.world.field.summoned;

public enum SummonedAssistType {
    // ASSIST
    NONE(0),
    ATTACK(1),
    HEAL(2),
    ATTACK_EX(3),
    SUMMON(4),
    ATTACK_MANUAL(5),
    ATTACK_COUNTER(6);

    private final int value;

    SummonedAssistType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static SummonedAssistType getByValue(int value) {
        for (SummonedAssistType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
