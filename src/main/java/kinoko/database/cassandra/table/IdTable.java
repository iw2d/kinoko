package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class IdTable {
    public static final String ID_TYPE = "id_type";
    public static final String NEXT_ID = "next_id";

    // ID types
    public static final String ACCOUNT_TABLE = AccountTable.getTableName();
    public static final String CHARACTER_TABLE = CharacterTable.getTableName();

    private static final String tableName = "id_table";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable(CqlSession session, String keyspace) {
        final ResultSet createResult = session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(ID_TYPE, DataTypes.TEXT)
                        .withColumn(NEXT_ID, DataTypes.INT)
                        .build()
        );
        if (createResult.wasApplied()) {
            // Insert initial values
            for (String idType : new String[]{
                    ACCOUNT_TABLE,
                    CHARACTER_TABLE
            }) {
                session.execute(
                        QueryBuilder.insertInto(keyspace, getTableName())
                                .value(ID_TYPE, QueryBuilder.literal(idType))
                                .value(NEXT_ID, QueryBuilder.literal(1))
                                .ifNotExists()
                                .build()
                );
            }
        }
    }
}
