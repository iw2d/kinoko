package kinoko.world.dialog.miniroom;

public enum LeaveType {
    // MRLeave
    UserRequest(0),
    WrongPosition(1),
    Closed(2),
    HostOut(3),
    Booked(4),
    Kicked(5),
    OpenTimeOver(6),
    // TRLeave
    TradeDone(7),
    TradeFail(8),
    TradeFail_OnlyItem(9),
    TradeFail_Expired(10),
    TradeFail_Denied(11),
    FieldError(12),
    ItemCRCFailed(13),
    // PSLeave
    NoMoreItem(14),
    KickedTimeOver(15),
    // ESLeave
    Open(16),
    StartManage(17),
    ClosedTimeOver(18),
    EndManage(19),
    DestoryByAdmin(20), // [sic]
    // MGLeave
    MGLeave_UserRequest(21);

    private final int value;

    LeaveType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
