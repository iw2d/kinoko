package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.AccountAccessor;
import kinoko.database.DatabaseManager;
import kinoko.database.postgresql.type.AccountDao;
import kinoko.server.ServerConfig;
import kinoko.world.user.Account;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import java.util.Optional;

public final class PostgresAccountAccessor extends PostgresAccessor implements AccountAccessor {

    public PostgresAccountAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    private String lowerUsername(String username) {
        return username.toLowerCase();
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkHashedPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    @Override
    public Optional<Account> getAccountById(int accountId) {
        String sql = "SELECT * FROM account.accounts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(AccountDao.load(conn, rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccountByUsername(String username) {
        String sql = "SELECT * FROM account.accounts WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lowerUsername(username));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(AccountDao.load(conn, rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean checkPassword(Account account, String password, boolean secondary) {
        try (Connection conn = getConnection()) {
            String hashed = AccountDao.getHashedPassword(conn, account, secondary);
            return hashed != null && checkHashedPassword(password, hashed);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary) {
        String sqlUpdate = "UPDATE account.accounts SET " +
                (secondary ? "secondary_password" : "password") +
                " = ? WHERE id = ?";
        try (Connection conn = getConnection()) {
            String hashedOld = AccountDao.getHashedPassword(conn, account, secondary);
            if (hashedOld == null || checkHashedPassword(oldPassword, hashedOld)) {
                try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                    updateStmt.setString(1, hashPassword(newPassword));
                    updateStmt.setInt(2, account.getId());
                    return updateStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public synchronized boolean newAccount(String username, String password) {
        Optional<Integer> accountIdOpt = DatabaseManager.idAccessor().nextAccountId();  // should be -1
        if (accountIdOpt.isEmpty() || getAccountByUsername(username).isPresent()) {
            return false;
        }
        int accountId;

        String sql = "INSERT INTO account.accounts (username, password, character_slots, nx_credit, nx_prepaid, maple_point, trunk_size, trunk_money) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
                "RETURNING ID";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lowerUsername(username));
            stmt.setString(2, hashPassword(password));
            stmt.setInt(3, ServerConfig.CHARACTER_BASE_SLOTS);
            stmt.setInt(4, 0);
            stmt.setInt(5, 0);
            stmt.setInt(6, 0);
            stmt.setInt(7, ServerConfig.TRUNK_BASE_SLOTS);
            stmt.setInt(8, 0);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    accountId = rs.getInt("id");
                } else {
                    throw new SQLException("Failed to retrieve account ID after insert.");
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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
