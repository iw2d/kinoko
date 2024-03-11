package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.server.ServerConfig;

public final class MigrationTable {
    public static final String ACCOUNT_ID = "account_id";
    public static final String CHANNEL_ID = "channel_id";
    public static final String CHARACTER_ID = "character_id";
    public static final String CLIENT_KEY = "client_key";
    public static final String MACHINE_ID = "machine_id";
    public static final String REMOTE_ADDRESS = "remote_address";

    private static final String tableName = "migration";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(ACCOUNT_ID, DataTypes.INT)
                        .withColumn(CHANNEL_ID, DataTypes.INT)
                        .withColumn(CHARACTER_ID, DataTypes.INT)
                        .withColumn(CLIENT_KEY, DataTypes.BLOB)
                        .withColumn(MACHINE_ID, DataTypes.BLOB)
                        .withColumn(REMOTE_ADDRESS, DataTypes.BLOB)
                        .withDefaultTimeToLiveSeconds(ServerConfig.MIGRATION_REQUEST_TTL)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, getTableName())
                        .andColumn(CHARACTER_ID)
                        .build()
        );
    }
}
