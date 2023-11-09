package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class PetInfoUDT {
    public static final String PET_NAME = "pet_name";
    public static final String LEVEL = "level";
    public static final String FULLNESS = "fullness";
    public static final String TAMENESS = "tameness";
    public static final String PET_SKILL = "pet_skill";
    public static final String PET_ATTRIBUTE = "pet_attribute";
    public static final String REMAIN_LIFE = "remain_life";

    private static final String TYPE_NAME = "pet_info_type";

    public static String getTypeName() {
        return TYPE_NAME;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(PetInfoUDT.PET_NAME, DataTypes.TEXT)
                        .withField(PetInfoUDT.LEVEL, DataTypes.TINYINT)
                        .withField(PetInfoUDT.FULLNESS, DataTypes.TINYINT)
                        .withField(PetInfoUDT.TAMENESS, DataTypes.SMALLINT)
                        .withField(PetInfoUDT.PET_SKILL, DataTypes.SMALLINT)
                        .withField(PetInfoUDT.PET_ATTRIBUTE, DataTypes.SMALLINT)
                        .withField(PetInfoUDT.REMAIN_LIFE, DataTypes.INT)
                        .build()
        );
    }
}
