package kinoko.database.postgresql.type;


import kinoko.provider.item.ItemInfo;
import kinoko.server.ServerConfig;
import kinoko.world.item.*;

import java.sql.*;
import java.util.Collection;

public class TrunkDao {

    /**
     * Saves all items in the given Trunk (Account Storage) for a specific account.

     * For each item in the trunk, a new item SN will be generated if it doesn't exist.
     * Items are then inserted into the `account.trunk_item` table with their slot.
     *
     * @param conn      the database connection to use
     * @param accountId the ID of the account whose trunk is being saved
     * @param trunk     the Trunk object containing items to save
     * @throws SQLException if a database error occurs
     */
    public static void save(Connection conn, int accountId, Trunk trunk) throws SQLException {
        String sql = """
        INSERT INTO account.trunk_item (account_id, slot, item_sn)
        VALUES (?, ?, ?)
        ON CONFLICT (account_id, slot)
        DO UPDATE SET item_sn = EXCLUDED.item_sn
        """; // There can be conflicts if items swapped slots
        Collection<Item> items = trunk.getItems();
        deleteUnusedItems(conn, accountId, items);
        ItemDao.saveItemsBatch(conn, items);  // insert/update

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int slot = 0;
            for (Item item : items) {
                stmt.setInt(1, accountId);
                stmt.setInt(2, slot++);
                stmt.setLong(3, item.getItemSn());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Remove trunk items that are no longer in use for a specific account.
     *
     * If the provided collection of items is non-empty, this method deletes all rows in
     * `account.trunk_item` for the given account whose `item_sn` is not present in the collection.
     * CAUTION: If the collection is empty, it deletes all trunk items for the account.
     * This is because it is expected for the account's entire trunk to be passed in.
     *
     * @param conn      the database connection to use
     * @param accountId the ID of the account whose trunk should be cleaned
     * @param items     the collection of items to retain; all others will be deleted
     * @throws SQLException if a database access error occurs
     */
    private static void deleteUnusedItems(Connection conn, int accountId, Collection<Item> items) throws SQLException {
        if (!items.isEmpty()) {
            Long[] itemSnArray = items.stream()
                    .filter(item -> !item.hasNoSN())   // skip items with no SN, aka has not been created in item.Items yet.
                    .map(Item::getItemSn)
                    .toArray(Long[]::new);

            if (itemSnArray.length == 0) {
                return; // nothing to delete
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM account.trunk_item WHERE account_id = ? AND item_sn <> ALL (?)")) {
                deleteStmt.setInt(1, accountId);
                Array sqlArray = conn.createArrayOf("bigint", itemSnArray);
                deleteStmt.setArray(2, sqlArray);
                deleteStmt.executeUpdate();
            }
        } else {
            // delete all items if the trunk collection is empty
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM account.trunk_item WHERE account_id = ?")) {
                deleteStmt.setInt(1, accountId);
                deleteStmt.executeUpdate();
            }
        }
    }

    public static Trunk load(Connection conn, int accountId) throws SQLException {
        String accountSql = "SELECT trunk_size, trunk_money FROM account.accounts WHERE id = ?";
        String itemsSql = """
        SELECT ti.slot, fi.*
        FROM account.trunk_item ti
        JOIN item.full_item fi ON ti.item_sn = fi.item_sn
        WHERE ti.account_id = ?
        """;


        int trunkSize = ServerConfig.TRUNK_BASE_SLOTS;
        int trunkMoney = 0;
        Trunk trunk;


        try (PreparedStatement stmt = conn.prepareStatement(accountSql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    trunkSize = rs.getInt("trunk_size");
                    trunkMoney = rs.getInt("trunk_money");
                }
            }
        }

        // Initialize trunk with proper size
        trunk = new Trunk(trunkSize);
        trunk.setMoney(trunkMoney);

        try (PreparedStatement stmt = conn.prepareStatement(itemsSql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int slot = rs.getInt("slot");
                    Item item = ItemDao.from(rs);
//                    trunk.getItems().add(item);
                    trunk.addItem(item);
                }
            }
        }

        return trunk;
    }
}