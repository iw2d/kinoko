package kinoko.database.postgresql.type;


import kinoko.world.user.stat.ExtendSp;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public final class ExtendSpDao {
    /**
     * Inserts or updates extended SP data for a character in the database.
     * If an entry with the same character ID and job level already exists, the SP value is updated.
     * Otherwise, a new record is inserted.
     * Skips execution if the provided ExtendSp object is null or empty.
     *
     * @param conn the active database connection
     * @param characterId the ID of the character whose SP data is being stored
     * @param extendSp the ExtendSp object containing job-level-to-SP mappings
     * @throws SQLException if a database access error occurs
     */
    public static void upsertExtendSp(Connection conn, int characterId, ExtendSp extendSp) throws SQLException {
        if (extendSp == null || extendSp.getMap().isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO player.extend_sp (character_id, job_level, sp)
            VALUES (?, ?, ?)
            ON CONFLICT (character_id, job_level)
            DO UPDATE SET sp = EXCLUDED.sp
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> entry : extendSp.getMap().entrySet()) {
                stmt.setInt(1, characterId);
                stmt.setInt(2, entry.getKey());
                stmt.setInt(3, entry.getValue());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Loads a character's extended SP data from the database.
     * Retrieves all job level–SP pairs associated with the given character ID and constructs an ExtendSp object from them.
     * If no records are found, an empty ExtendSp instance is returned.
     *
     * @param conn the active database connection
     * @param characterId the ID of the character whose extended SP data is being loaded
     * @return an ExtendSp object containing the character's job-level-to-SP mappings, or empty if none exist
     * @throws SQLException if a database access error occurs
     */
    public static ExtendSp loadExtendSp(Connection conn, int characterId) throws SQLException {
        String sql = """
            SELECT job_level, sp
            FROM player.extend_sp
            WHERE character_id = ?
        """;

        Map<Integer, Integer> map = new HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("job_level"), rs.getInt("sp"));
                }
            }
        }

        return ExtendSp.from(map);
    }
}
