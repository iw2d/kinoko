package kinoko.packet.world;

import kinoko.server.alliance.Alliance;
import kinoko.server.alliance.AllianceRequestType;
import kinoko.server.alliance.AllianceResultType;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildMember;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.GameConstants;

import java.util.List;

public final class AlliancePacket {
    // CWvsContext::OnAllianceResult -----------------------------------------------------------------------------------

    public static OutPacket invite(int inviterId, String inviterName) {
        final OutPacket outPacket = OutPacket.of(OutHeader.AllianceResult);
        outPacket.encodeByte(AllianceRequestType.Invite.getValue());
        outPacket.encodeInt(inviterId); // dwInviterID
        outPacket.encodeString(inviterName); // sInviter
        outPacket.encodeString(""); // not used
        return outPacket;
    }

    public static OutPacket loadDone(Alliance alliance) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.LoadDone);
        outPacket.encodeByte(alliance != null);
        if (alliance != null) {
            alliance.encode(outPacket); // ALLIANCEDATA::Decode
        }
        return outPacket;
    }

    public static OutPacket loadGuildDone(List<Guild> guilds) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.LoadGuildDone);
        outPacket.encodeInt(guilds.size());
        for (Guild guild : guilds) {
            guild.encode(outPacket); // GUILDDATA::Decode
        }
        return outPacket;
    }

    public static OutPacket notifyLoginOrLogout(int allianceId, int guildId, int characterId, boolean online) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.NotifyLoginOrLogout);
        outPacket.encodeInt(allianceId);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(online);
        return outPacket;
    }

    public static OutPacket createDone(Alliance alliance, List<Guild> guilds) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.CreateDone);
        alliance.encode(outPacket); // ALLIANCEDATA::Decode
        for (Guild guild : guilds) {
            guild.encode(outPacket); // GUILDDATA::Decode
        }
        return outPacket;
    }

    public static OutPacket withdrawDone(Alliance alliance, Guild guild, boolean kicked) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.Withdraw_Done);
        alliance.encode(outPacket); // ALLIANCEDATA::Decode
        outPacket.encodeInt(guild.getGuildId()); // nGuildID
        guild.encode(outPacket); // GUILDDATA::Decode
        outPacket.encodeByte(kicked); // You have been kicked from the alliance. | You have left the alliance.
        return outPacket;
    }

    public static OutPacket inviteDone(Alliance alliance, Guild guild) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.Invite_Done);
        alliance.encode(outPacket); // ALLIANCEDATA::Decode
        outPacket.encodeInt(guild.getGuildId()); // nGuildID
        guild.encode(outPacket); // GUILDDATA::Decode
        return outPacket;
    }

    public static OutPacket updateAllianceInfo(Alliance alliance) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.UpdateAllianceInfo);
        alliance.encode(outPacket); // ALLIANCEDATA::Decode
        return outPacket;
    }

    public static OutPacket changeLevelOrJob(int allianceId, int guildId, int characterId, int level, int job) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.ChangeLevelOrJob);
        outPacket.encodeInt(allianceId);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(characterId);
        outPacket.encodeInt(level);
        outPacket.encodeInt(job);
        return outPacket;
    }

    public static OutPacket changeMasterDone(int allianceId, int oldMasterId, int newMasterId) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.ChangeLevelOrJob);
        outPacket.encodeInt(allianceId);
        outPacket.encodeInt(oldMasterId);
        outPacket.encodeInt(newMasterId);
        return outPacket;
    }

    public static OutPacket setGradeNameDone(int allianceId, List<String> gradeNames) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.SetGradeName_Done);
        outPacket.encodeInt(allianceId);
        for (int i = 0; i < GameConstants.UNION_GRADE_MAX; i++) {
            outPacket.encodeString(gradeNames.get(i));
        }
        return outPacket;
    }

    public static OutPacket changeGradeDone(GuildMember member) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.SetGradeName_Done);
        outPacket.encodeInt(member.getCharacterId());
        outPacket.encodeByte(member.getAllianceRank().getValue()); // nAllianceGrade
        return outPacket;
    }

    public static OutPacket setNoticeDone(int allianceId, String notice) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.SetNotice_Done);
        outPacket.encodeInt(allianceId);
        outPacket.encodeString(notice);
        return outPacket;
    }

    public static OutPacket destroyDone(int allianceId) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.Destroy_Done);
        outPacket.encodeInt(allianceId);
        return outPacket;
    }

    public static OutPacket updateGuildInfo(Guild guild) {
        final OutPacket outPacket = AlliancePacket.of(AllianceResultType.UpdateGuildInfo);
        outPacket.encodeInt(guild.getAllianceId()); // nAllianceID
        outPacket.encodeInt(guild.getGuildId()); // nGuildID
        guild.encode(outPacket); // GUILDDATA::Decode
        return outPacket;
    }

    private static OutPacket of(AllianceResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.AllianceResult);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }
}
