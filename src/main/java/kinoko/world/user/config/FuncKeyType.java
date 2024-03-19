package kinoko.world.user.config;

public enum FuncKeyType {
    // T
    NONE(0),
    SKILL(1),
    ITEM(2),
    EMOTION(3),
    MENU(4),
    BASIC_ACTION(5),
    BASIC_MOTION(6),
    EFFECT(7),
    MACRO_SKILL(8),
    COUNT(8);

    private final byte value;

    FuncKeyType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }

    public static FuncKeyType getByValue(int value) {
        for (FuncKeyType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
