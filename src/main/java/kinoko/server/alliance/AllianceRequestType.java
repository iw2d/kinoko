package kinoko.server.alliance;

public enum AllianceRequestType {
    // AllianceReq
    Create(0),
    Load(1),
    Withdraw(2),
    Invite(3),
    Join(4),
    UpdateMemberCountMax(5),
    Kick(6),
    ChangeMaster(7),
    SetGradeName(8),
    ChangeGrade(9),
    SetNotice(10),
    Destroy(11);

    private final int value;

    AllianceRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static AllianceRequestType getByValue(int value) {
        for (AllianceRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
