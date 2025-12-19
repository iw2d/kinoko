package kinoko.database.postgresql.type;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WishlistDao {

    /**
     * Saves the account's wishlist to the database.
     *
     * Existing wishlist entries for the account are deleted first.
     * Each item_id is then inserted into the `account.wishlist` table
     * with its slot. If a slot already exists, the item_id is updated.
     *
     * @param conn    the active database connection
     * @param wishlist The wishlist to save.
     * @throws SQLException if a database error occurs
     */
    public static void save(Connection conn, int accountId, List<Integer> wishlist) throws SQLException {
        deleteUnusedItems(conn, accountId, wishlist);

        String sqlInsert = """
            INSERT INTO account.wishlist (account_id, slot, item_id)
            VALUES (?, ?, ?)
            ON CONFLICT (account_id, slot) DO UPDATE
            SET item_id = EXCLUDED.item_id
        """;
        try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
            int slot = 0;
            for (Integer itemId : wishlist) {
                insertStmt.setInt(1, accountId);
                insertStmt.setInt(2, slot++);
                insertStmt.setInt(3, itemId);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    /**
     * Deletes wishlist entries for a specific account that are no longer in use.
     *
     * If the provided wishlist is non-empty, only entries whose item_id
     * is not in the list are deleted. If the list is empty, all wishlist
     * entries for the account are deleted.
     *
     * @param conn      the active database connection
     * @param accountId the ID of the account whose wishlist should be cleaned
     * @param wishlist  the list of item IDs to retain in the wishlist
     * @throws SQLException if a database error occurs
     */
    public static void deleteUnusedItems(Connection conn, int accountId, List<Integer> wishlist) throws SQLException {
        if (wishlist != null && !wishlist.isEmpty()) {
            Integer[] itemArray = wishlist.toArray(new Integer[0]);

            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM account.wishlist WHERE account_id = ? AND item_id <> ALL (?)"
            )) {
                deleteStmt.setInt(1, accountId);
                Array sqlArray = conn.createArrayOf("integer", itemArray);
                deleteStmt.setArray(2, sqlArray);
                deleteStmt.executeUpdate();
            }
        } else {
            // Delete all wishlist entries if the list is empty
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM account.wishlist WHERE account_id = ?"
            )) {
                deleteStmt.setInt(1, accountId);
                deleteStmt.executeUpdate();
            }
        }
    }

    /**
     * Loads the wishlist for the given account.
     *
     * Retrieves item IDs from the database ordered by slot.
     * If the wishlist has fewer than 10 items, zeros are appended to fill it.
     *
     * @param conn      the active database connection
     * @param accountId the ID of the account whose wishlist should be loaded
     * @return an unmodifiable list of item IDs (size will always be at least 10)
     * @throws SQLException if a database error occurs
     */
    public static List<Integer> load(Connection conn, int accountId) throws SQLException {
        List<Integer> wishlist = new ArrayList<>();
        String sql = "SELECT w.item_id FROM account.wishlist w WHERE w.account_id = ? ORDER BY w.slot";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    wishlist.add(rs.getInt("item_id"));
                }
            }
        }

        // Pad with zeros to always return 10 items
        while (wishlist.size() < 10) {
            wishlist.add(0);
        }

        return Collections.unmodifiableList(wishlist);
    }
}
