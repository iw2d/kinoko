package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.AccountAccessor;
import kinoko.world.Account;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraAccountAccessor extends CassandraAccessor implements AccountAccessor {
    public static final String TABLE_NAME = "account";

    public CassandraAccountAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Account loadAccount(Row row) {
        return new Account(
                row.getInt("id"),
                row.getString("username")
        );
    }

    @Override
    public Optional<Account> getAccountByUsername(String username) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), TABLE_NAME).all()
                        .whereColumn("username").isEqualTo(literal(username))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadAccount(row));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccountByPassword(String username, String password) {
        final ResultSet selectResult = getSession().execute(
            selectFrom(getKeyspace(), TABLE_NAME).all()
                    .whereColumn("username").isEqualTo(literal(username))
                    .build()
        );
        for (Row row : selectResult) {
            final String hashedPassword = row.getString("password");
            if (hashedPassword == null) {
                continue;
            }
            if (BCrypt.checkpw(password, hashedPassword)) {
                return Optional.of(loadAccount(row));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean newAccount(String username, String password) {
        final Optional<Integer> accountId = getNextId(TABLE_NAME);
        if (accountId.isEmpty()) {
            return false;
        }
        final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        final ResultSet insertResult = getSession().execute(
                insertInto(getKeyspace(), TABLE_NAME)
                        .value("id", literal(accountId.get()))
                        .value("username", literal(username))
                        .value("password", literal(hashedPassword))
                        .build()
        );
        return insertResult.wasApplied();
    }

    @Override
    public boolean updateAccount(Account account) {
        final ResultSet updateResult = getSession().execute(
                QueryBuilder.update(getKeyspace(), TABLE_NAME)
                        .setColumn("username", literal(account.getUsername()))
                        .whereColumn("id").isEqualTo(literal(account.getId()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    public static void createTable(CqlSession session, String keyspace) {
        // Query using only username
        session.execute(
                SchemaBuilder.createTable(keyspace, TABLE_NAME)
                        .ifNotExists()
                        .withPartitionKey("username", DataTypes.TEXT)
                        .withColumn("password", DataTypes.TEXT)
                        .withColumn("id", DataTypes.INT)
                        .build()
        );
    }
}
