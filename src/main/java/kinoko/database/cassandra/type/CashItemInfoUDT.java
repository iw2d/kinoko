package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createType;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;

public final class CashItemInfoUDT {
    public static final String ITEM = "item";
    public static final String COMMODITY_ID = "commodity_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String CHARACTER_ID = "character_id";
    public static final String CHARACTER_NAME = "character_name";

    private static final String typeName = "cash_item_info_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(ITEM, udt(ItemUDT.getTypeName(), true))
                        .withField(COMMODITY_ID, DataTypes.INT)
                        .withField(ACCOUNT_ID, DataTypes.INT)
                        .withField(CHARACTER_ID, DataTypes.INT)
                        .withField(CHARACTER_NAME, DataTypes.TEXT)
                        .build()
        );
    }
}
