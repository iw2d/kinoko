package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class PetDataUDT {
    public static final String PET_NAME = "pet_name";
    public static final String LEVEL = "level";
    public static final String FULLNESS = "fullness";
    public static final String TAMENESS = "tameness";
    public static final String PET_SKILL = "pet_skill";
    public static final String PET_ATTRIBUTE = "pet_attribute";
    public static final String REMAIN_LIFE = "remain_life";

    private static final String typeName = "pet_data_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(PET_NAME, DataTypes.TEXT)
                        .withField(LEVEL, DataTypes.TINYINT)
                        .withField(FULLNESS, DataTypes.TINYINT)
                        .withField(TAMENESS, DataTypes.SMALLINT)
                        .withField(PET_SKILL, DataTypes.SMALLINT)
                        .withField(PET_ATTRIBUTE, DataTypes.SMALLINT)
                        .withField(REMAIN_LIFE, DataTypes.INT)
                        .build()
        );
    }
}
