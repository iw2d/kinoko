package kinoko.database;

import java.time.Instant;

public final class CharacterRankData {
    private final int characterId;
    private final int jobCategory;
    private final long cumulativeExp;
    private final Instant maxLevelTime;

    public CharacterRankData(int characterId, int jobCategory, long cumulativeExp, Instant maxLevelTime) {
        this.characterId = characterId;
        this.jobCategory = jobCategory;
        this.cumulativeExp = cumulativeExp;
        this.maxLevelTime = maxLevelTime;
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getJobCategory() {
        return jobCategory;
    }

    public long getCumulativeExp() {
        return cumulativeExp;
    }

    public Instant getMaxLevelTime() {
        return maxLevelTime != null ? maxLevelTime : Instant.MAX;
    }
}
