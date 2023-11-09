package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class IdTable {
    public static final String ID_TYPE = "id_type";
    public static final String NEXT_ID = "next_id";


    private static final String TABLE_NAME = "id_table";

    public static String getTableName() {
        return TABLE_NAME;
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
                    AccountTable.getTableName(),
                    CharacterTable.getTableName()
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
