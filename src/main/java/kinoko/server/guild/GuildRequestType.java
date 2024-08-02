package kinoko.server.guild;

public enum GuildRequestType {
    // GuildReq
    LoadGuild(0),
    InputGuildName(1),
    CheckGuildName(2),
    CreateGuildAgree(3),
    CreateNewGuild(4),
    InviteGuild(5),
    JoinGuild(6),
    WithdrawGuild(7),
    KickGuild(8),
    RemoveGuild(9),
    IncMaxMemberNum(10),
    ChangeLevel(11),
    ChangeJob(12),
    SetGradeName(13),
    SetMemberGrade(14),
    SetMark(15),
    SetNotice(16),
    InputMark(17),
    CheckQuestWaiting(18),
    CheckQuestWaiting2(19),
    InsertQuestWaiting(20),
    CancelQuestWaiting(21),
    RemoveQuestCompleteGuild(22),
    IncPoint(23),
    IncCommitment(24),
    SetQuestTime(25),
    ShowGuildRanking(26),
    SetSkill(27); // GuildReg_SetSkill?

    private final int value;

    GuildRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
