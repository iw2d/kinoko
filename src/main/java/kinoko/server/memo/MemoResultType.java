package kinoko.server.memo;

public enum MemoResultType {
    // MemoRes
    Load(3),
    Send_Succeed(4),
    Send_Warning(5),
    Send_ConfirmOnline(6),

    // MemoNotify
    Receive(7);

    private final int value;

    MemoResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
