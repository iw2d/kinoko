package kinoko.world.friend;

public enum FriendRequestType {
    // FriendReq
    LOAD_FRIEND(0),
    SET_FRIEND(1),
    ACCEPT_FRIEND(2),
    DELETE_FRIEND(3),
    NOTIFY_LOGIN(4),
    NOTIFY_LOGOUT(5),
    INC_MAX_COUNT(6);

    private final int value;

    FriendRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static FriendRequestType getByValue(int value) {
        for (FriendRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
