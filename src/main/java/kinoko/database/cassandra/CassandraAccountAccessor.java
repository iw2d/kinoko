package kinoko.database.cassandra;

import com.alibaba.fastjson2.JSONArray;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.AccountAccessor;
import kinoko.database.DatabaseManager;
import kinoko.database.json.AccountSerializer;
import kinoko.server.ServerConfig;
import kinoko.world.item.Trunk;
import kinoko.world.user.Account;
import kinoko.world.user.Locker;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static kinoko.database.schema.AccountSchema.*;

public final class CassandraAccountAccessor extends CassandraAccessor implements AccountAccessor {
    private static final String tableName = "account_Table";
    private final AccountSerializer accountSerializer = new AccountSerializer();

    public CassandraAccountAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Account loadAccount(Row row) {
        final int accountId = row.getInt(ACCOUNT_ID);
        final String username = row.getString(USERNAME);
        final String secondaryPassword = row.getString(SECONDARY_PASSWORD);

        final Account account = new Account(accountId, username);
        account.setHasSecondaryPassword(secondaryPassword != null && !secondaryPassword.isEmpty());
        account.setSlotCount(row.getInt(CHARACTER_SLOTS));
        account.setNxCredit(row.getInt(NX_CREDIT));
        account.setNxPrepaid(row.getInt(NX_PREPAID));
        account.setMaplePoint(row.getInt(MAPLE_POINT));

        final Trunk trunk = new Trunk(row.getInt(TRUNK_SIZE));
        trunk.getItems().addAll(accountSerializer.deserializeTrunkItems(getJsonArray(row, TRUNK_ITEMS)));
        trunk.setMoney(row.getInt(TRUNK_MONEY));
        account.setTrunk(trunk);

        final Locker locker = new Locker();
        locker.getCashItems().addAll(accountSerializer.deserializeLockerItems(getJsonArray(row, LOCKER_ITEMS)));
        account.setLocker(locker);

        final List<Integer> wishlist = row.getList(WISHLIST, Integer.class);
        account.setWishlist(Collections.unmodifiableList(wishlist != null ? wishlist : Collections.nCopies(10, 0)));

        return account;
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
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(ACCOUNT_ID).isEqualTo(literal(accountId))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadAccount(row));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccountByUsername(String username) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(USERNAME).isEqualTo(literal(lowerUsername(username)))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadAccount(row));
        }
        return Optional.empty();
    }

    @Override
    public boolean checkPassword(Account account, String password, boolean secondary) {
        final String columnName = secondary ? SECONDARY_PASSWORD : PASSWORD;
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .column(columnName)
                        .whereColumn(ACCOUNT_ID).isEqualTo(literal(account.getId()))
                        .build()
                        .setExecutionProfileName(CassandraConnector.PROFILE_ONE)
        );
        for (Row row : selectResult) {
            final String hashedPassword = row.getString(columnName);
            if (hashedPassword == null) {
                continue;
            }
            if (checkHashedPassword(password, hashedPassword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary) {
        final String columnName = secondary ? SECONDARY_PASSWORD : PASSWORD;
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .column(columnName)
                        .whereColumn(ACCOUNT_ID).isEqualTo(literal(account.getId()))
                        .build()
        );
        for (Row row : selectResult) {
            final String hashedOldPassword = row.getString(columnName);
            if (hashedOldPassword == null || checkHashedPassword(oldPassword, hashedOldPassword)) {
                final ResultSet updateResult = getSession().execute(
                        update(getKeyspace(), tableName)
                                .setColumn(columnName, literal(hashPassword(newPassword)))
                                .whereColumn(ACCOUNT_ID).isEqualTo(literal(account.getId()))
                                .build()
                );
                return updateResult.wasApplied();
            }
        }
        return false;
    }

    @Override
    public synchronized boolean newAccount(String username, String password) {
        final Optional<Integer> accountId = DatabaseManager.idAccessor().nextAccountId();
        if (accountId.isEmpty()) {
            return false;
        }
        if (getAccountByUsername(username).isPresent()) {
            return false;
        }
        final ResultSet insertResult = getSession().execute(
                insertInto(getKeyspace(), tableName)
                        .value(ACCOUNT_ID, literal(accountId.get()))
                        .value(USERNAME, literal(lowerUsername(username)))
                        .value(PASSWORD, literal(hashPassword(password)))
                        .value(CHARACTER_SLOTS, literal(ServerConfig.CHARACTER_BASE_SLOTS))
                        .value(NX_CREDIT, literal(0))
                        .value(NX_PREPAID, literal(0))
                        .value(MAPLE_POINT, literal(0))
                        .value(TRUNK_ITEMS, literalJsonArray(new JSONArray()))
                        .value(TRUNK_SIZE, literal(ServerConfig.TRUNK_BASE_SLOTS))
                        .value(TRUNK_MONEY, literal(0))
                        .value(LOCKER_ITEMS, literalJsonArray(new JSONArray()))
                        .value(WISHLIST, literal(List.of()))
                        .ifNotExists()
                        .build()
        );
        return insertResult.wasApplied();
    }

    @Override
    public boolean saveAccount(Account account) {
        final ResultSet updateResult = getSession().execute(
                update(getKeyspace(), tableName)
                        .setColumn(CHARACTER_SLOTS, literal(account.getSlotCount()))
                        .setColumn(NX_CREDIT, literal(account.getNxCredit()))
                        .setColumn(NX_PREPAID, literal(account.getNxPrepaid()))
                        .setColumn(MAPLE_POINT, literal(account.getMaplePoint()))
                        .setColumn(TRUNK_ITEMS, literalJsonArray(accountSerializer.serializeTrunkItems(account.getTrunk().getItems())))
                        .setColumn(TRUNK_SIZE, literal(account.getTrunk().getSize()))
                        .setColumn(TRUNK_MONEY, literal(account.getTrunk().getMoney()))
                        .setColumn(LOCKER_ITEMS, literalJsonArray(accountSerializer.serializeLockerItems(account.getLocker().getCashItems())))
                        .setColumn(WISHLIST, literal(account.getWishlist()))
                        .whereColumn(ACCOUNT_ID).isEqualTo(literal(account.getId()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, tableName)
                        .ifNotExists()
                        .withPartitionKey(ACCOUNT_ID, DataTypes.INT)
                        .withColumn(USERNAME, DataTypes.TEXT)
                        .withColumn(PASSWORD, DataTypes.TEXT)
                        .withColumn(SECONDARY_PASSWORD, DataTypes.TEXT)
                        .withColumn(CHARACTER_SLOTS, DataTypes.INT)
                        .withColumn(NX_CREDIT, DataTypes.INT)
                        .withColumn(NX_PREPAID, DataTypes.INT)
                        .withColumn(MAPLE_POINT, DataTypes.INT)
                        .withColumn(TRUNK_ITEMS, JSON_TYPE)
                        .withColumn(TRUNK_SIZE, DataTypes.INT)
                        .withColumn(TRUNK_MONEY, DataTypes.INT)
                        .withColumn(LOCKER_ITEMS, JSON_TYPE)
                        .withColumn(WISHLIST, DataTypes.frozenListOf(DataTypes.INT))
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, tableName)
                        .andColumn(USERNAME)
                        .build()
        );
    }
}
