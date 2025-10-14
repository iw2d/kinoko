package kinoko.database.types;

import java.time.Instant;

public record CharacterRankData(int characterId, int jobCategory, long cumulativeExp, Instant maxLevelTime) {

    @Override
    public Instant maxLevelTime() {
        return maxLevelTime != null ? maxLevelTime : Instant.MAX;
    }
}