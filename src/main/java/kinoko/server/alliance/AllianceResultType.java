package kinoko.server.alliance;

public enum AllianceResultType {
    // AllianceRes
    LoadDone(12),
    LoadGuildDone(13),
    NotifyLoginOrLogout(14),
    CreateDone(15),
    Withdraw_Done(16),
    Withdraw_Failed(17),
    Invite_Done(18),
    Invite_Failed(19),
    InviteGuild_BlockedByOpt(20),
    InviteGuild_AlreadyInvited(21),
    InviteGuild_Rejected(22),
    UpdateAllianceInfo(23),
    ChangeLevelOrJob(24),
    ChangeMaster_Done(25),
    SetGradeName_Done(26),
    ChangeGrade_Done(27),
    SetNotice_Done(28),
    Destroy_Done(29),
    UpdateGuildInfo(30);

    private final int value;

    AllianceResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static AllianceResultType getByValue(int value) {
        for (AllianceResultType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
