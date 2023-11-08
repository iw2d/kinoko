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
import static kinoko.database.cassandra.model.AccountModel.*;

public final class CassandraAccountAccessor extends CassandraAccessor implements AccountAccessor {
    public static final String TABLE_NAME = "account";

    public CassandraAccountAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Account loadAccount(Row row) {
        final int accountId = row.getInt(ACCOUNT_ID.getName());
        final String username = row.getString(USERNAME.getName());
        final Account account = new Account(accountId, username);
        account.setNxCredit(row.getInt(NX_CREDIT.getName()));
        account.setNxPrepaid(row.getInt(NX_PREPAID.getName()));
        account.setMaplePoint(row.getInt(MAPLE_POINT.getName()));
        return account;
    }

    @Override
    public Optional<Account> getAccountByUsername(String username) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), TABLE_NAME).all()
                        .whereColumn(USERNAME.getName()).isEqualTo(literal(username))
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
                        .whereColumn(USERNAME.getName()).isEqualTo(literal(username))
                        .build()
        );
        for (Row row : selectResult) {
            final String hashedPassword = row.getString(PASSWORD.getName());
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
                        .value(USERNAME.getName(), literal(username))
                        .value(ACCOUNT_ID.getName(), literal(accountId.get()))
                        .value(PASSWORD.getName(), literal(hashedPassword))
                        .value(NX_CREDIT.getName(), literal(0))
                        .value(NX_PREPAID.getName(), literal(0))
                        .value(MAPLE_POINT.getName(), literal(0))
                        .build()
        );
        return insertResult.wasApplied();
    }

    @Override
    public boolean saveAccount(Account account) {
        final ResultSet updateResult = getSession().execute(
                QueryBuilder.update(getKeyspace(), TABLE_NAME)
                        .setColumn(NX_CREDIT.getName(), literal(account.getNxCredit()))
                        .setColumn(NX_PREPAID.getName(), literal(account.getNxPrepaid()))
                        .setColumn(MAPLE_POINT.getName(), literal(account.getMaplePoint()))
                        .whereColumn(USERNAME.getName()).isEqualTo(literal(account.getUsername()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, TABLE_NAME)
                        .ifNotExists()
                        .withPartitionKey(USERNAME.getName(), DataTypes.TEXT)
                        .withClusteringColumn(ACCOUNT_ID.getName(), DataTypes.INT)
                        .withColumn(PASSWORD.getName(), DataTypes.TEXT)
                        .withColumn(NX_CREDIT.getName(), DataTypes.INT)
                        .withColumn(NX_PREPAID.getName(), DataTypes.INT)
                        .withColumn(MAPLE_POINT.getName(), DataTypes.INT)
                        .build()
        );
    }
}
