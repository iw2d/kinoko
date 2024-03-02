package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class FuncKeyManUDT {
    public static final String FUNC_KEY_MAP = "func_key_map";
    public static final String QUICKSLOT_KEY_MAP = "quickslot_key_map";
    public static final String PET_CONSUME_ITEM = "pet_consume_item";
    public static final String PET_CONSUME_MP_ITEM = "pet_consume_mp_item";

    private static final String typeName = "func_key_man_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(FUNC_KEY_MAP, DataTypes.frozenMapOf(DataTypes.INT, DataTypes.TEXT))
                        .withField(QUICKSLOT_KEY_MAP, DataTypes.frozenMapOf(DataTypes.INT, DataTypes.INT))
                        .withField(PET_CONSUME_ITEM, DataTypes.INT)
                        .withField(PET_CONSUME_MP_ITEM, DataTypes.INT)
                        .build()
        );
    }
}
