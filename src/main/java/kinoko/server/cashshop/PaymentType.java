package kinoko.server.cashshop;

public enum PaymentType {
    NX_CREDIT(0x1),     // nNexonCash
    MAPLE_POINT(0x2),   // nMaplePoint
    NX_PREPAID(0x4);    // nPrepareNXCash

    private final int value;

    PaymentType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static PaymentType getByValue(int value) {
        for (PaymentType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
