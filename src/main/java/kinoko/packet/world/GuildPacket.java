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
        final OutPacket outPacket = OutPacket.of(OutHeader.GuildResult);
        outPacket.encodeByte(GuildRequestType.InputGuildName.getValue());
        return outPacket;
    }

    public static OutPacket inviteGuild(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GuildResult);
        outPacket.encodeByte(GuildRequestType.InviteGuild.getValue());
        outPacket.encodeInt(remoteUser.getCharacterId()); // dwInviterID
        outPacket.encodeString(remoteUser.getCharacterName()); // sInviter
        outPacket.encodeInt(remoteUser.getLevel()); // nLevel
        outPacket.encodeInt(remoteUser.getJob()); // nJobCode
        return outPacket;
    }

    public static OutPacket inputMark() {
        final OutPacket outPacket = OutPacket.of(OutHeader.GuildResult);
        outPacket.encodeByte(GuildRequestType.InputMark.getValue());
        return outPacket;
    }

    public static OutPacket loadGuildDone(Guild guild) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GuildResult);
        outPacket.encodeByte(GuildResultType.LoadGuild_Done.getValue());
        outPacket.encodeByte(guild != null);
        if (guild != null) {
            guild.encode(outPacket); // GUILDDATA::Decode
        }
        return outPacket;
    }
}
