package kinoko.packet.stage;

public enum PinCodeUpdateModalOpt {
    CANCEL(0),
    OK(1),
    ;
    private final int value;

    PinCodeUpdateModalOpt(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}