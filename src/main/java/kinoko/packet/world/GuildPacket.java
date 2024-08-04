package kinoko.packet.world;

import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildRequestType;
import kinoko.server.guild.GuildResultType;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.server.user.RemoteUser;

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

    public static OutPacket removeGuildDone(int guildId) {
        final OutPacket outPacket = GuildPacket.of(GuildResultType.RemoveGuild_Done);
        outPacket.encodeInt(guildId);
        return outPacket;
    }

    public static OutPacket removeGuildUnknown() {
        return GuildPacket.of(GuildResultType.RemoveGuild_Unknown);
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
