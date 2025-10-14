package kinoko.database.postgresql.type;

import kinoko.server.cashshop.Gift;
import kinoko.world.item.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public final class GiftDao {
    /**
     * Loads a Gift object from the current row of the ResultSet.
     *
     * @param rs the ResultSet positioned at the gift row
     * @return the loaded Gift object
     * @throws SQLException if a database access error occurs
     */
    public static Gift loadGift(ResultSet rs) throws SQLException {
        return new Gift(
                rs.getLong("item_sn"),
                rs.getInt("item_id"),
                rs.getInt("commodity_id"),
                rs.getInt("sender_id"),
                rs.getString("sender_name"),
                rs.getString("sender_message"),
                rs.getLong("pair_gift_sn")
        );
    }


    /**
     * Retrieves all gifts for a given receiver ID.
     *
     * @param conn        the active SQL connection
     * @param receiverId  the ID of the character receiving gifts
     * @return a list of Gift objects for the receiver
     * @throws SQLException if a database access error occurs
     */
    public static List<Gift> getGiftsByReceiverId(Connection conn, int receiverId) throws SQLException {
        List<Gift> gifts = new ArrayList<>();
        String sql = """
            SELECT g.item_sn, fi.item_id, g.commodity_id, g.sender_id,
                g.sender_name, g.sender_message, g.pair_gift_sn
            FROM gift.gifts g
            JOIN item.full_item fi ON fi.item_sn = g.item_sn
            WHERE g.receiver_id = ?
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    gifts.add(loadGift(rs));
                }
            }
        }
        return gifts;
    }

    /**
     * Retrieves a gift by its item serial number.
     *
     * @param conn    the active SQL connection
     * @param itemSn  the item serial number of the gift
     * @return an Optional containing the Gift if found, or empty if not
     * @throws SQLException if a database access error occurs
     */
    public static Optional<Gift> getGiftByItemSn(Connection conn, long itemSn) throws SQLException {
        String sql = """
            SELECT g.item_sn, fi.item_id, g.commodity_id, g.sender_id,
                g.sender_name, g.sender_message, g.pair_gift_sn
            FROM gift.gifts g
            JOIN item.full_item fi ON fi.item_sn = g.item_sn
            WHERE g.item_sn = ?
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, itemSn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadGift(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Inserts a new gift into the database.
     *
     * If the gift does not have a valid item serial number, a new item will be created first.
     *
     * @param conn       the active SQL connection
     * @param gift       the gift to insert
     * @param receiverId the ID of the receiver
     * @return true if the gift was successfully inserted, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean insertGift(Connection conn, Gift gift, int receiverId) throws SQLException {
        String sql = """
            INSERT INTO gift.gifts (item_sn, receiver_id, commodity_id, sender_id, sender_name, sender_message, pair_gift_sn)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (item_sn) DO NOTHING
            """;

        long itemSN = gift.getGiftSn();
        if (itemSN <= 0) {  // create a new item.
            Item basicItem = new Item(gift.getItemId(), (short) 1);
            ItemDao.createNewItem(conn, basicItem);
            itemSN = basicItem.getItemSn();
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, itemSN);
            stmt.setInt(2, receiverId);
            stmt.setInt(3, gift.getCommodityId());
            stmt.setInt(4, gift.getSenderId());
            stmt.setString(5, gift.getSenderName());
            stmt.setString(6, gift.getSenderMessage());
            stmt.setLong(7, gift.getPairItemSn());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a gift from the database by its item serial number.
     *
     * @param conn the active SQL connection
     * @param gift the gift to delete
     * @return true if the gift was successfully deleted, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean deleteGift(Connection conn, Gift gift) throws SQLException {
        String sql = "DELETE FROM gift.gifts WHERE item_sn = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, gift.getGiftSn());
            return stmt.executeUpdate() > 0;
        }
    }
}

