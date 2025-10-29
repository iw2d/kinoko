package kinoko.database.postgresql.type;

import kinoko.world.user.CharacterData;
import kinoko.world.user.data.PopularityRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class PopularityRecordDao {

    /**
     * Loads the PopularityRecord for the specified character.
     *
     * Retrieves all records of other characters who have given popularity to this character,
     * along with the timestamps of when it occurred.
     *
     * @param conn the database connection to use
     * @param characterId the ID of the character
     * @return a PopularityRecord populated with other characters and timestamps
     * @throws SQLException if a database access error occurs
     */
    public static PopularityRecord loadPopularityRecord(Connection conn, int characterId) throws SQLException {
        PopularityRecord pr = new PopularityRecord();

        String sql = "SELECT other_character_id, timestamp FROM player.popularity WHERE character_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int otherCharId = rs.getInt("other_character_id");
                    Timestamp ts = rs.getTimestamp("timestamp");
                    if (ts != null) {
                        pr.getRecords().put(otherCharId, ts.toInstant());
                    }
                }
            }
        }

        return pr;
    }

    /**
     * Saves the character’s popularity (fame) relationships to other characters.
     * Each entry represents a character that has received or given popularity points.
     * Uses UPSERT logic to ensure timestamps are updated for existing records.
     *
     * @param conn the active database connection
     * @param characterData the character whose popularity data should be saved
     * @throws SQLException if a database access error occurs
     */
    public static void saveCharacterPopularity(Connection conn, CharacterData characterData) throws SQLException {
        String sql = """
        INSERT INTO player.popularity (character_id, other_character_id, timestamp)
        VALUES (?, ?, ?)
        ON CONFLICT (character_id, other_character_id)
        DO UPDATE SET timestamp = EXCLUDED.timestamp
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            PopularityRecord pr = characterData.getPopularityRecord();
            int charId = characterData.getCharacterId();

            for (var entry : pr.getRecords().entrySet()) {
                stmt.setInt(1, charId);
                stmt.setInt(2, entry.getKey());
                stmt.setTimestamp(3, Timestamp.from(entry.getValue()));
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

}
