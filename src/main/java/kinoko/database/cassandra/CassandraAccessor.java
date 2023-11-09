package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import kinoko.database.cassandra.table.IdTable;

import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public abstract class CassandraAccessor {
    private final CqlSession session;
    private final String keyspace;

    public CassandraAccessor(CqlSession session, String keyspace) {
        this.session = session;
        this.keyspace = keyspace;
    }

    public final CqlSession getSession() {
        return session;
    }

    public final String getKeyspace() {
        return keyspace;
    }

    protected synchronized Optional<Integer> getNextId(String type) {
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
}
