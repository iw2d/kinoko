package kinoko.packet.field;

public enum WhisperFlag {
    // WP
    Location(0x1),
    Whisper(0x2),
    Request(0x4),
    Result(0x8),
    Receive(0x10),
    Blocked(0x20),
    Location_F(0x40), // bTabFriend
    Manager(0x80),

    // Inbound flags
    LocationRequest(Location.getValue() | Request.getValue()),
    LocationRequest_F(Location_F.getValue() | Request.getValue()),
    WhisperRequest(Whisper.getValue() | Request.getValue()),
    WhisperRequestmanager(Whisper.getValue() | Request.getValue() | Manager.getValue()),

    // Both inbound and outbound
    WhisperBlocked(Whisper.getValue() | Blocked.getValue()),

    // Outbound flags (CField::OnWhisper)
    LocationResult(Location.getValue() | Result.getValue()),
    LocationResult_F(Location_F.getValue() | Result.getValue()),
    WhisperResult(Whisper.getValue() | Result.getValue()),
    WhisperResultManager(Whisper.getValue() | Result.getValue() | Manager.getValue()),
    WhisperReceive(Whisper.getValue() | Receive.getValue()),
    WhisperReceiveManager(Whisper.getValue() | Receive.getValue() | Manager.getValue());


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
