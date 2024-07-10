package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class RingDataUDT {
    public static final String PAIR_CHARACTER_ID = "pair_character_id";
    public static final String PAIR_CHARACTER_NAME = "pair_character_name";
    public static final String PAIR_ITEM_SN = "pair_item_sn";

    private static final String typeName = "ring_data_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(PAIR_CHARACTER_ID, DataTypes.INT)
                        .withField(PAIR_CHARACTER_NAME, DataTypes.TEXT)
                        .withField(PAIR_ITEM_SN, DataTypes.BIGINT)
                        .build()
        );
    }
}
