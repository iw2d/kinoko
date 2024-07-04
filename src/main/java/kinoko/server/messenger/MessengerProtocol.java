package kinoko.server.messenger;

public enum MessengerProtocol {
    // MSMP
    MSMP_Enter(0),
    MSMP_SelfEnterResult(1),
    MSMP_Leave(2),
    MSMP_Invite(3),
    MSMP_InviteResult(4),
    MSMP_Blocked(5),
    MSMP_Chat(6),
    MSMP_Avatar(7),
    MSMP_Migrated(8);

    private final int value;

    MessengerProtocol(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static MessengerProtocol getByValue(int value) {
        for (MessengerProtocol type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
