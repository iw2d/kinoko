package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class QuestRecordUDT {
    public static final String QUEST_ID = "quest_id";
    public static final String QUEST_STATE = "quest_state";
    public static final String QUEST_VALUE = "quest_value";
    public static final String COMPLETED_TIME = "completed_time";

    private static final String typeName = "quest_record_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(QUEST_ID, DataTypes.INT)
                        .withField(QUEST_STATE, DataTypes.INT)
                        .withField(QUEST_VALUE, DataTypes.TEXT)
                        .withField(COMPLETED_TIME, DataTypes.TIMESTAMP)
                        .build()
        );
    }
}
