package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class MemoTable {
    public static final String MEMO_ID = "memo_id";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String MEMO_TYPE = "memo_type";
    public static final String MEMO_CONTENT = "memo_content";
    public static final String SENDER_NAME = "sender_name";
    public static final String DATE_SENT = "date_sent";

    private static final String tableName = "memo_table";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(MEMO_ID, DataTypes.INT)
                        .withColumn(RECEIVER_ID, DataTypes.INT)
                        .withColumn(MEMO_TYPE, DataTypes.INT)
                        .withColumn(MEMO_CONTENT, DataTypes.TEXT)
                        .withColumn(SENDER_NAME, DataTypes.TEXT)
                        .withColumn(DATE_SENT, DataTypes.TIMESTAMP)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, getTableName())
                        .andColumn(RECEIVER_ID)
                        .build()
        );
    }
}
