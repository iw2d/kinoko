package kinoko.world.user.stat;

public enum TwoStateType {
    NOT_TWO_STATE,
    NO_EXPIRE,
    EXPIRE_BASED_ON_CURRENT_TIME,
    EXPIRE_BASED_ON_LAST_UPDATED_TIME;

    public final int getValue() {
        return ordinal();
    }

    public static TwoStateType getByValue(int value) {
        for (TwoStateType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
