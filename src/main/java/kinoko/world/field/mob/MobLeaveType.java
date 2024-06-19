package kinoko.world.field.mob;

public enum MobLeaveType {
    // MOBLEAVEFIELD
    REMAINHP(0),
    ETC(1),
    SELFDESTRUCT(2),
    DESTRUCTBYMISS(3),
    SWALLOW(4),
    SUMMONTIMEOUT(5);

    private final byte value;

    MobLeaveType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }
}
