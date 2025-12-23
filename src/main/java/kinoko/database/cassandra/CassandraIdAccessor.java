package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import kinoko.database.IdAccessor;
import kinoko.database.cassandra.table.IdTable;

import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraIdAccessor extends CassandraAccessor implements IdAccessor {
    public CassandraIdAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Optional<Integer> getNextId(String type) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), IdTable.getTableName()).all()
                        .whereColumn(IdTable.ID_TYPE).isEqualTo(literal(type))
                        .build()
        );
        for (Row selectRow : selectResult) {
            final int nextId = selectRow.getInt(IdTable.NEXT_ID);
            final ResultSet updateResult = getSession().execute(
                    update(getKeyspace(), IdTable.getTableName())
                            .setColumn(IdTable.NEXT_ID, literal(nextId + 1)) // increment ID
                            .whereColumn(IdTable.ID_TYPE).isEqualTo(literal(type))
                            .ifColumn(IdTable.NEXT_ID).isEqualTo(literal(nextId)) // if not already updated
                            .build()
            );
            if (updateResult.wasApplied()) {
                return Optional.of(nextId);
            } else {
                // retry
                return getNextId(type);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<Integer> nextAccountId() {
        return getNextId(IdTable.ACCOUNT_ID);
    }

    @Override
    public synchronized Optional<Integer> nextCharacterId() {
        return getNextId(IdTable.CHARACTER_ID);
    }

    @Override
    public synchronized Optional<Integer> nextPartyId() {
        return getNextId(IdTable.PARTY_ID);
    }

    @Override
    public synchronized Optional<Integer> nextGuildId() {
        return getNextId(IdTable.GUILD_ID);
    }
    
    @Override
    public synchronized Optional<Integer> nextAllianceId() {
        return getNextId(IdTable.ALLIANCE_ID);
    }

    @Override
    public synchronized Optional<Integer> nextMemoId() {
        return getNextId(IdTable.MEMO_ID);
    }
}
