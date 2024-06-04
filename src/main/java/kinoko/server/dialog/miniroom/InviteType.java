package kinoko.server.dialog.miniroom;

public enum InviteType {
    // MRInvite
    SUCCESS(0),
    NO_CHARACTER(1),
    CANNOT_INVITE(2),
    REJECTED(3),
    BLOCKED(4);

    private final int value;

    InviteType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static InviteType getByValue(int value) {
        for (InviteType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
