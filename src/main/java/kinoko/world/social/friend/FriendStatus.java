package kinoko.world.social.friend;

public enum FriendStatus {
    // FS
    NORMAL(0),
    REQUEST(1),
    REFUSED(2),
    CELL_AUTH(3),
    MATE_ENABLE(4);

    private final int value;

    FriendStatus(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static FriendStatus getByValue(int value) {
        for (FriendStatus status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
