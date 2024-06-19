package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class WildHunterInfoUDT {
    public static final String RIDING_TYPE = "riding_type";
    public static final String CAPTURED_MOBS = "captured_mobs";

    private static final String typeName = "wild_hunter_info_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(RIDING_TYPE, DataTypes.INT)
                        .withField(CAPTURED_MOBS, DataTypes.frozenListOf(DataTypes.INT))
                        .build()
        );
    }
}
