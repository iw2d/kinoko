package kinoko.world.dialog.miniroom;

public enum InviteType {
    // MRInvite
    Success(0),
    NoCharacter(1),
    CannotInvite(2),
    Rejected(3),
    Blocked(4);

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
