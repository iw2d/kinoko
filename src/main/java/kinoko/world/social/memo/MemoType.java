package kinoko.world.social.memo;

public enum MemoType {
    // MEMO
    DEFAULT(0),
    INC_POP(1),
    NOTIFY_RECEIPT_GIFT(2),
    INVITATION(3),
    BROKE_UP(4),
    DIVORCED(5),
    FROM_GM(6);

    private final int value;

    MemoType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static MemoType getByValue(int value) {
        for (MemoType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
