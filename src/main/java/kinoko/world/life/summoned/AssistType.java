package kinoko.world.life.summoned;

public enum AssistType {
    // ASSIST
    NONE(0),
    ATTACK(1),
    HEAL(2),
    ATTACK_EX(3),
    SUMMON(4),
    ATTACK_MANUAL(5),
    ATTACK_COUNTER(6);

    private final int value;

    AssistType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
