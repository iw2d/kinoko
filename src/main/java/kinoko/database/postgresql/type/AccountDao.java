package kinoko.database.postgresql.type;

import kinoko.world.user.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AccountDao {

    /**
     * Saves the given account and all related data to the database.
     * Updates the main account fields (character slots, NX credit, prepaid, Maple points, trunk size, trunk money)
     * in the accounts table and saves the associated trunk, wishlist, and locker data.
     * All operations should be executed inside a transaction. If any database operation fails,
     * an SQLException is thrown and the transaction can be rolled back by the caller.
     *
     * @param conn the database connection to use
     * @param account the account object containing updated data to save
     * @throws SQLException if any database operation fails
     */
    public static void save(Connection conn, Account account) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE account.accounts SET character_slots = ?, nx_credit = ?, nx_prepaid = ?, maple_point = ?, trunk_size = ?, trunk_money = ? WHERE id = ?"
        )) {
            stmt.setInt(1, account.getSlotCount());
            stmt.setInt(2, account.getNxCredit());
            stmt.setInt(3, account.getNxPrepaid());
            stmt.setInt(4, account.getMaplePoint());
            stmt.setInt(5, account.getTrunk().getSize());
            stmt.setInt(6, account.getTrunk().getMoney());
            stmt.setInt(7, account.getId());
            stmt.executeUpdate();
        }

        // Save related tables
        int accountId = account.getId();
        TrunkDao.save(conn, accountId, account.getTrunk());
        WishlistDao.save(conn, accountId, account.getWishlist());
        LockerDao.save(conn, accountId, account.getLocker());
    }

    /**
     * Retrieves the hashed password (either primary or secondary) for the given account from the database.
     * Determines which password column to query based on the {@code secondary} flag.
     *
     * @param conn the active database connection used to execute the query
     * @param account the account whose password is being retrieved
     * @param secondary true to fetch the secondary password, false to fetch the primary password
     * @return the hashed password string if found, or null if no password exists for the account
     * @throws SQLException if a database access error occurs
     */
    public static String getHashedPassword(Connection conn, Account account, boolean secondary) throws SQLException {
        String column = secondary ? "secondary_password" : "password";
        String sql = "SELECT " + column + " FROM account.accounts WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, account.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(column);
                }
            }
        }
        return null;
    }

    /**
     * Loads an Account object and its related data from the database using the provided ResultSet and connection.
     * Builds the Account instance from base fields such as ID, username, and secondary password,
     * then loads associated data including the trunk, locker, and wishlist within the same connection.
     *
     * @param conn the active database connection used to load related account data
     * @param rs the ResultSet containing the account information (positioned at a valid row)
     * @return a fully initialized Account object
     * @throws SQLException if a database access error occurs
     */
    public static Account load(Connection conn, ResultSet rs) throws SQLException {
        final int accountId = rs.getInt("id");
        final String username = rs.getString("username");
        final String secondaryPassword = rs.getString("secondary_password");

        final Account account = new Account(accountId, username);
        account.setHasSecondaryPassword(secondaryPassword != null && !secondaryPassword.isEmpty());
        account.setSlotCount(rs.getInt("character_slots"));
        account.setNxCredit(rs.getInt("nx_credit"));
        account.setNxPrepaid(rs.getInt("nx_prepaid"));
        account.setMaplePoint(rs.getInt("maple_point"));

        account.setTrunk(TrunkDao.load(conn, accountId));
        account.setLocker(LockerDao.load(conn, accountId));
        account.setWishlist(WishlistDao.load(conn, accountId));

        return account;
    }


    /**
     * Retrieves the account ID for the given character ID.
     *
     * @param conn        the database connection to use
     * @param characterId the ID of the character
     * @return Optional containing the account ID if found, otherwise empty
     * @throws SQLException if a database access error occurs
     */
    public static Optional<Integer> getAccountIdByCharacterId(Connection conn, int characterId) throws SQLException {
        String sql = "SELECT account_id FROM player.characters WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("account_id"));
                }
            }
        }
        return Optional.empty();
    }
}
