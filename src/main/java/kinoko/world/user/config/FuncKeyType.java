package kinoko.world.user.config;

public enum FuncKeyType {
    // T
    NONE(0),
    SKILL(1),
    ITEM(2),
    EMOTION(3),
    MENU(4),
    BASICACTION(5),
    BASICMOTION(6),
    EFFECT(7),
    MACROSKILL(8),
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
