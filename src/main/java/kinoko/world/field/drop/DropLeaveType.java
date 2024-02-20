package kinoko.world.field.drop;

public enum DropLeaveType {
    TIMEOUT(0),
    SCREEN_SCROLL(1),
    PICKED_UP_BY_USER(2),
    PICKED_UP_BY_MOB(3),
    EXPLODE(4),
    PICKED_UP_BY_PET(5);

    private final int value;

    DropLeaveType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
