package kinoko.server.guild;

public enum GuildResultType {
    // GuildRes
    LoadGuild_Done(28),
    CheckGuildName_Available(29),
    CheckGuildName_AlreadyUsed(30),
    CheckGuildName_Unknown(31),
    CreateGuildAgree_Reply(32),
    CreateGuildAgree_Unknown(33),
    CreateNewGuild_Done(34),
    CreateNewGuild_AlreadyJoined(35),
    CreateNewGuild_GuildNameAlreayExist(36), // [sic]
    CreateNewGuild_Beginner(37),
    CreateNewGuild_Disagree(38),
    CreateNewGuild_NotFullParty(39),
    CreateNewGuild_Unknown(40),
    JoinGuild_Done(41),
    JoinGuild_AlreadyJoined(42),
    JoinGuild_AlreadyFull(43),
    JoinGuild_UnknownUser(44),
    JoinGuild_Unknown(45),
    WithdrawGuild_Done(46),
    WithdrawGuild_NotJoined(47),
    WithdrawGuild_Unknown(48),
    KickGuild_Done(49),
    KickGuild_NotJoined(50),
    KickGuild_Unknown(51),
    RemoveGuild_Done(52),
    RemoveGuild_NotJoined(53),
    RemoveGuild_Unknown(54),
    InviteGuild_BlockedUser(55),
    InviteGuild_AlreadyInvited(56),
    InviteGuild_Rejected(57),
    AdminCannotCreate(58),
    AdminCannotInvite(59),
    IncMaxMemberNum_Done(60),
    IncMaxMemberNum_Unknown(61),
    ChangeLevelOrJob(62),
    NotifyLoginOrLogout(63),
    SetGradeName_Done(64),
    SetGradeName_Unknown(65),
    SetMemberGrade_Done(66),
    SetMemberGrade_Unknown(67),
    SetMemberCommitment_Done(68),
    SetMark_Done(69),
    SetMark_Unknown(70),
    SetNotice_Done(71),
    InsertQuest(72),
    NoticeQuestWaitingOrder(73),
    SetGuildCanEnterQuest(74),
    IncPoint_Done(75),
    ShowGuildRanking(76),
    GuildQuest_NotEnoughUser(77),
    GuildQuest_RegisterDisconnected(78),
    GuildQuest_NoticeOrder(79),
    Authkey_Update(80),
    SetSkill_Done(81),
    ServerMsg(82);

    private final int value;

    GuildResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static GuildResultType getByValue(int value) {
        for (GuildResultType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
