package kinoko.database.postgresql.type;

import kinoko.world.item.RingData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class RingDataDao {
    /**
     * Inserts or updates RingData for a given item_sn.
     * If an entry already exists, it will be updated instead.
     */
    public static void upsertRingData(Connection conn, long itemSn, RingData ringData) throws SQLException {
        if (ringData == null) {
            return; // nothing to save
        }

        String sql = """
            INSERT INTO item.ring_data (
                item_sn, pair_character_id, pair_character_name, pair_item_sn
            )
            VALUES (?, ?, ?, ?)
            ON CONFLICT (item_sn)
            DO UPDATE SET
                pair_character_id = EXCLUDED.pair_character_id,
                pair_character_name = EXCLUDED.pair_character_name,
                pair_item_sn = EXCLUDED.pair_item_sn
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, itemSn);
            stmt.setInt(2, ringData.getPairCharacterId());
            stmt.setString(3, ringData.getPairCharacterName());
            stmt.setLong(4, ringData.getPairItemSn());
            stmt.executeUpdate();
        }
    }
}
