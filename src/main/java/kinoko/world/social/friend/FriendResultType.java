package kinoko.world.social.friend;

public enum FriendResultType {
    // FriendRes
    LoadFriend_Done(7),
    NotifyChange_FriendInfo(8),
    Invite(9),
    SetFriend_Done(10),
    SetFriend_FullMe(11),
    SetFriend_FullOther(12),
    SetFriend_AlreadySet(13),
    SetFriend_Master(14),
    SetFriend_UnknownUser(15),
    SetFriend_Unknown(16),
    AcceptFriend_Unknown(17),
    DeleteFriend_Done(18),
    DeleteFriend_Unknown(19),
    Notify(20),
    IncMaxCount_Done(21),
    INcMaxCount_Unknown(22),
    PleaseWait(23);

    private final int value;

    FriendResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
