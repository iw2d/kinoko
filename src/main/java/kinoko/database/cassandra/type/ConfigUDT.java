package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class ConfigUDT {
    public static final String MACRO_SYS_DATA = "macro_sys_data";
    public static final String FUNC_KEY_MAP = "func_key_map";
    public static final String QUICKSLOT_KEY_MAP = "quickslot_key_map";
    public static final String PET_CONSUME_ITEM = "pet_consume_item";
    public static final String PET_CONSUME_MP_ITEM = "pet_consume_mp_item";

    private static final String typeName = "config_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(MACRO_SYS_DATA, DataTypes.BLOB)
                        .withField(FUNC_KEY_MAP, DataTypes.BLOB)
                        .withField(QUICKSLOT_KEY_MAP, DataTypes.BLOB)
                        .withField(PET_CONSUME_ITEM, DataTypes.INT)
                        .withField(PET_CONSUME_MP_ITEM, DataTypes.INT)
                        .build()
        );
    }
}
