package kinoko.database.postgresql.type;

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
}
