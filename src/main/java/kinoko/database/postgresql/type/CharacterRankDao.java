package kinoko.database.postgresql.type;


import kinoko.database.types.CharacterRankData;
import kinoko.server.rank.CharacterRank;
import kinoko.world.job.JobConstants;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public final class CharacterRankDao {

    /**
     * Retrieves all character ranks (world and job-specific) using the provided connection.
     *
     * Characters with admin/manager jobs are skipped. Ranks are sorted by cumulative
     * EXP descending, and for ties, by earliest max level time.
     *
     * @param conn an active SQL connection
     * @return a map from character ID to CharacterRank
     * @throws SQLException if a database access error occurs
     */
    public static Map<Integer, CharacterRank> getCharacterRanks(Connection conn) throws SQLException {
        Map<Integer, CharacterRank> ranks = new HashMap<>();
        String sql = """
            SELECT c.id, c.max_level_time, s.job, s.exp
            FROM player.characters c
            JOIN player.stats s ON c.id = s.character_id
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<CharacterRankData> rankDataList = new ArrayList<>();

            while (rs.next()) {
                int characterId = rs.getInt("id");
                int jobId = rs.getInt("job");
                long cumulativeExp = rs.getLong("exp");
                Timestamp ts = rs.getTimestamp("max_level_time");

                if (JobConstants.isAdminJob(jobId) || JobConstants.isManagerJob(jobId)) {
                    continue;
                }

                rankDataList.add(new CharacterRankData(
                        characterId,
                        JobConstants.getJobCategory(jobId),
                        cumulativeExp,
                        ts != null ? ts.toInstant() : Instant.MAX
                ));
            }

            // Sort by EXP (descending) and then by earliest max level time
            rankDataList.sort(
                    Comparator.comparingLong(CharacterRankData::cumulativeExp).reversed()
                            .thenComparing(CharacterRankData::maxLevelTime)
            );

            // compute world rank and job rank
            Map<Integer, Integer> jobRanks = new HashMap<>();
            for (CharacterRankData data : rankDataList) {
                int worldRank = ranks.size() + 1;
                int jobRank = jobRanks.getOrDefault(data.jobCategory(), 0) + 1;
                jobRanks.put(data.jobCategory(), jobRank);

                ranks.put(data.characterId(), new CharacterRank(
                        data.characterId(),
                        worldRank,
                        jobRank
                ));
            }
        }

        return ranks;
    }
}
