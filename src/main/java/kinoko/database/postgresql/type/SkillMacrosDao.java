package kinoko.database.postgresql.type;

import kinoko.world.GameConstants;
import kinoko.world.user.data.SingleMacro;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SkillMacrosDao {
    /**
     * Upserts a list of macros for a character.
     */
    public static void upsertMacros(Connection conn, int characterId, List<SingleMacro> macros) throws SQLException {
        if (macros == null || macros.isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO player.skill_macros (character_id, macro_index, name, mute, skills)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (character_id, macro_index)
            DO UPDATE SET name = EXCLUDED.name,
                          mute = EXCLUDED.mute,
                          skills = EXCLUDED.skills
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < macros.size(); i++) {
                SingleMacro macro = macros.get(i);
                stmt.setInt(1, characterId);
                stmt.setInt(2, i); // macro_index 0-4
                stmt.setString(3, macro.getName());
                stmt.setBoolean(4, macro.isMute());
                stmt.setArray(5, conn.createArrayOf("int",
                        Arrays.stream(macro.getSkills()).boxed().toArray(Integer[]::new)));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Loads all macros for a character.
     */
    public static List<SingleMacro> loadMacros(Connection conn, int characterId) throws SQLException {
        String sql = "SELECT macro_index, name, mute, skills FROM player.skill_macros WHERE character_id = ?";
        SingleMacro[] macros = new SingleMacro[5];

        // Initialize all slots with default blank macros
        for (int i = 0; i < GameConstants.MACRO_SYS_DATA_SIZE; i++) {
            macros[i] = new SingleMacro("", false, new int[GameConstants.MACRO_SKILL_COUNT]);
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int index = rs.getInt("macro_index");
                    if (index < 0 || index >= GameConstants.MACRO_SYS_DATA_SIZE) continue; // safety check
                    String name = rs.getString("name");
                    boolean mute = rs.getBoolean("mute");
                    Integer[] skillsArray = (Integer[]) rs.getArray("skills").getArray();
                    int[] skills = Arrays.stream(skillsArray).mapToInt(Integer::intValue).toArray();
                    macros[index] = new SingleMacro(name, mute, skills);
                }
            }
        }

        return Arrays.asList(macros);
    }
}
