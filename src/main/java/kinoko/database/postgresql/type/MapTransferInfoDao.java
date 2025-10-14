package kinoko.database.postgresql.type;

import kinoko.world.user.data.MapTransferInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class MapTransferInfoDao {

    /**
     * Loads MapTransferInfo for the specified character.
     *
     * Retrieves the main map ID and legacy/old map ID for the character.
     *
     * @param conn the database connection to use
     * @param characterId the ID of the character
     * @return MapTransferInfo populated with map transfer data
     * @throws SQLException if a database access error occurs
     */
    public static MapTransferInfo loadMapTransferInfo(Connection conn, int characterId) throws SQLException {
        MapTransferInfo mti = new MapTransferInfo();

        String sql = "SELECT map_id, old_map_id FROM player.map_transfer WHERE character_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int mapId = rs.getInt("map_id");
                    int oldMapId = rs.getInt("old_map_id");

                    mti.getMapTransfer().add(mapId);       // main list
                    mti.getMapTransferEx().add(oldMapId);  // legacy/old map
                }
            }
        }

        return mti;
    }
}
