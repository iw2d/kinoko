package kinoko.packet.stage;

public enum PinCodeModalOpt {
    CANCEL(0),
    LOGIN(1),
    CHANGE_PIN(2),
    ;
    private final int value;

    PinCodeModalOpt(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}