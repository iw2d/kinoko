package kinoko.world.user.data;

public enum FuncKeyMappedType {
    // FuncKeyMapped
    KeyModified(0),
    PetConsumeItemModified(1),
    PetConsumeMPItemModified(2);

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
