package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class SkillRecordUDT {
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String MASTER_LEVEL = "master_level";

    private static final String typeName = "skill_record_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(SKILL_ID, DataTypes.INT)
                        .withField(SKILL_LEVEL, DataTypes.INT)
                        .withField(MASTER_LEVEL, DataTypes.INT)
                        .build()
        );
    }
}
