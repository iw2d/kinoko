package kinoko.database.postgresql.type;


import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;

import java.sql.*;

public final class SkillManagerDao {

    /**
     * Loads the SkillManager for the specified character.
     *
     * Retrieves skill cooldowns and skill records (level & master level)
     * from the database for the given character ID.
     *
     * @param conn the database connection to use
     * @param characterId the ID of the character
     * @return a fully populated SkillManager object
     * @throws SQLException if a database access error occurs
     */
    public static SkillManager loadSkillCooltimesAndRecords(Connection conn, int characterId) throws SQLException {
        SkillManager sm = new SkillManager();

        // Load skill cooldowns
        String cooldownSql = "SELECT skill_id, cooldown_end FROM player.skill_cooltime WHERE character_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(cooldownSql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int skillId = rs.getInt("skill_id");
                    Timestamp cooldownEnd = rs.getTimestamp("cooldown_end");
                    if (cooldownEnd != null) {
                        sm.getSkillCooltimes().put(skillId, cooldownEnd.toInstant());
                    }
                }
            }
        }

        // Load skill records
        String recordSql = "SELECT skill_id, level, master_level FROM player.skill_record WHERE character_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(recordSql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int skillId = rs.getInt("skill_id");
                    int level = rs.getInt("level");
                    int masterLevel = rs.getInt("master_level");
                    SkillRecord record = new SkillRecord(skillId, level, masterLevel);
                    sm.addSkill(record);
                }
            }
        }

        return sm;
    }
}
