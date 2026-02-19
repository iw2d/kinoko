package kinoko.database.sqlite;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kinoko.database.AccountAccessor;
import kinoko.database.DatabaseManager;
import kinoko.database.cassandra.table.AccountTable;
import kinoko.database.json.CashItemInfoSerializer;
import kinoko.database.json.ItemSerializer;
import kinoko.server.ServerConfig;
import kinoko.world.item.Trunk;
import kinoko.world.user.Account;
import kinoko.world.user.Locker;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Collections;
import java.util.Optional;

import static kinoko.database.schema.AccountSchema.*;

public final class SqliteAccountAccessor extends SqliteAccessor implements AccountAccessor {
    private static final String tableName = "account_table";

    private final ItemSerializer itemSerializer = new ItemSerializer();
    private final CashItemInfoSerializer cashItemInfoSerializer = new CashItemInfoSerializer();

    public SqliteAccountAccessor(Connection connection) {
        super(connection);
    }

    private Account loadAccount(ResultSet rs) throws SQLException {
        final int accountId = rs.getInt(ACCOUNT_ID);
        final String username = rs.getString(USERNAME);
        final String secondaryPassword = rs.getString(AccountTable.SECONDARY_PASSWORD);

        final Account account = new Account(accountId, username);
        account.setHasSecondaryPassword(secondaryPassword != null && !secondaryPassword.isEmpty());
        account.setSlotCount(rs.getInt(CHARACTER_SLOTS));
        account.setNxCredit(rs.getInt(NX_CREDIT));
        account.setNxPrepaid(rs.getInt(NX_PREPAID));
        account.setMaplePoint(rs.getInt(MAPLE_POINT));

        final Trunk trunk = new Trunk(rs.getInt(TRUNK_SIZE));
        for (var itemObject : getJsonArray(rs, TRUNK_ITEMS)) {
            trunk.getItems().add(itemSerializer.deserialize((JSONObject) itemObject));
        }
        trunk.setMoney(rs.getInt(TRUNK_MONEY));
        account.setTrunk(trunk);

        final Locker locker = new Locker();
        for (var ciiObject : getJsonArray(rs, LOCKER_ITEMS)) {
            locker.addCashItem(cashItemInfoSerializer.deserialize((JSONObject) ciiObject));
        }
        account.setLocker(locker);

        account.setWishlist(getJsonArray(rs, WISHLIST).toList(Integer.class));
        return account;
    }

    private String lowerUsername(String username) {
        return username.toLowerCase();
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkHashedPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

    @Override
    public Optional<Account> getAccountById(int accountId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName + " WHERE " + ACCOUNT_ID + " = ?"
        )) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
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
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName + " WHERE " + USERNAME + " = ?"
        )) {
            ps.setString(1, lowerUsername(username));
            try (ResultSet rs = ps.executeQuery()) {
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
        final String columnName = secondary ? SECONDARY_PASSWORD : PASSWORD;
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT " + columnName + " FROM " + tableName + " WHERE " + ACCOUNT_ID + " = ?"
        )) {
            ps.setInt(1, account.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final String hashedPassword = rs.getString(1);
                    if (hashedPassword != null && checkHashedPassword(password, hashedPassword)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public synchronized boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary) {
        final String columnName = secondary ? SECONDARY_PASSWORD : PASSWORD;
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT " + columnName + " FROM " + tableName + " WHERE " + ACCOUNT_ID + " = ?")) {
            ps.setInt(1, account.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final String hashedPassword = rs.getString(1);
                    if (hashedPassword != null && !checkHashedPassword(oldPassword, hashedPassword)) {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try (PreparedStatement ps = getConnection().prepareStatement("UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + ACCOUNT_ID + " = ?")) {
            ps.setString(1, hashPassword(newPassword));
            ps.setInt(2, account.getId());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean newAccount(String username, String password) {
        final Optional<Integer> accountId = DatabaseManager.idAccessor().nextAccountId();
        if (accountId.isEmpty()) {
            return false;
        }
        try (PreparedStatement ps = getConnection().prepareStatement(
                "INSERT INTO " + tableName + "(" +
                        ACCOUNT_ID + ", " +
                        USERNAME + ", " +
                        PASSWORD + ", " +
                        CHARACTER_SLOTS + ", " +
                        NX_CREDIT + ", " +
                        NX_PREPAID + ", " +
                        MAPLE_POINT + ", " +
                        TRUNK_ITEMS + ", " +
                        TRUNK_SIZE + ", " +
                        TRUNK_MONEY + ", " +
                        LOCKER_ITEMS + ", " +
                        WISHLIST + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        )) {
            int i = 1;
            ps.setInt(i++, accountId.get());
            ps.setString(i++, lowerUsername(username));
            ps.setString(i++, hashPassword(password));
            ps.setInt(i++, ServerConfig.CHARACTER_BASE_SLOTS);
            ps.setInt(i++, 0);
            ps.setInt(i++, 0);
            ps.setInt(i++, 0);
            setJsonArray(ps, i++, new JSONArray()); // TRUNK_ITEMS
            ps.setInt(i++, ServerConfig.TRUNK_BASE_SLOTS);
            ps.setInt(i++, 0);
            setJsonArray(ps, i++, new JSONArray()); // LOCKER_ITEMS
            setJsonArray(ps, i++, new JSONArray(Collections.nCopies(10, 0))); // WISHLIST
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveAccount(Account account) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "UPDATE " + tableName + " SET " +
                        CHARACTER_SLOTS + " = ?, " +
                        NX_CREDIT + " = ?, " +
                        NX_PREPAID + " = ?, " +
                        MAPLE_POINT + " = ?, " +
                        TRUNK_ITEMS + " = ?, " +
                        TRUNK_SIZE + " = ?, " +
                        TRUNK_MONEY + " = ?, " +
                        LOCKER_ITEMS + " = ?, " +
                        WISHLIST + " = ? WHERE " + ACCOUNT_ID + " = ?"
        )) {
            ps.setInt(1, account.getSlotCount());
            ps.setInt(2, account.getNxCredit());
            ps.setInt(3, account.getNxPrepaid());
            ps.setInt(4, account.getMaplePoint());
            final JSONArray trunkArray = new JSONArray();
            for (var item : account.getTrunk().getItems()) {
                trunkArray.add(itemSerializer.serialize(item));
            }
            setJsonArray(ps, 5, trunkArray);
            ps.setInt(6, account.getTrunk().getSize());
            ps.setInt(7, account.getTrunk().getMoney());
            final JSONArray lockerArray = new JSONArray();
            for (var cii : account.getLocker().getCashItems()) {
                lockerArray.add(cashItemInfoSerializer.serialize(cii));
            }
            setJsonArray(ps, 8, lockerArray);
            setJsonArray(ps, 9, new JSONArray(account.getWishlist()));
            ps.setInt(10, account.getId());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createTable(Connection connection) throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            ACCOUNT_ID + " INTEGER PRIMARY KEY, " +
                            USERNAME + " TEXT NOT NULL UNIQUE, " +
                            PASSWORD + " TEXT NOT NULL, " +
                            SECONDARY_PASSWORD + " TEXT, " + // NULLABLE
                            CHARACTER_SLOTS + " INTEGER NOT NULL, " +
                            NX_CREDIT + " INTEGER NOT NULL, " +
                            NX_PREPAID + " INTEGER NOT NULL, " +
                            MAPLE_POINT + " INTEGER NOT NULL, " +
                            TRUNK_ITEMS + " " + JSON_TYPE + ", " +
                            TRUNK_SIZE + " INTEGER NOT NULL, " +
                            TRUNK_MONEY + " INTEGER NOT NULL, " +
                            LOCKER_ITEMS + " " + JSON_TYPE + ", " +
                            WISHLIST + " " + JSON_TYPE + ")"
            );
        }
    }
}
