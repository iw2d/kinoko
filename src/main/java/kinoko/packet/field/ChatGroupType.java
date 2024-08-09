package kinoko.packet.field;

public enum ChatGroupType {
    // CG
    Friend(0),
    Party(1),
    Guild(2),
    Alliance(3),
    COUPLE(4),
    ToCouple(5),
    Expedition(6);

    private final int value;

    ChatGroupType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static ChatGroupType getByValue(int value) {
        for (ChatGroupType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
