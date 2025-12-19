package kinoko.world.field.mob;

public enum MobType {
    NORMAL(0),
    SUB_MOB(1),
    PARENT_MOB(2);

    private final byte value;

    MobType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }

    public static MobType getByValue(int value) {
        for (MobType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
