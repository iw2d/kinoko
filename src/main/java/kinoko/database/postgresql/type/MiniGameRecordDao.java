package kinoko.database.postgresql.type;

import kinoko.world.user.CharacterData;
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

    /**
     * Saves MiniGameRecord for the specified character.
     *
     * Updates the Omok and Memory game statistics in the database.
     * If a record for the character does not exist, it inserts a new one.
     *
     * @param conn the database connection to use
     * @param characterData CharacterData object
     * @throws SQLException if a database access error occurs
     */
    public static void saveMiniGameRecord(Connection conn, CharacterData characterData) throws SQLException {
        int characterId = characterData.getCharacterId();
        MiniGameRecord record = characterData.getMiniGameRecord();

        String sql = """
        INSERT INTO player.minigame 
            (character_id, omok_wins, omok_ties, omok_losses, omok_score,
             memory_wins, memory_ties, memory_losses, memory_score)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (character_id)
        DO UPDATE SET 
            omok_wins = EXCLUDED.omok_wins,
            omok_ties = EXCLUDED.omok_ties,
            omok_losses = EXCLUDED.omok_losses,
            omok_score = EXCLUDED.omok_score,
            memory_wins = EXCLUDED.memory_wins,
            memory_ties = EXCLUDED.memory_ties,
            memory_losses = EXCLUDED.memory_losses,
            memory_score = EXCLUDED.memory_score
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            stmt.setInt(2, record.getOmokGameWins());
            stmt.setInt(3, record.getOmokGameTies());
            stmt.setInt(4, record.getOmokGameLosses());
            stmt.setDouble(5, record.getOmokGameScore());
            stmt.setInt(6, record.getMemoryGameWins());
            stmt.setInt(7, record.getMemoryGameTies());
            stmt.setInt(8, record.getMemoryGameLosses());
            stmt.setDouble(9, record.getMemoryGameScore());

            stmt.executeUpdate();
        }
    }
}
