package kinoko.database.postgresql.type;

import kinoko.world.user.CharacterData;
import kinoko.world.user.data.WildHunterInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class WildHunterInfoDao {
    /**
     * Loads WildHunterInfo for the specified character.
     *
     * Retrieves riding type and up to 5 captured mobs for the character.
     *
     * @param characterId the ID of the character
     * @return WildHunterInfo populated with riding type and captured mobs
     * @throws SQLException if a database access error occurs
     */
    public static WildHunterInfo loadWildHunterInfo(Connection conn, int characterId) throws SQLException {
        WildHunterInfo wh = new WildHunterInfo();

        String sqlRiding = "SELECT riding_type FROM player.wild_hunter WHERE character_id = ?";
        String sqlMobs = "SELECT mob_id FROM player.wild_hunter_mob WHERE character_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sqlRiding)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    wh.setRidingType(rs.getInt("riding_type"));
                }
            }
        }

        // Load captured mobs
        try (PreparedStatement stmt = conn.prepareStatement(sqlMobs)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    wh.getCapturedMobs().add(rs.getInt("mob_id"));
                    if (wh.getCapturedMobs().size() >= 5) break; // enforce max 5
                }
            }
        }

        return wh;
    }

    /**
     * Saves WildHunterInfo for the specified character.
     *
     * Updates the riding type and captured mobs for the character.
     * Existing captured mobs are cleared and replaced with the provided list (up to 5 mobs).
     *
     * @param conn the database connection to use
     * @param characterData The CharacterData object
     * @throws SQLException if a database access error occurs
     */
    public static void saveWildHunterInfo(Connection conn, CharacterData characterData) throws SQLException {
        int characterId = characterData.getCharacterId();
        WildHunterInfo wh = characterData.getWildHunterInfo();

        // Save riding type
        String sqlRiding = """
        INSERT INTO player.wild_hunter (character_id, riding_type)
        VALUES (?, ?)
        ON CONFLICT (character_id)
        DO UPDATE SET riding_type = EXCLUDED.riding_type
    """;
        try (PreparedStatement stmt = conn.prepareStatement(sqlRiding)) {
            stmt.setInt(1, characterId);
            stmt.setInt(2, wh.getRidingType());
            stmt.executeUpdate();
        }

        // Clear existing captured mobs
        String sqlDeleteMobs = "DELETE FROM player.wild_hunter_mob WHERE character_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteMobs)) {
            stmt.setInt(1, characterId);
            stmt.executeUpdate();
        }

        // Insert captured mobs (up to 5)
        String sqlInsertMob = "INSERT INTO player.wild_hunter_mob (character_id, mob_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsertMob)) {
            int count = 0;
            for (Integer mobId : wh.getCapturedMobs()) {
                if (count++ >= 5) break;
                stmt.setInt(1, characterId);
                stmt.setInt(2, mobId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
