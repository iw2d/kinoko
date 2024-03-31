package kinoko.packet.field;

public enum GroupMessageType {
    // ID_CHAT_TARGET
    FRIEND(0),
    FRIEND_GROUP(1),
    PARTY(2),
    EXPEDITION(3),
    GUILD(4),
    ALLIANCE(5),
    COUPLE(6),
    WHISPER(7),
    ALL(8);

    private final int value;

    GroupMessageType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static GroupMessageType getByValue(int value) {
        for (GroupMessageType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
