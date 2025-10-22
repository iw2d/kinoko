package kinoko.database.postgresql.type;

import kinoko.database.DatabaseManager;
import kinoko.world.user.Account;
import org.mindrot.jbcrypt.BCrypt;

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

    /**
     * Retrieves an account by its unique ID from the database.
     * Executes a query against the accounts table and constructs the Account object using load().
     *
     * @param conn the active database connection
     * @param accountId the ID of the account to retrieve
     * @return an Optional containing the Account if found, otherwise empty
     * @throws SQLException if a database access error occurs
     */
    public static Optional<Account> getAccountById(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT * FROM account.accounts WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(load(conn, rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves an account by its username from the database.
     * The username is converted to lowercase before lookup for consistency.
     *
     * @param conn the active database connection
     * @param username the username of the account to retrieve
     * @return an Optional containing the Account if found, otherwise empty
     * @throws SQLException if a database access error occurs
     */
    public static Optional<Account> getAccountByUsername(Connection conn, String username) throws SQLException {
        String sql = "SELECT * FROM account.accounts WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(load(conn, rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Updates the account's password or secondary password in the database.
     * Automatically hashes the provided password before saving.
     *
     * @param conn the active database connection
     * @param account the account whose password is being updated
     * @param newPassword the new plaintext password
     * @param secondary true to update the secondary password, false to update the primary
     * @return true if the update succeeded, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean updatePassword(Connection conn, Account account, String newPassword, boolean secondary) throws SQLException {
        String column = secondary ? "secondary_password" : "password";
        String sql = "UPDATE account.accounts SET " + column + " = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashPassword(newPassword));
            stmt.setInt(2, account.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Creates a new account in the database with default values for character slots and currencies.
     * The password is automatically hashed before being stored.
     *
     * @param conn the active database connection
     * @param username the desired username for the new account
     * @param password the plaintext password for the new account
     * @return true if the account was successfully created, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean createAccount(Connection conn, String username, String password) throws SQLException {
        String sql = """
        INSERT INTO account.accounts 
        (username, password, character_slots, nx_credit, nx_prepaid, maple_point, trunk_size, trunk_money)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;
        Optional<Integer> accountIdOpt = DatabaseManager.idAccessor().nextAccountId();  // should be -1
        if (accountIdOpt.isEmpty() || getAccountByUsername(conn, username).isPresent()) {
            return false;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            stmt.setString(2, hashPassword(password));
            stmt.setInt(3, kinoko.server.ServerConfig.CHARACTER_BASE_SLOTS);
            stmt.setInt(4, 0);
            stmt.setInt(5, 0);
            stmt.setInt(6, 0);
            stmt.setInt(7, kinoko.server.ServerConfig.TRUNK_BASE_SLOTS);
            stmt.setInt(8, 0);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Generates a BCrypt hash for the provided plaintext password.
     *
     * @param password the plaintext password to hash
     * @return the hashed password string
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies whether a plaintext password matches the provided hashed password.
     *
     * @param password the plaintext password to verify
     * @param hashedPassword the stored hashed password to compare against
     * @return true if the passwords match, false otherwise
     */
    public static boolean checkHashedPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
