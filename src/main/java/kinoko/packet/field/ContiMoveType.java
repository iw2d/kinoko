package kinoko.packet.field;

public enum ContiMoveType {
    // CONTI
    DORMANT(0),
    WAIT(1),
    START(2),
    MOVE(3),
    MOBGEN(4),
    MOBDESTROY(5),
    END(6),

    TARGET_STARTFIELD(7),
    TARGET_START_SHIPMOVE_FIELD(8),
    TARGET_WAITFIELD(9),
    TARGET_MOVEFIELD(10),
    TARGET_ENDFIELD(11),
    TARGET_END_SHIPMOVE_FIELD(12);


    private final int value;

    ContiMoveType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
