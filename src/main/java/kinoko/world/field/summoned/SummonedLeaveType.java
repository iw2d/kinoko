package kinoko.world.field.summoned;

public enum SummonedLeaveType {
    // LEAVE_TYPE
    UPDATE(0),
    SUMMONED_DEAD(1),
    MYSTERY(2),
    DEFAULT(3),
    LEAVE_FIELD(4),
    SELF_DESTRUCT(5),
    GABIOTA(6), // [sic]
    ENTER_FORBIDEN_MAP(7),
    ENTER_EVENT_FIELD(8),
    USER_DEAD(9),
    ON_REMOVE(10),
    TESLACOIL_ERROR(11),
    NOT_ABLE_MULTIPLE(12),
    DIDNT_SELFDESTRUCT(13),
    SUMMONED_COUNT_OVER(14);

    private final int value;

    SummonedLeaveType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
