package kinoko.packet.field.effect;

public enum FieldEffectType {
    // FieldEffect
    Summmon(0),
    Tremble(1),
    Object(2),
    Screen(3),
    Sound(4),
    MobHPTag(5),
    ChangeBGM(6),
    RewordRullet(7); // [sic]

    private final int value;

    FieldEffectType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
