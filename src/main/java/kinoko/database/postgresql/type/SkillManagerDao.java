package kinoko.database.postgresql.type;


import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.CharacterData;

import java.sql.*;
import java.time.Instant;

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

    /**
     * Saves or updates all skill-related data for the given character, including:
     * - Skill levels and master levels
     * - Active skill cooldowns
     * Uses UPSERT logic to maintain consistency between client and server skill data.
     *
     * @param conn the active database connection
     * @param characterData the character whose skills should be saved
     * @throws SQLException if a database access error occurs
     */
    public static void saveCharacterSkills(Connection conn, CharacterData characterData) throws SQLException {
        String skillRecordSql = """
        INSERT INTO player.skill_record (character_id, skill_id, level, master_level)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (character_id, skill_id) 
        DO UPDATE SET level = EXCLUDED.level, master_level = EXCLUDED.master_level
        """;

        try (PreparedStatement stmt = conn.prepareStatement(skillRecordSql)) {
            for (SkillRecord sr : characterData.getSkillManager().getSkillRecords()) {
                stmt.setInt(1, characterData.getCharacterId());
                stmt.setInt(2, sr.getSkillId());
                stmt.setInt(3, sr.getSkillLevel());
                stmt.setInt(4, sr.getMasterLevel());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }

        // Save skill cooltimes
        String skillCooltimeSql = """
        INSERT INTO player.skill_cooltime (character_id, skill_id, cooldown_end)
        VALUES (?, ?, ?)
        ON CONFLICT (character_id, skill_id)
        DO UPDATE SET cooldown_end = EXCLUDED.cooldown_end
        """;
        try (PreparedStatement stmt = conn.prepareStatement(skillCooltimeSql)) {
            for (var entry : characterData.getSkillManager().getSkillCooltimes().entrySet()) {
                int skillId = entry.getKey();
                Instant endTime = entry.getValue();
                stmt.setInt(1, characterData.getCharacterId());
                stmt.setInt(2, skillId);
                stmt.setTimestamp(3, Timestamp.from(endTime));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
