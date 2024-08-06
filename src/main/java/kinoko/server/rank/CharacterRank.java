package kinoko.server.rank;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class CharacterRank implements Encodable {
    private final int characterId;
    private final int worldRank;
    private final int jobRank;

    private int worldRankGap;
    private int jobRankGap;

    public CharacterRank(int characterId, int worldRank, int jobRank) {
        this.characterId = characterId;
        this.worldRank = worldRank;
        this.jobRank = jobRank;
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getWorldRank() {
        return worldRank;
    }

    public int getJobRank() {
        return jobRank;
    }

    public int getWorldRankGap() {
        return worldRankGap;
    }

    public void setWorldRankGap(int worldRankGap) {
        this.worldRankGap = worldRankGap;
    }

    public int getJobRankGap() {
        return jobRankGap;
    }

    public void setJobRankGap(int jobRankGap) {
        this.jobRankGap = jobRankGap;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CLogin::RANK (16)
        outPacket.encodeInt(worldRank); // nWorldRank
        outPacket.encodeInt(worldRankGap); // nWorldRankGap
        outPacket.encodeInt(jobRank); // nJobRank
        outPacket.encodeInt(jobRankGap); // nJobRankGap
    }
}
