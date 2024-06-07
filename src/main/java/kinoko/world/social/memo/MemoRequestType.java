package kinoko.world.social.memo;

public enum MemoRequestType {
    // MemoReq
    Send(0),
    Delete(1),
    Load(2);

    private final int value;

    MemoRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static MemoRequestType getByValue(int value) {
        for (MemoRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
