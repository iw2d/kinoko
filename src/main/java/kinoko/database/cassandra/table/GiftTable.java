package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.cassandra.type.ItemUDT;

public final class GiftTable {
    public static final String ITEM_SN = "item_sn";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String ITEM = "item";
    public static final String SENDER_NAME = "sender_name";
    public static final String MESSAGE = "message";


    private static final String tableName = "gift";


    public static String getTableName() {
        return tableName;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(ITEM_SN, DataTypes.BIGINT)
                        .withPartitionKey(RECEIVER_ID, DataTypes.INT)
                        .withColumn(ITEM, SchemaBuilder.udt(ItemUDT.getTypeName(), true))
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
