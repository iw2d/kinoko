package kinoko.server.dialog.miniroom;

public enum MiniRoomInviteType {
    // MRInvite
    Success(0),
    NoCharacter(1),
    CannotInvite(2),
    Rejected(3),
    Blocked(4);

    private final int value;

    MiniRoomInviteType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static MiniRoomInviteType getByValue(int value) {
        for (MiniRoomInviteType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
