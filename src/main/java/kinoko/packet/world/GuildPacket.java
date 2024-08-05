package kinoko.packet.world;

import kinoko.server.guild.*;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.server.user.RemoteUser;
import kinoko.world.GameConstants;

import java.util.List;

public final class GuildPacket {
    // CWvsContext::OnGuildResult --------------------------------------------------------------------------------------

    public static OutPacket inputGuildName() {
        return GuildPacket.of(GuildRequestType.InputGuildName);
    }

    public static OutPacket inviteGuild(RemoteUser remoteUser) {
        final OutPacket outPacket = GuildPacket.of(GuildRequestType.InviteGuild);
        outPacket.encodeInt(remoteUser.getCharacterId()); // dwInviterID
        outPacket.encodeString(remoteUser.getCharacterName()); // sInviter
        outPacket.encodeInt(remoteUser.getLevel()); // nLevel
        outPacket.encodeInt(remoteUser.getJob()); // nJobCode
        return outPacket;
    }

    public static OutPacket inputMark() {
        return GuildPacket.of(GuildRequestType.InputMark);
    }

    public static OutPacket loadGuildDone(Guild guild) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.LoadGuild_Done);
        outPacket.encodeByte(guild != null);
        if (guild != null) {
            guild.encode(outPacket); // GUILDDATA::Decode
        }
        return outPacket;
    }

    public static OutPacket checkGuildNameAlreadyUsed() {
        return GuildPacket.of(GuildResultType.CheckGuildName_AlreadyUsed);
    }

    public static OutPacket createNewGuildDone(Guild guild) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.CreateNewGuild_Done);
        guild.encode(outPacket);
        return outPacket;
    }

    public static OutPacket createNewGuildAlreadyJoined() {
        return GuildPacket.of(GuildResultType.CreateNewGuild_AlreadyJoined);
    }

    public static OutPacket createNewGuildBeginner() {
        return GuildPacket.of(GuildResultType.CreateNewGuild_Beginner);
    }


    public static OutPacket createNewGuildUnknown() {
        return GuildPacket.of(GuildResultType.CreateNewGuild_Unknown);
    }

    public static OutPacket joinGuildDone(int guildId, GuildMember member) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.JoinGuild_Done);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(member.getCharacterId());
        member.encode(outPacket);
        return outPacket;
    }

    public static OutPacket joinGuildAlreadyJoined() {
        return GuildPacket.of(GuildResultType.JoinGuild_AlreadyJoined);
    }

    public static OutPacket joinGuildAlreadyFull() {
        return GuildPacket.of(GuildResultType.JoinGuild_AlreadyFull);
    }

    public static OutPacket joinGuildUnknown() {
        return GuildPacket.of(GuildResultType.JoinGuild_Unknown);
    }

    public static OutPacket withdrawGuildDone(int guildId, GuildMember member) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.WithdrawGuild_Done);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(member.getCharacterId());
        outPacket.encodeString(member.getCharacterName());
        return outPacket;
    }

    public static OutPacket withdrawGuildNotJoined() {
        return GuildPacket.of(GuildResultType.WithdrawGuild_NotJoined);
    }

    public static OutPacket withdrawGuildUnknown() {
        return GuildPacket.of(GuildResultType.WithdrawGuild_Unknown);
    }

    public static OutPacket kickGuildDone(int guildId, GuildMember member) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.KickGuild_Done);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(member.getCharacterId());
        outPacket.encodeString(member.getCharacterName());
        return outPacket;
    }

    public static OutPacket kickGuildNotJoined() {
        return GuildPacket.of(GuildResultType.KickGuild_NotJoined);
    }

    public static OutPacket kickGuildUnknown() {
        return GuildPacket.of(GuildResultType.KickGuild_Unknown);
    }

    public static OutPacket removeGuildDone(int guildId) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.RemoveGuild_Done);
        outPacket.encodeInt(guildId);
        return outPacket;
    }

    public static OutPacket removeGuildUnknown() {
        return GuildPacket.of(GuildResultType.RemoveGuild_Unknown);
    }

    public static OutPacket inviteGuildFailed(GuildResultType resultType, String characterName) {
        assert resultType == GuildResultType.InviteGuild_BlockedUser || resultType == GuildResultType.InviteGuild_AlreadyInvited || resultType == GuildResultType.InviteGuild_Rejected;
        final OutPacket outPacket = GuildPacket.of(resultType);
        outPacket.encodeString(characterName);
        return outPacket;
    }

    public static OutPacket incMaxMemberNumDone(int guildId, int memberMax) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.IncMaxMemberNum_Done);
        outPacket.encodeInt(guildId);
        outPacket.encodeByte(memberMax);
        return outPacket;
    }

    public static OutPacket incMaxMemberNumUnknown() {
        return GuildPacket.of(GuildResultType.IncMaxMemberNum_Unknown);
    }

    public static OutPacket changeLevelOrJob(int guildId, int characterId, int level, int job) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.ChangeLevelOrJob);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(characterId);
        outPacket.encodeInt(level);
        outPacket.encodeInt(job);
        return outPacket;
    }

    public static OutPacket notifyLoginOrLogout(int guildId, int characterId, boolean online) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.NotifyLoginOrLogout);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(online);
        return outPacket;
    }

    public static OutPacket setGradeNameDone(int guildId, List<String> gradeNames) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.SetGradeName_Done);
        outPacket.encodeInt(guildId);
        for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
            outPacket.encodeString(gradeNames.get(i));
        }
        return outPacket;
    }

    public static OutPacket setGradeNameUnknown() {
        return GuildPacket.of(GuildResultType.SetGradeName_Unknown);
    }

    public static OutPacket setMemberGradeDone(int guildId, GuildMember member) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.SetMemberGrade_Done);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(member.getCharacterId());
        outPacket.encodeByte(member.getGuildRank().getValue());
        return outPacket;
    }

    public static OutPacket setMemberGradeUnknown() {
        return GuildPacket.of(GuildResultType.SetMemberGrade_Unknown);
    }

    public static OutPacket setMarkDone(int guildId, short markBg, byte markBgColor, short mark, byte markColor) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.SetMark_Done);
        outPacket.encodeInt(guildId);
        outPacket.encodeShort(markBg);
        outPacket.encodeByte(markBgColor);
        outPacket.encodeShort(mark);
        outPacket.encodeByte(markColor);
        return outPacket;
    }

    public static OutPacket setMarkUnknown() {
        return GuildPacket.of(GuildResultType.SetMark_Unknown);
    }

    public static OutPacket setNoticeDone(int guildId, String notice) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.SetNotice_Done);
        outPacket.encodeInt(guildId);
        outPacket.encodeString(notice);
        return outPacket;
    }

    public static OutPacket showGuildRanking(List<GuildRanking> guildRankings) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.ShowGuildRanking);
        outPacket.encodeInt(0); // ignored
        outPacket.encodeInt(guildRankings.size());
        for (GuildRanking guildRanking : guildRankings) {
            guildRanking.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket serverMsg(String message) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.ServerMsg);
        outPacket.encodeByte(message != null); // The guild request has not been accepted due to unknown reason.
        if (message != null) {
            outPacket.encodeString(message);
        }
        return outPacket;
    }

    private static OutPacket of(GuildRequestType requestType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GuildResult);
        outPacket.encodeByte(requestType.getValue());
        return outPacket;
    }

    private static OutPacket of(GuildResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GuildResult);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }
}
