package kinoko.server.whisper;

public enum WhisperFlag {
    // WP
    LOCATION(0x1),
    WHISPER(0x2),
    REQUEST(0x4),
    RESULT(0x8),
    RECEIVE(0x10),
    BLOCKED(0x20),
    LOCATION_F(0x40), // bTabFriend
    MANAGER(0x80),

    // Inbound flags
    LOCATION_REQUEST(LOCATION.getValue() | REQUEST.getValue()),
    LOCATION_REQUEST_F(LOCATION_F.getValue() | REQUEST.getValue()),
    WHISPER_REQUEST(WHISPER.getValue() | REQUEST.getValue()),
    WHISPER_REQUEST_MANAGER(WHISPER.getValue() | REQUEST.getValue() | MANAGER.getValue()),

    // Both inbound and outbound
    WHISPER_BLOCKED(WHISPER.getValue() | BLOCKED.getValue()),

    // Outbound flags (CField::OnWhisper)
    LOCATION_RESULT(LOCATION.getValue() | RESULT.getValue()),
    LOCATION_RESULT_F(LOCATION_F.getValue() | RESULT.getValue()),
    WHISPER_RESULT(WHISPER.getValue() | RESULT.getValue()),
    WHISPER_RESULT_MANAGER(WHISPER.getValue() | RESULT.getValue() | MANAGER.getValue()),
    WHISPER_RECEIVE(WHISPER.getValue() | RECEIVE.getValue()),
    WHISPER_RECEIVE_MANAGER(WHISPER.getValue() | RECEIVE.getValue() | MANAGER.getValue());


    private final int value;

    WhisperFlag(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static WhisperFlag getByValue(int value) {
        for (WhisperFlag flag : values()) {
            if (flag.getValue() == value) {
                return flag;
            }
        }
        return null;
    }
}
