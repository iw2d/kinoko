package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class GiftTable {
    public static final String GIFT_SN = "gift_sn";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String ITEM_ID = "item_id";
    public static final String COMMODITY_ID = "commodity_id";
    public static final String SENDER_NAME = "sender_name";
    public static final String MESSAGE = "message";


    private static final String tableName = "gift_table";


    public static String getTableName() {
        return tableName;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(GIFT_SN, DataTypes.BIGINT)
                        .withColumn(RECEIVER_ID, DataTypes.INT)
                        .withColumn(ITEM_ID, DataTypes.INT)
                        .withColumn(COMMODITY_ID, DataTypes.INT)
                        .withColumn(SENDER_NAME, DataTypes.TEXT)
                        .withColumn(MESSAGE, DataTypes.TEXT)
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
