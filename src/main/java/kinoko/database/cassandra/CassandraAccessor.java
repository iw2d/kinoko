package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public abstract class CassandraAccessor {
    public static final String ID_TABLE_NAME = "id_table";
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
                selectFrom(getKeyspace(), ID_TABLE_NAME).all()
                        .whereColumn("id_type").isEqualTo(literal(type))
                        .build()
        );
        for (Row selectRow : selectResult) {
            final int nextId = selectRow.getInt("next_id");
            final ResultSet updateResult = getSession().execute(
                    update(getKeyspace(), ID_TABLE_NAME)
                            .setColumn("next_id", literal(nextId + 1)) // increment ID
                            .whereColumn("id_type").isEqualTo(literal(type))
                            .ifColumn("next_id").isEqualTo(literal(nextId)) // if next_id was not already updated
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

    public static void createKeyspace(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createKeyspace(keyspace)
                        .ifNotExists()
                        .withSimpleStrategy(1)
                        .build()
        );
    }

    public static void createIdTable(CqlSession session, String keyspace) {
        final ResultSet createResult = session.execute(
                SchemaBuilder.createTable(keyspace, ID_TABLE_NAME)
                        .ifNotExists()
                        .withPartitionKey("id_type", DataTypes.TEXT)
                        .withColumn("next_id", DataTypes.INT)
                        .build()
        );
        if (createResult.wasApplied()) {
            // Insert initial values
            for (String idType : new String[]{
                    CassandraAccountAccessor.TABLE_NAME,
                    CassandraCharacterAccessor.TABLE_NAME
            }) {
                session.execute(
                        insertInto(keyspace, ID_TABLE_NAME)
                                .value("id_type", literal(idType))
                                .value("next_id", literal(1))
                                .build()
                );
            }
        }
    }
}
