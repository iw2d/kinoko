package kinoko.world.social.party;

public enum PartyRequestType {
    // PartyReq
    LOAD_PARTY(0),
    CREATE_NEW_PARTY(1),
    WITHDRAW_PARTY(2),
    JOIN_PARTY(3),
    INVITE_PARTY(4),
    KICK_PARTY(5),
    CHANGE_PARTY_BOSS(6);

    private final int value;

    PartyRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static PartyRequestType getByValue(int value) {
        for (PartyRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
