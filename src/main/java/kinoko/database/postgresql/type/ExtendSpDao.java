package kinoko.database.postgresql.type;


import kinoko.world.user.stat.ExtendSp;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public final class ExtendSpDao {
    /**
     * Upserts all entries from ExtendSp into player.extend_sp.
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
     * Loads ExtendSp for a given character.
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
