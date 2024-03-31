package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class FriendTable {
    public static final String CHARACTER_ID = "character_id";
    public static final String FRIEND_ID = "friend_id";
    public static final String FRIEND_NAME = "friend_name";
    public static final String FRIEND_GROUP = "friend_group";
    public static final String FRIEND_STATUS = "friend_status";

    private static final String tableName = "friend";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(CHARACTER_ID, DataTypes.INT)
                        .withClusteringColumn(FRIEND_ID, DataTypes.INT)
                        .withColumn(FRIEND_NAME, DataTypes.TEXT)
                        .withColumn(FRIEND_GROUP, DataTypes.TEXT)
                        .withColumn(FRIEND_STATUS, DataTypes.INT)
                        .build()
        );
    }
}
