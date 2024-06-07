package kinoko.world.social.party;

public enum PartyResultType {
    // PartyReq
    InviteParty(4),

    // PartyRes
    LoadParty_Done(7),
    CreateNewParty_Done(8),
    CreateNewParty_AlreadyJoined(9),
    CreateNewParty_Beginner(10),
    CreateNewParty_Unknown(11),
    WithdrawParty_Done(12),
    WithdrawParty_NotJoined(13),
    WithdrawParty_Unknown(14),
    JoinParty_Done(15),
    JoinParty_Done2(16),
    JoinParty_AlreadyJoined(17),
    JoinParty_AlreadyFull(18),
    JoinParty_OverDesiredSize(19),
    JoinParty_UnknownUser(20),
    JoinParty_Unknown(21),
    InviteParty_Sent(22),
    InviteParty_BlockedUser(23),
    InviteParty_AlreadyInvited(24),
    InviteParty_AlreadyInvitedByInviter(25),
    InviteParty_Rejected(26),
    InviteParty_Accepted(27),
    KickParty_Done(28),
    KickParty_FieldLimit(29),
    KickParty_Unknown(30),
    ChangePartyBoss_Done(31),
    ChangePartyBoss_NotSameField(32),
    ChangePartyBoss_NoMemberInSameField(33),
    ChangePartyBoss_NotSameChannel(34),
    ChangePartyBoss_Unknown(35),
    AdminCannotCreate(36),
    AdminCannotInvite(37),
    UserMigration(38),
    ChangeLevelOrJob(39),
    SuccessToSelectPQReward(40),
    FailToSelectPQReward(41),
    ReceivePQReward(42),
    FailToRequestPQReward(43),
    CanNotInThisField(44),
    ServerMsg(45),

    // PartyInfo
    TownPortalChanged(46),
    OpenGate(47);

    private final int value;

    PartyResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static PartyResultType getByValue(int value) {
        for (PartyResultType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
