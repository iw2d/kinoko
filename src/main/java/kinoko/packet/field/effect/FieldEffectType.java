package kinoko.packet.field.effect;

public enum FieldEffectType {
    SUMMON(0),
    TREMBLE(1),
    OBJECT(2),
    SCREEN(3),
    SOUND(4),
    MOB_HP_TAG(5),
    CHANGE_BGM(6),
    REWORD_RULLET(7); // [sic]

    private final int value;

    FieldEffectType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
