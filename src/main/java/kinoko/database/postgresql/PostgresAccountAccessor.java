package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.AccountAccessor;
import kinoko.database.postgresql.type.AccountDao;
import kinoko.world.user.Account;

import java.sql.*;

import java.util.Optional;

public final class PostgresAccountAccessor extends PostgresAccessor implements AccountAccessor {

    public PostgresAccountAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Retrieves an account by its unique ID.
     * Opens a database connection and delegates the loading logic to AccountDao.
     *
     * @param accountId the ID of the account to retrieve
     * @return an Optional containing the Account if found, otherwise empty
     */
    @Override
    public Optional<Account> getAccountById(int accountId) {
        try (Connection conn = getConnection()) {
            return AccountDao.getAccountById(conn, accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Retrieves an account by its username.
     * The username is normalized and passed to AccountDao for lookup.
     *
     * @param username the username to look up
     * @return an Optional containing the Account if found, otherwise empty
     */
    @Override
    public Optional<Account> getAccountByUsername(String username) {
        try (Connection conn = getConnection()) {
            return AccountDao.getAccountByUsername(conn, username);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Checks whether the provided password matches the stored hash for the account.
     * Uses AccountDao to retrieve the hashed password and perform verification.
     *
     * @param account the account being authenticated
     * @param password the plaintext password to verify
     * @param secondary true if verifying the secondary password, false for the primary
     * @return true if the password matches, false otherwise
     */
    @Override
    public boolean checkPassword(Account account, String password, boolean secondary) {
        return withTransaction(conn -> {
            String hashed = AccountDao.getHashedPassword(conn, account, secondary);
            return hashed != null && AccountDao.checkHashedPassword(password, hashed);
        });
    }

    /**
     * Updates the account's password if the provided old password matches the stored hash.
     * The method handles both primary and secondary password updates through AccountDao.
     *
     * @param account the account whose password is being updated
     * @param oldPassword the current plaintext password
     * @param newPassword the new plaintext password to set
     * @param secondary true if updating the secondary password, false for the primary
     * @return true if the password was successfully updated, false otherwise
     */
    @Override
    public boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary) {
        return withTransaction(conn -> {
            String hashedOld = AccountDao.getHashedPassword(conn, account, secondary);
            if (hashedOld == null || AccountDao.checkHashedPassword(oldPassword, hashedOld)) {
                return AccountDao.updatePassword(conn, account, newPassword, secondary);
            }
            return false;
        });
    }

    /**
     * Creates a new account with the provided username and password.
     * Executes all SQL operations inside a transaction for safety.
     *
     * @param username the desired username for the new account
     * @param password the plaintext password for the new account
     * @return true if the account was successfully created, false otherwise
     */
    @Override
    public synchronized boolean newAccount(String username, String password) {
        return withTransaction(conn -> {
            return AccountDao.createAccount(conn, username, password);
                }
        );
    }

    /**
     * Saves all changes made to the given account, including its related data.
     * Uses a transaction to ensure atomic updates across all tables.
     *
     * @param account the account containing updated data to persist
     * @return true if the account was successfully saved, false otherwise
     */
    @Override
    public boolean saveAccount(Account account) {
        try {
            return withTransaction(getConnection(), c -> AccountDao.save(c, account));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
