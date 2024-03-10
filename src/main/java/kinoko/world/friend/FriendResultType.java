package kinoko.world.friend;

public enum FriendResultType {
    // FriendRes
    LOAD_FRIEND_DONE(7),
    NOTIFY_CHANGE_FRIEND_INFO(8),
    INVITE(9),
    SET_FRIEND_DONE(10),
    SET_FRIEND_FULL_ME(11),
    SET_FRIEND_FULL_OTHER(12),
    SET_FRIEND_ALREADY_SET(13),
    SET_FRIEND_MASTER(14),
    SET_FRIEND_UNKNOWN_USER(15),
    SET_FRIEND_UNKNOWN(16),
    ACCEPT_FRIEND_UNKNOWN(17),
    DELETE_FRIEND_DONE(18),
    DELETE_FRIEND_UNKNOWN(19),
    NOTIFY(20),
    INC_MAX_COUNT_DONE(21),
    INC_MAX_COUNT_UNKNOWN(22),
    PLEASE_WAIT(23);

    private final int value;

    FriendResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
