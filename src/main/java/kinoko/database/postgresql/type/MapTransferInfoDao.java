package kinoko.database.postgresql.type;

import kinoko.world.user.CharacterData;
import kinoko.world.user.data.MapTransferInfo;

import java.sql.*;
import java.util.Arrays;

public final class MapTransferInfoDao {

    /**
     * Loads MapTransferInfo for the specified character.
     *
     * Retrieves the map_ids and old_map_ids arrays from the database and populates
     * the corresponding lists in a MapTransferInfo object.
     *
     * @param conn the database connection to use
     * @param characterId the ID of the character
     * @return MapTransferInfo populated with the character's map transfer lists
     * @throws SQLException if a database access error occurs
     */
    public static MapTransferInfo loadMapTransferInfo(Connection conn, int characterId) throws SQLException {
        MapTransferInfo mti = new MapTransferInfo();

        String sql = "SELECT map_ids, old_map_ids FROM player.map_transfer WHERE character_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Array mapArray = rs.getArray("map_ids");
                    Array oldMapArray = rs.getArray("old_map_ids");

                    if (mapArray != null) {
                        Integer[] mapIds = (Integer[]) mapArray.getArray();
                        mti.getMapTransfer().addAll(Arrays.asList(mapIds));
                    }
                    if (oldMapArray != null) {
                        Integer[] oldMapIds = (Integer[]) oldMapArray.getArray();
                        mti.getMapTransferEx().addAll(Arrays.asList(oldMapIds));
                    }
                }
            }
        }

        return mti;
    }

    /**
     * Saves MapTransferInfo for the specified character.
     *
     * Replaces or inserts the character’s map transfer data in the database.
     * Converts the map transfer lists into SQL integer arrays and performs an UPSERT.
     *
     * @param conn the database connection to use
     * @param characterData CharacterData instance.
     * @throws SQLException if a database access error occurs
     */
    public static void saveMapTransferInfo(Connection conn, CharacterData characterData) throws SQLException {
        MapTransferInfo mapTransferInfo = characterData.getMapTransferInfo();
        int characterId = characterData.getCharacterId();

        String sql = """
        INSERT INTO player.map_transfer (character_id, map_ids, old_map_ids)
        VALUES (?, ?, ?)
        ON CONFLICT (character_id)
        DO UPDATE SET map_ids = EXCLUDED.map_ids, old_map_ids = EXCLUDED.old_map_ids
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Convert Java lists to SQL array
            Array mapArray = conn.createArrayOf("INTEGER", mapTransferInfo.getMapTransfer().toArray());
            Array oldMapArray = conn.createArrayOf("INTEGER", mapTransferInfo.getMapTransferEx().toArray());

            stmt.setInt(1, characterId);
            stmt.setArray(2, mapArray);
            stmt.setArray(3, oldMapArray);
            stmt.executeUpdate();
        }
    }
}
