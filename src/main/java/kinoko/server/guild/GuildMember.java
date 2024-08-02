package kinoko.server.guild;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class GuildMember implements Encodable {
    private final int characterId;
    private final String characterName;
    private int job;
    private int level;
    private boolean online;
    private GuildRank guildRank;
    private GuildRank allianceRank;

    public GuildMember(int characterId, String characterName, int job, int level, boolean online, GuildRank guildRank, GuildRank allianceRank) {
        this.characterId = characterId;
        this.characterName = characterName;
        this.job = job;
        this.level = level;
        this.online = online;
        this.guildRank = guildRank;
        this.allianceRank = allianceRank;
    }

    public int getCharacterId() {
        return characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public GuildRank getGuildRank() {
        return guildRank;
    }

    public void setGuildRank(GuildRank guildRank) {
        this.guildRank = guildRank;
    }

    public GuildRank getAllianceRank() {
        return allianceRank;
    }

    public void setAllianceRank(GuildRank allianceRank) {
        this.allianceRank = allianceRank;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GUILDMEMBER (37)
        outPacket.encodeString(characterName); // sCharacterName
        outPacket.encodeInt(job); // nJob
        outPacket.encodeInt(level); // nLevel
        outPacket.encodeInt(guildRank.getValue()); // nGrade
        outPacket.encodeInt(online); // bOnline
        outPacket.encodeInt(0); // nCommitment
        outPacket.encodeInt(allianceRank.getValue()); // nAllianceGrade
    }
}
