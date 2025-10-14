package kinoko.database.postgresql.type;

import kinoko.world.quest.QuestManager;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;

import java.sql.*;
import java.time.Instant;

public final class QuestManagerDao {

    /**
     * Loads the QuestManager for the specified character.
     *
     * Retrieves all quest records including quest ID, status, progress,
     * and completed time for the character.
     *
     * @param conn the database connection to use
     * @param characterId the ID of the character
     * @return a fully populated QuestManager object
     * @throws SQLException if a database access error occurs
     */
    public static QuestManager loadQuestRecords(Connection conn, int characterId) throws SQLException {
        QuestManager qm = new QuestManager();

        String sql = """
            SELECT quest_id, status, progress, completed_time
            FROM player.quest_record
            WHERE character_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int questId = rs.getInt("quest_id");
                    int statusInt = rs.getInt("status");
                    QuestState state = QuestState.getByValue(statusInt);

                    String value = rs.getString("progress");

                    Timestamp completedTs = rs.getTimestamp("completed_time");
                    Instant completedTime = completedTs != null ? completedTs.toInstant() : null;

                    QuestRecord record = new QuestRecord(questId, state, value, completedTime);
                    qm.addQuestRecord(record);
                }
            }
        }

        return qm;
    }
}
