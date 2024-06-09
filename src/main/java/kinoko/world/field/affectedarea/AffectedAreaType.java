package kinoko.world.field.affectedarea;

public enum AffectedAreaType {
    // AffectArea
    MobSkill(0),
    UserSkill(1),
    Smoke(2),
    Buff(3),
    BlessedMist(4);

    private final int value;

    AffectedAreaType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
