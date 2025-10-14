package kinoko.database.postgresql.type;

import kinoko.world.user.data.MiniGameRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class MiniGameRecordDao {

    /**
     * Loads MiniGameRecord for the specified character.
     *
     * Retrieves Omok and Memory game statistics including wins, ties, losses, and scores.
     *
     * @param conn the database connection to use
     * @param characterId the ID of the character
     * @return MiniGameRecord populated with game statistics
     * @throws SQLException if a database access error occurs
     */
    public static MiniGameRecord loadMiniGameRecord(Connection conn, int characterId) throws SQLException {
        MiniGameRecord record = new MiniGameRecord();

        String sql = """
            SELECT omok_wins, omok_ties, omok_losses, omok_score,
                   memory_wins, memory_ties, memory_losses, memory_score
            FROM player.minigame
            WHERE character_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    record.setOmokGameWins(rs.getInt("omok_wins"));
                    record.setOmokGameTies(rs.getInt("omok_ties"));
                    record.setOmokGameLosses(rs.getInt("omok_losses"));
                    record.setOmokGameScore(rs.getDouble("omok_score"));

                    record.setMemoryGameWins(rs.getInt("memory_wins"));
                    record.setMemoryGameTies(rs.getInt("memory_ties"));
                    record.setMemoryGameLosses(rs.getInt("memory_losses"));
                    record.setMemoryGameScore(rs.getDouble("memory_score"));
                }
            }
        }

        return record;
    }
}
