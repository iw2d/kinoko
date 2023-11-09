package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class AccountTable {
    public static final String ACCOUNT_ID = "account_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SECONDARY_PASSWORD = "secondary_password";
    public static final String SLOT_COUNT = "slot_count";
    public static final String NX_CREDIT = "nx_credit";
    public static final String NX_PREPAID = "nx_prepaid";
    public static final String MAPLE_POINT = "maple_point";

    private static final String TABLE_NAME = "account";
    private static final String USERNAME_INDEX = "account_username_index";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(ACCOUNT_ID, DataTypes.INT)
                        .withColumn(USERNAME, DataTypes.TEXT)
                        .withColumn(PASSWORD, DataTypes.TEXT)
                        .withColumn(SECONDARY_PASSWORD, DataTypes.TEXT)
                        .withColumn(SLOT_COUNT, DataTypes.INT)
                        .withColumn(NX_CREDIT, DataTypes.INT)
                        .withColumn(NX_PREPAID, DataTypes.INT)
                        .withColumn(MAPLE_POINT, DataTypes.INT)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, getTableName())
                        .andColumn(USERNAME)
                        .build()
        );
    }
}
