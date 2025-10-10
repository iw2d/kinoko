package kinoko.database.postgresql.type;

import kinoko.world.user.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
