package kinoko.world.user.friend;

public enum FriendRequestType {
    // FriendReq
    LoadFriend(0),
    SetFriend(1),
    AcceptFriend(2),
    DeleteFriend(3),
    NotifyLogin(4),
    NotifyLogout(5),
    IncMaxCount(6);

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
