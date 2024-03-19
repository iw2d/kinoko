package kinoko.world.user.config;

public enum FuncKeyMappedType {
    // FuncKeyMapped
    KEY_MODIFIED(0),
    PET_CONSUME_ITEM_MODIFIED(1),
    PET_CONSUME_MP_ITEM_MODIFIED(2);

    private final byte value;

    FuncKeyMappedType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }

    public static FuncKeyMappedType getByValue(int value) {
        for (FuncKeyMappedType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
