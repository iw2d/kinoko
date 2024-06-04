package kinoko.server.dialog.miniroom;

public enum LeaveType {
    // MRLeave
    USER_REQUEST(0),
    WRONG_POSITION(1),
    CLOSED(2),
    HOST_OUT(3),
    BOOKED(4),
    KICKED(5),
    OPEN_TIME_OVER(6),
    // TRLeave
    TRADE_DONE(7),
    TRADE_FAIL(8),
    TRADE_FAIL_ONLY_ITEM(9),
    TRADE_FAIL_EXPIRED(10),
    TRADE_FAIL_DENIED(11),
    FIELD_ERROR(12),
    ITEM_CR_FAILED(13),
    // PSLeave
    NO_MORE_ITEM(14),
    KICKED_TIME_OVER(15),
    // ESLeave
    OPEN(16),
    START_MANAGE(17),
    CLOSED_TIME_OVER(18),
    END_MANAGE(19),
    DESTORY_BY_ADMIN(20), // [sic]
    // MGLeave
    MINIGAME_USER_REQUEST(21);

    private final int value;

    LeaveType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
