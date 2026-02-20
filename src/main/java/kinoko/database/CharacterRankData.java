package kinoko.database;

import kinoko.server.rank.CharacterRank;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<Integer, CharacterRank> processCharacterRanks(List<CharacterRankData> rankDataList) {
        rankDataList.sort(Comparator.comparing(CharacterRankData::getCumulativeExp).reversed().thenComparing(CharacterRankData::getMaxLevelTime));
        final Map<Integer, Integer> jobRanks = new HashMap<>(); // job rank counter
        final Map<Integer, CharacterRank> characterRanks = new HashMap<>(); // character id -> character rank
        for (CharacterRankData rankData : rankDataList) {
            final int characterId = rankData.getCharacterId();
            final int jobCategory = rankData.getJobCategory();
            final int worldRank = characterRanks.size() + 1;
            final int jobRank = jobRanks.getOrDefault(jobCategory, 0) + 1;
            jobRanks.put(jobCategory, jobRank);
            characterRanks.put(characterId, new CharacterRank(
                    characterId,
                    worldRank,
                    jobRank
            ));
        }
        return characterRanks;
    }
}
