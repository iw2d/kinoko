package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.AccountAccessor;
import kinoko.database.DatabaseManager;
import kinoko.server.ServerConfig;
import kinoko.server.cashshop.CashItemInfo;
import kinoko.world.item.Item;
import kinoko.world.item.Trunk;
import kinoko.world.user.Account;
import kinoko.world.user.Locker;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class PostgresAccountAccessor extends PostgresAccessor implements AccountAccessor {

    public PostgresAccountAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    private Account loadAccount(ResultSet rs) throws SQLException {
        final int accountId = rs.getInt("id");
        final String username = rs.getString("username");
        final String secondaryPassword = rs.getString("secondary_password");

        final Account account = new Account(accountId, username);
        account.setHasSecondaryPassword(secondaryPassword != null && !secondaryPassword.isEmpty());
        account.setSlotCount(rs.getInt("character_slots"));
        account.setNxCredit(rs.getInt("nx_credit"));
        account.setNxPrepaid(rs.getInt("nx_prepaid"));
        account.setMaplePoint(rs.getInt("maple_point"));

        account.setTrunk(loadTrunk(accountId));
        account.setLocker(loadLocker(accountId));
        account.setWishlist(loadWishlist(accountId));

        return account;
    }

    private Trunk loadTrunk(int accountId) throws SQLException {
        int trunkSize = ServerConfig.TRUNK_BASE_SLOTS;
        int trunkMoney = 0;

        String accountSql = "SELECT trunk_size, trunk_money FROM account.accounts WHERE id = ?";
        String itemsSql = """
        SELECT ti.slot, i.item_id, i.quantity
        FROM account.trunk_item ti
        JOIN item.items i ON ti.item_sn = i.item_sn
        WHERE ti.account_id = ?
        ORDER BY ti.slot
    """;

        Trunk trunk;

        try (Connection conn = getConnection()) {
            // Query trunk info
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

            // Query items
            try (PreparedStatement stmt = conn.prepareStatement(itemsSql)) {
                stmt.setInt(1, accountId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Item item = new Item(rs.getInt("item_id"), (short) rs.getInt("quantity"));
                        trunk.getItems().add(item);
                    }
                }
            }
        }

        return trunk;
    }


    private Locker loadLocker(int accountId) throws SQLException {
        Locker locker = new Locker();
        String sql = "SELECT li.slot, li.item_sn, li.commodity_id, i.item_id, i.quantity " +
                "FROM account.locker_item li " +
                "JOIN item.items i ON li.item_sn = i.item_sn " +
                "WHERE li.account_id = ? ORDER BY li.slot";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
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

    private List<Integer> loadWishlist(int accountId) throws SQLException {
        List<Integer> wishlist = new ArrayList<>();
        String sql = "SELECT w.item_id FROM account.wishlist w " +
                "WHERE w.account_id = ? ORDER BY w.slot";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    wishlist.add(rs.getInt("item_id"));
                }
            }
        }
        while (wishlist.size() < 10) wishlist.add(0);
        return Collections.unmodifiableList(wishlist);
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
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadAccount(rs));
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
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, lowerUsername(username));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadAccount(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean checkPassword(Account account, String password, boolean secondary) {
        String column = secondary ? "secondary_password" : "password";
        String sql = "SELECT " + column + " FROM account.accounts WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, account.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashed = rs.getString(column);
                    return hashed != null && checkHashedPassword(password, hashed);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary) {
        String column = secondary ? "secondary_password" : "password";
        String sqlSelect = "SELECT " + column + " FROM account.accounts WHERE id = ?";
        String sqlUpdate = "UPDATE account.accounts SET " + column + " = ? WHERE id = ?";
        try (PreparedStatement selectStmt = getConnection().prepareStatement(sqlSelect)) {
            selectStmt.setInt(1, account.getId());
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    String hashedOld = rs.getString(column);
                    if (hashedOld == null || checkHashedPassword(oldPassword, hashedOld)) {
                        try (PreparedStatement updateStmt = getConnection().prepareStatement(sqlUpdate)) {
                            updateStmt.setString(1, hashPassword(newPassword));
                            updateStmt.setInt(2, account.getId());
                            return updateStmt.executeUpdate() > 0;
                        }
                    }
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
        System.out.println("Creating account.");
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
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

            // Initialize a base trunk, locker, wishlist
//            for (int i = 0; i < ServerConfig.TRUNK_BASE_SLOTS; i++) {
//                try (PreparedStatement tStmt = getConnection().prepareStatement(
//                        "INSERT INTO account.trunk_item (account_id, slot, item_sn) VALUES (?, ?, ?)")) {
//                    tStmt.setInt(1, accountId);
//                    tStmt.setInt(2, i);
//                    tStmt.setLong(3, itemSn);
//                    tStmt.executeUpdate();
//                }
//            }

//            for (int i = 0; i < 10; i++) {
//                try (PreparedStatement wStmt = getConnection().prepareStatement(
//                        "INSERT INTO account.wishlist (account_id, slot, item_sn) VALUES (?, ?, ?)")) {
//                    wStmt.setInt(1, accountId);
//                    wStmt.setInt(2, i);
//                    wStmt.setLong(3, itemSn);
//                    wStmt.executeUpdate();
//                }
//            }

            // Locker starts empty, no rows needed initially
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveAccount(Account account) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE account.accounts SET character_slots = ?, nx_credit = ?, nx_prepaid = ?, maple_point = ?, trunk_size = ?, trunk_money = ? WHERE id = ?")) {
                stmt.setInt(1, account.getSlotCount());
                stmt.setInt(2, account.getNxCredit());
                stmt.setInt(3, account.getNxPrepaid());
                stmt.setInt(4, account.getMaplePoint());
                stmt.setInt(5, account.getTrunk().getSize());
                stmt.setInt(6, account.getTrunk().getMoney());
                stmt.setInt(7, account.getId());
                stmt.executeUpdate();
            }

            // Save trunk
            try (PreparedStatement delTrunk = conn.prepareStatement("DELETE FROM account.trunk_item WHERE account_id = ?")) {
                delTrunk.setInt(1, account.getId());
                delTrunk.executeUpdate();
            }
            int slot = 0;
            for (Item item : account.getTrunk().getItems()) {
                try (PreparedStatement insertTrunk = conn.prepareStatement(
                        "INSERT INTO account.trunk_item (account_id, slot, item_sn) VALUES (?, ?, ?)")) {
                    insertTrunk.setInt(1, account.getId());
                    insertTrunk.setInt(2, slot++);
                    insertTrunk.setObject(3, item.getItemSn()); // null if empty
                    insertTrunk.executeUpdate();
                }
            }

            // Save wishlist
            try (PreparedStatement delWish = conn.prepareStatement("DELETE FROM account.wishlist WHERE account_id = ?")) {
                delWish.setInt(1, account.getId());
                delWish.executeUpdate();
            }
            slot = 0;
            for (Integer itemId : account.getWishlist()) {
                try (PreparedStatement insertWish = conn.prepareStatement(
                        """
                        INSERT INTO account.wishlist (account_id, slot, item_id)
                        VALUES (?, ?, ?)
                        ON CONFLICT (account_id, slot) DO UPDATE
                        SET item_id = EXCLUDED.item_id
                        """
                )) {
                    insertWish.setInt(1, account.getId());
                    insertWish.setInt(2, slot++);
                    insertWish.setInt(3, itemId); // now using item_id, not item_sn
                    insertWish.executeUpdate();
                }
            }

            // Save locker
            try (PreparedStatement delLocker = conn.prepareStatement("DELETE FROM account.locker_item WHERE account_id = ?")) {
                delLocker.setInt(1, account.getId());
                delLocker.executeUpdate();
            }
            slot = 0;
            for (CashItemInfo cash : account.getLocker().getCashItems()) {
                try (PreparedStatement insertLocker = conn.prepareStatement(
                        "INSERT INTO account.locker_item (account_id, slot, item_sn, commodity_id) VALUES (?, ?, ?, ?)")) {
                    insertLocker.setInt(1, account.getId());
                    insertLocker.setInt(2, slot++);
                    insertLocker.setObject(3, cash.getItem().getItemSn());
                    insertLocker.setInt(4, cash.getCommodityId());
                    insertLocker.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
