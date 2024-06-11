package kinoko.world.field.summoned;

public enum SummonedActionType {
    // SUMMONEDACT
    STAND(0),
    MOVE(1),
    FLY(2),
    SUMMONED(3),
    ATTACK1(4),
    ATTACK2(5),
    ATTACK_TRIANGLE(6),
    SKILL1(7),
    SKILL2(8),
    SKILL3(9),
    SKILL4(10),
    SKILL5(11),
    SKILL6(12),
    HEAL(13),
    SUBSUMMON(14),
    HIT(15),
    DIE(16),
    SAY(17),
    PREPARE(18),
    NO(19);

    private final int value;

    SummonedActionType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
