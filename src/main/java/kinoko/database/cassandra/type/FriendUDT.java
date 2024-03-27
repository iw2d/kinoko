package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class FriendUDT {
    public static final String FRIEND_ID = "friend_id";
    public static final String FRIEND_NAME = "friend_name";
    public static final String FRIEND_GROUP = "friend_group";
    public static final String FRIEND_STATUS = "friend_status";

    private static final String typeName = "friend_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(FRIEND_ID, DataTypes.INT)
                        .withField(FRIEND_NAME, DataTypes.TEXT)
                        .withField(FRIEND_GROUP, DataTypes.TEXT)
                        .withField(FRIEND_STATUS, DataTypes.INT)
                        .build()
        );
    }
}
