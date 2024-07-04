package kinoko.server.party;

public enum PartyRequestType {
    // PartyReq
    LoadParty(0),
    CreateNewParty(1),
    WithdrawParty(2),
    JoinParty(3),
    InviteParty(4),
    KickParty(5),
    ChangePartyBoss(6);

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
