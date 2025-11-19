package kinoko.server.family;

public enum FamilyRequestType {
    // PartyReq
    LoadParty(0),
    CreateNewParty(1),
    WithdrawParty(2),
    JoinParty(3),
    InviteParty(4),
    KickParty(5),
    ChangePartyBoss(6);

    private final int value;

    FamilyRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static FamilyRequestType getByValue(int value) {
        for (FamilyRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
