package kinoko.database.postgresql.type;

import kinoko.world.item.EquipData;
import kinoko.world.item.Item;
import kinoko.world.item.PetData;
import kinoko.world.item.RingData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Collection;

public class ItemDao {
    private static final Logger log = LogManager.getLogger(ItemDao.class);

    /**
     * Inserts a new item into the `item.items` table and returns its generated item_sn.
     * <p>
     * If the insertion is successful, the auto-generated item_sn is also set in the provided Item object.
     * This method is useful when creating a new item that does not yet have an item_sn.
     *
     * @param conn the active database connection
     * @param item the Item object to insert
     * @return the auto-generated item_sn for the newly inserted item
     * @throws SQLException if the insertion fails or the item_sn cannot be generated
     */
    public static long createNewItem(Connection conn, Item item) throws SQLException {

        String sql = """
                    INSERT INTO item.items (item_id, quantity, attribute, title, date_expire)
                    VALUES (?, ?, ?, ?, ?)
                    RETURNING item_sn
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getItemId());
            stmt.setInt(2, item.getQuantity());
            stmt.setShort(3, item.getAttribute());
            stmt.setString(4, item.getTitle());
            stmt.setTimestamp(5, item.getDateExpire() != null ? Timestamp.from(item.getDateExpire()) : null);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long generatedSn = rs.getLong("item_sn");
                    item.setItemSn(generatedSn); // store it in the item object
                    EquipDataDao.upsertEquipData(conn, generatedSn, item.getEquipData());
                    return generatedSn;
                } else {
                    throw new SQLException("Failed to generate item_sn for new item");
                }
            }
        }
    }

    /**
     * Saves a collection of items to the database in batch.
     * <p>
     * For each item, this method checks if it already has an item_sn:
     * - If the item_sn is missing (<=0), a new one is generated and the item is inserted.
     * - If the item_sn exists, the item is updated with the latest quantity, attributes, title, and expiration date.
     * <p>
     * This approach ensures efficient batch inserts for new items while keeping existing items up to date.
     *
     * @param conn  the active database connection
     * @param items the collection of items to insert or update
     * @throws SQLException if any SQL error occurs during insert or update
     */
    public static void saveItemsBatch(Connection conn, Collection<Item> items) throws SQLException {
        if (items.isEmpty()) return;

        String sqlInsert = """
                    INSERT INTO item.items (item_sn, item_id, quantity, attribute, title, date_expire)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        String sqlUpdate = """
                    UPDATE item.items
                    SET quantity = ?, attribute = ?, title = ?, date_expire = ?
                    WHERE item_sn = ?
                """;

        try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
            for (Item item : items) {
                long itemSn = item.getItemSn();

                // Generate new item_sn if needed
                if (item.hasNoSN()) {
                    try (PreparedStatement seqStmt = conn.prepareStatement(
                            "SELECT nextval(pg_get_serial_sequence('item.items', 'item_sn'))");
                         ResultSet rs = seqStmt.executeQuery()) {
                        rs.next();
                        itemSn = rs.getLong(1);
                        item.setItemSn(itemSn);
                    }

                    stmtInsert.setLong(1, itemSn);
                    stmtInsert.setInt(2, item.getItemId());
                    stmtInsert.setInt(3, item.getQuantity());
                    stmtInsert.setShort(4, item.getAttribute());
                    stmtInsert.setString(5, item.getTitle());
                    stmtInsert.setTimestamp(6, item.getDateExpire() != null ? Timestamp.from(item.getDateExpire()) : null);
                    stmtInsert.addBatch();
                } else {
                    try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                        stmtUpdate.setInt(1, item.getQuantity());
                        stmtUpdate.setShort(2, item.getAttribute());
                        stmtUpdate.setString(3, item.getTitle());
                        stmtUpdate.setTimestamp(4, item.getDateExpire() != null ? Timestamp.from(item.getDateExpire()) : null);
                        stmtUpdate.setLong(5, itemSn);
                        stmtUpdate.executeUpdate();
                    }
                }
            }

            // Execute all insert batches
            stmtInsert.executeBatch();
            // Update all EquipData
            EquipDataDao.saveEquipDataBatch(conn, items);
        }
    }

    public static Item from(ResultSet rs) throws SQLException {
        long itemSn = rs.getLong("item_sn");
        int itemId = rs.getInt("item_id");
        short quantity = rs.getShort("quantity");
        short attribute = rs.getShort("attribute");
        String title = rs.getString("title");
        Timestamp dateExpireTs = rs.getTimestamp("date_expire");

        EquipData equipData = new EquipData(
                rs.getShort("inc_str"),
                rs.getShort("inc_dex"),
                rs.getShort("inc_int"),
                rs.getShort("inc_luk"),
                rs.getShort("inc_max_hp"),
                rs.getShort("inc_max_mp"),
                rs.getShort("inc_pad"),
                rs.getShort("inc_mad"),
                rs.getShort("inc_pdd"),
                rs.getShort("inc_mdd"),
                rs.getShort("inc_acc"),
                rs.getShort("inc_eva"),
                rs.getShort("inc_craft"),
                rs.getShort("inc_speed"),
                rs.getShort("inc_jump"),
                rs.getByte("ruc"),
                rs.getByte("cuc"),
                rs.getInt("iuc"),
                rs.getByte("chuc"),
                rs.getByte("grade"),
                rs.getShort("option_1"),
                rs.getShort("option_2"),
                rs.getShort("option_3"),
                rs.getShort("socket_1"),
                rs.getShort("socket_2"),
                rs.getByte("level_up_type"),
                rs.getByte("level"),
                rs.getInt("exp"),
                rs.getInt("durability")
        );

        PetData petData = null;
        if (rs.getObject("pet_name") != null) {
            petData = new PetData(
                    rs.getString("pet_name"),
                    rs.getByte("pet_level"),
                    rs.getByte("fullness"),
                    rs.getShort("tameness"),
                    rs.getShort("pet_skill"),
                    rs.getShort("pet_attribute"),
                    rs.getInt("remain_life")
            );
        }

        // RingData
        RingData ringData = null;
        if (rs.getObject("pair_character_id") != null) {
            ringData = new RingData(
                    rs.getInt("pair_character_id"),
                    rs.getString("pair_character_name"),
                    rs.getLong("pair_item_sn")
            );
        }

        Item item = new Item(
                itemId,
                quantity,
                itemSn,
                false, // cash flag
                attribute,
                title,
                dateExpireTs != null ? dateExpireTs.toInstant() : null,
                equipData,
                petData,
                ringData
        );
        return item;
    }

    /**
     * Cleans up invalid items from the database that no longer have valid references.
     * <p>
     * In the PostgreSQL implementation, all items are stored in {@code item.Items}, regardless of
     * whether they are currently held by a player (inventory, trunk, locker, wishlist, gifted)
     * or not. This can lead to orphaned item records when items are dropped, since dropped
     * items are not tracked by the database.
     * <p>
     * This method queries and removes items that are not referenced anywhere else, ensuring
     * synchronization between the in-game state and the persistent database state. This function
     * typically called during server initialization, when no dropped items exist.
     * BE CAREFUL to run this in any other situation.
     *
     * @param conn the active SQL connection used to perform cleanup operations
     */
    public static void cleanupInvalidItems(Connection conn) throws SQLException {
        String sql = """
                    DELETE FROM item.items i
                    WHERE NOT EXISTS (SELECT 1 FROM item.equip_data e WHERE e.item_sn = i.item_sn)
                      AND NOT EXISTS (SELECT 1 FROM item.pet_data p WHERE p.item_sn = i.item_sn)
                      AND NOT EXISTS (SELECT 1 FROM item.ring_data r WHERE r.item_sn = i.item_sn)
                      AND NOT EXISTS (SELECT 1 FROM player.inventory inv WHERE inv.item_sn = i.item_sn)
                      AND NOT EXISTS (SELECT 1 FROM account.trunk_item t WHERE t.item_sn = i.item_sn)
                      AND NOT EXISTS (SELECT 1 FROM account.locker_item l WHERE l.item_sn = i.item_sn)
                      AND NOT EXISTS (SELECT 1 FROM gift.gifts g WHERE g.item_sn = i.item_sn);
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsDeleted = stmt.executeUpdate();
            log.info("Cleaned up {} items with no references.", rowsDeleted);
        }
    }
}