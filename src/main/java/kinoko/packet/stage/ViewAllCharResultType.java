package kinoko.packet.stage;

public enum ViewAllCharResultType {
    // VAC_ResCode
    Success(0),
    CountRelatedSvrs(1),
    AlreadyConnected(2),
    TimedOut(3),
    HasNoCharacterInAllWorld(4),
    HasNoCharacterInOneWorld(5),
    DBError(6),
    VADDlgAlreadyOn(7); // (sic)

    private final int value;

    ViewAllCharResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
