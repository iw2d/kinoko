package kinoko.database.postgresql.type;

import kinoko.server.cashshop.CashItemInfo;
import kinoko.world.item.Item;
import kinoko.world.user.Locker;

import java.sql.*;
import java.util.Collection;

public class LockerDao {

    /**
     * Saves all items in the given Locker for a specific account.
     *
     * For each item in the locker, a new item SN will be generated if it doesn't exist.
     * Items are then inserted into the `account.locker_item` table with their slot and optional commodity_id.
     *
     * @param conn      the database connection to use
     * @param accountId the ID of the account whose locker is being saved
     * @param locker    the Locker object containing items to save
     * @throws SQLException if a database error occurs
     */
    public static void save(Connection conn, int accountId, Locker locker) throws SQLException {
        Collection<CashItemInfo> cashItems = locker.getCashItems();
        Collection<Item> allItems = cashItems.stream()
                .map(CashItemInfo::getItem)
                .toList();

        deleteUnusedItems(conn, accountId, allItems);
        ItemDao.saveItemsBatch(conn, allItems);  // insert/update

        String sql = """
            INSERT INTO account.locker_item (account_id, slot, item_sn, commodity_id)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (account_id, slot) DO UPDATE
            SET item_sn = EXCLUDED.item_sn,
                commodity_id = EXCLUDED.commodity_id
        """;  // There can be conflicts if items swapped slots

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int slot = 0;
            for (CashItemInfo cash : cashItems) {
                stmt.setInt(1, accountId);
                stmt.setInt(2, slot++);
                stmt.setLong(3, cash.getItem().getItemSn());
                stmt.setInt(4, cash.getCommodityId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Remove locker items that are no longer in use for a specific account.
     *
     * If the provided collection of CashItemInfo is non-empty, this method deletes all rows in
     * `account.locker_item` for the given account whose `item_sn` is not present in the collection.
     * If the collection is empty, all locker items for the account are deleted.
     *
     * @param conn      the database connection to use
     * @param accountId the ID of the account whose locker should be cleaned
     * @param items     the collection of CashItemInfo to retain; all others will be deleted
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
                    "DELETE FROM account.locker_item WHERE account_id = ? AND item_sn <> ALL (?)")) {
                deleteStmt.setInt(1, accountId);
                Array sqlArray = conn.createArrayOf("bigint", itemSnArray);
                deleteStmt.setArray(2, sqlArray);
                deleteStmt.executeUpdate();
            }
        } else {
            // delete all items if the locker collection is empty
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM account.locker_item WHERE account_id = ?")) {
                deleteStmt.setInt(1, accountId);
                deleteStmt.executeUpdate();
            }
        }
    }

    /**
     * Loads the Locker for a specific account.
     *
     * Retrieves all locker items joined with the item table. Constructs CashItemInfo
     * objects and adds them to a Locker. The resulting Locker contains all saved items.
     *
     * @param conn      the active database connection
     * @param accountId the ID of the account whose locker should be loaded
     * @return the Locker with all items for the account
     * @throws SQLException if a database error occurs
     */
    public static Locker load(Connection conn, int accountId) throws SQLException {
        Locker locker = new Locker();
        String sql = """
            SELECT li.slot, li.item_sn, li.commodity_id, i.item_id, i.quantity
            FROM account.locker_item li
            JOIN item.items i ON li.item_sn = i.item_sn
            WHERE li.account_id = ? ORDER BY li.slot
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item(rs.getInt("item_id"), (short) rs.getInt("quantity"));
                    CashItemInfo info = new CashItemInfo(
                            item,
                            rs.getInt("commodity_id"),
                            accountId, // account owner
                            -1,        // character owner unknown at this point
                            null
                    );
                    locker.addCashItem(info);
                }
            }
        }

        return locker;
    }
}
