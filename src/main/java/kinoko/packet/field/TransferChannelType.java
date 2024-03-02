package kinoko.packet.field;

public enum TransferChannelType {
    // TC
    DONE(0),
    GAMESVR_DISCONNECTED(1),
    SHOPSVR_DISCONNECTED(2),
    ITCSVR_DISCONNECTED(3),
    ITCSVR_OVER_LIMIT_USER(4),
    ITCSVR_LOW_LEVEL_USER(5);

    private final int value;

    TransferChannelType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
