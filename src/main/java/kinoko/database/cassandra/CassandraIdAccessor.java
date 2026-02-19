package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.IdAccessor;

import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static kinoko.database.schema.IdSchema.*;

public final class CassandraIdAccessor extends CassandraAccessor implements IdAccessor {
    private static final String tableName = "id_table";

    public CassandraIdAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Optional<Integer> getNextId(String type) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(ID_TYPE).isEqualTo(literal(type))
                        .build()
        );
        for (Row selectRow : selectResult) {
            final int nextId = selectRow.getInt(NEXT_ID);
            final ResultSet updateResult = getSession().execute(
                    update(getKeyspace(), tableName)
                            .setColumn(NEXT_ID, literal(nextId + 1)) // increment ID
                            .whereColumn(ID_TYPE).isEqualTo(literal(type))
                            .ifColumn(NEXT_ID).isEqualTo(literal(nextId)) // if not already updated
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
        return getNextId(ACCOUNT_ID);
    }

    @Override
    public synchronized Optional<Integer> nextCharacterId() {
        return getNextId(CHARACTER_ID);
    }

    @Override
    public synchronized Optional<Integer> nextPartyId() {
        return getNextId(PARTY_ID);
    }

    @Override
    public synchronized Optional<Integer> nextGuildId() {
        return getNextId(GUILD_ID);
    }

    @Override
    public synchronized Optional<Integer> nextMemoId() {
        return getNextId(MEMO_ID);
    }

    public static void createTable(CqlSession session, String keyspace) {
        final ResultSet createResult = session.execute(
                SchemaBuilder.createTable(keyspace, tableName)
                        .ifNotExists()
                        .withPartitionKey(ID_TYPE, DataTypes.TEXT)
                        .withColumn(NEXT_ID, DataTypes.INT)
                        .build()
        );
        if (createResult.wasApplied()) {
            // Insert initial values
            for (String idType : List.of(
                    ACCOUNT_ID,
                    CHARACTER_ID,
                    PARTY_ID,
                    GUILD_ID,
                    MEMO_ID
            )) {
                session.execute(
                        QueryBuilder.insertInto(keyspace, tableName)
                                .value(ID_TYPE, QueryBuilder.literal(idType))
                                .value(NEXT_ID, QueryBuilder.literal(1))
                                .ifNotExists()
                                .build()
                );
            }
        }
    }
}
