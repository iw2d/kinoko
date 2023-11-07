package kinoko.database.postgres;

import kinoko.database.AccountAccessor;
import kinoko.database.ConnectionPool;
import kinoko.world.Account;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static kinoko.database.generated.public_.Tables.ACCOUNT;

public final class PostgresAccountAccessor extends PostgresAccessor implements AccountAccessor {
    public PostgresAccountAccessor(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Optional<Account> getAccountByUsername(String username) {
        try (final Connection conn = getConnection()) {
            final DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            final Result<Record> result = context.select().from(ACCOUNT)
                    .where(ACCOUNT.USERNAME.eq(username))
                    .fetch();
            for (Record r : result) {
                return Optional.of(new Account(
                        r.getValue(ACCOUNT.ID),
                        r.getValue(ACCOUNT.USERNAME)
                ));
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccountByPassword(String username, String password) {
        try (final Connection conn = getConnection()) {
            final DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            final Result<Record> result = context.select().from(ACCOUNT)
                    .where(ACCOUNT.USERNAME.eq(username))
                    .fetch();
            for (Record r : result) {
                final String hashedPassword = r.getValue(ACCOUNT.PASSWORD);
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return Optional.of(new Account(
                            r.getValue(ACCOUNT.ID),
                            r.getValue(ACCOUNT.USERNAME)
                    ));
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean newAccount(String username, String password) {
        if (getAccountByUsername(username).isPresent()) {
            // already exists
            return false;
        }
        try (final Connection conn = getConnection()) {
            final DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            final int inserted = context.insertInto(ACCOUNT)
                    .set(ACCOUNT.USERNAME, username)
                    .set(ACCOUNT.PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt()))
                    .onDuplicateKeyIgnore()
                    .execute();
            return inserted > 0;
        } catch (SQLException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public boolean updateAccount(Account account) {
        try (final Connection conn = getConnection()) {
            final DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            final int updated = context.update(ACCOUNT)
                    .set(ACCOUNT.USERNAME, account.getUsername())
                    .where(ACCOUNT.ID.eq(account.getId()))
                    .execute();
            return updated > 0;
        } catch (SQLException e) {
            log.error(e);
        }
        return false;
    }
}
