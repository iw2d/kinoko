package kinoko.database.postgresql.type;


import kinoko.world.GameConstants;
import kinoko.world.user.data.ConfigManager;
import kinoko.world.user.data.FuncKeyMapped;
import kinoko.world.user.data.FuncKeyType;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public final class ConfigManagerDao {

    /**
     * Loads the ConfigManager for the specified character.
     *
     * Retrieves pet consume settings, pet exception list, function key mapping,
     * quickslot key map, and associated macros.
     *
     * @param conn the database connection to use
     * @param characterId the ID of the character
     * @return a fully populated ConfigManager object
     * @throws SQLException if a database access error occurs
     */
    public static ConfigManager loadConfig(Connection conn, int characterId) throws SQLException {
        String sql = """
            SELECT pet_consume_item, pet_consume_mp_item, pet_exception_list,
                   func_key_types, func_key_ids, quickslot_key_map
            FROM player.config
            WHERE character_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return ConfigManager.defaults();
                }

                int petConsumeItem = rs.getInt("pet_consume_item");
                int petConsumeMpItem = rs.getInt("pet_consume_mp_item");

                // --- Pet exception list ---
                List<Integer> petExceptionList;
                Array petExArr = rs.getArray("pet_exception_list");
                if (petExArr != null) {
                    Integer[] arr = (Integer[]) petExArr.getArray();
                    petExceptionList = Arrays.asList(arr);
                } else {
                    petExceptionList = List.of();
                }

                // --- Function key map ---
                FuncKeyMapped[] funcKeyMap = new FuncKeyMapped[GameConstants.FUNC_KEY_MAP_SIZE];
                Array funcTypeArr = rs.getArray("func_key_types");
                Array funcIdArr = rs.getArray("func_key_ids");

                if (funcTypeArr != null && funcIdArr != null) {
                    Short[] typeValues = (Short[]) funcTypeArr.getArray();
                    Integer[] idValues = (Integer[]) funcIdArr.getArray();

                    for (int i = 0; i < funcKeyMap.length; i++) {
                        FuncKeyType type = FuncKeyType.getByValue(typeValues[i].byteValue());
                        int id = idValues[i];
                        funcKeyMap[i] = FuncKeyMapped.of(type, id);
                    }
                } else {
                    funcKeyMap = Arrays.copyOf(GameConstants.DEFAULT_FUNC_KEY_MAP, GameConstants.FUNC_KEY_MAP_SIZE);
                }

                // --- Quickslot key map ---
                int[] quickslotKeyMap;
                Array quickArr = rs.getArray("quickslot_key_map");
                if (quickArr != null) {
                    Integer[] arr = (Integer[]) quickArr.getArray();
                    quickslotKeyMap = Arrays.stream(arr).mapToInt(Integer::intValue).toArray();
                } else {
                    quickslotKeyMap = Arrays.copyOf(GameConstants.DEFAULT_QUICKSLOT_KEY_MAP, GameConstants.QUICKSLOT_KEY_MAP_SIZE);
                }

                ConfigManager cm = new ConfigManager(petConsumeItem, petConsumeMpItem, petExceptionList, funcKeyMap, quickslotKeyMap);

                // Load macros from SkillMacrosDao
                cm.updateMacroSysData(SkillMacrosDao.loadMacros(conn, characterId));

                return cm;
            }
        }
    }
}
