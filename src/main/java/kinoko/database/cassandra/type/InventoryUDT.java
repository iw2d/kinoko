package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class InventoryUDT {
    public static final String ITEMS = "items";
    public static final String SIZE = "size";


    private static final String TYPE_NAME = "inventory_type";
    public static String getTypeName() {
        return TYPE_NAME;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, TYPE_NAME)
                        .ifNotExists()
                        .withField(ITEMS, DataTypes.frozenMapOf(DataTypes.INT, SchemaBuilder.udt(ItemUDT.getTypeName(), true)))
                        .withField(SIZE, DataTypes.INT)
                        .build()
        );
    }
}
