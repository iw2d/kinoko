package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.IdAccessor;
import kinoko.database.postgresql.type.ItemDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public final class PostgresIdAccessor extends PostgresAccessor implements IdAccessor {

    public PostgresIdAccessor(HikariDataSource dataSource) {
        super(dataSource);

        try (Connection conn = getConnection()){
            ItemDao.cleanupInvalidItems(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Optional<Integer> getNextId(String type) {
        return Optional.of(-1); // Postgres auto-generates IDs, so we return -1 as a placeholder
    }
    @Override
    public synchronized Optional<Integer> nextAccountId() {
        return getNextId("account_id");
    }

    @Override
    public synchronized Optional<Integer> nextCharacterId() {
        return getNextId("character_id");
    }

    @Override
    public synchronized Optional<Integer> nextPartyId() {
        return getNextId("party_id");
    }

    @Override
    public synchronized Optional<Integer> nextGuildId() {
        return getNextId("guild_id");
    }

    @Override
    public synchronized Optional<Integer> nextMemoId() {
        return getNextId("memo_id");
    }
}
