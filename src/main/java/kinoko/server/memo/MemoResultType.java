package kinoko.server.memo;

public enum MemoResultType {
    // MemoRes
    LOAD(3),
    SEND_SUCCEED(4),
    SEND_WARNING(5),
    SEND_CONFIRM_ONLINE(6),

    // MemoNotify
    RECEIVE(7);

    private final int value;

    MemoResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
