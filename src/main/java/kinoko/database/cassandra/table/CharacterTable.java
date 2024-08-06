package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.cassandra.type.*;

public final class CharacterTable {
    public static final String CHARACTER_ID = "character_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String CHARACTER_NAME = "character_name";
    public static final String CHARACTER_NAME_INDEX = "character_name_index";
    public static final String CHARACTER_STAT = "character_stat";
    public static final String CHARACTER_EQUIPPED = "character_equipped";
    public static final String EQUIP_INVENTORY = "equip_inventory";
    public static final String CONSUME_INVENTORY = "consume_inventory";
    public static final String INSTALL_INVENTORY = "install_inventory";
    public static final String ETC_INVENTORY = "etc_inventory";
    public static final String CASH_INVENTORY = "cash_inventory";
    public static final String MONEY = "money";
    public static final String EXT_SLOT_EXPIRE = "ext_slot_expire";
    public static final String SKILL_COOLTIMES = "skill_cooltimes";
    public static final String SKILL_RECORDS = "skill_records";
    public static final String QUEST_RECORDS = "quest_records";
    public static final String CONFIG = "config";
    public static final String MINIGAME_RECORD = "minigame_record";
    public static final String MAP_TRANSFER_INFO = "map_transfer_info";
    public static final String WILD_HUNTER_INFO = "wild_hunter_info";
    public static final String ITEM_SN_COUNTER = "item_sn_counter";
    public static final String FRIEND_MAX = "friend_max";
    public static final String PARTY_ID = "party_id";
    public static final String GUILD_ID = "guild_id";

    private static final String tableName = "character_table";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(CHARACTER_ID, DataTypes.INT)
                        .withColumn(ACCOUNT_ID, DataTypes.INT)
                        .withColumn(CHARACTER_NAME, DataTypes.TEXT)
                        .withColumn(CHARACTER_NAME_INDEX, DataTypes.TEXT)
                        .withColumn(CHARACTER_STAT, SchemaBuilder.udt(CharacterStatUDT.getTypeName(), true))
                        .withColumn(CHARACTER_EQUIPPED, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(EQUIP_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(CONSUME_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(INSTALL_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(ETC_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(CASH_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(MONEY, DataTypes.INT)
                        .withColumn(EXT_SLOT_EXPIRE, DataTypes.TIMESTAMP)
                        .withColumn(SKILL_COOLTIMES, DataTypes.frozenMapOf(DataTypes.INT, DataTypes.TIMESTAMP))
                        .withColumn(SKILL_RECORDS, DataTypes.frozenListOf(SchemaBuilder.udt(SkillRecordUDT.getTypeName(), true)))
                        .withColumn(QUEST_RECORDS, DataTypes.frozenListOf(SchemaBuilder.udt(QuestRecordUDT.getTypeName(), true)))
                        .withColumn(MINIGAME_RECORD, SchemaBuilder.udt(MiniGameRecordUDT.getTypeName(), true))
                        .withColumn(MAP_TRANSFER_INFO, SchemaBuilder.udt(MapTransferInfoUDT.getTypeName(), true))
                        .withColumn(WILD_HUNTER_INFO, SchemaBuilder.udt(WildHunterInfoUDT.getTypeName(), true))
                        .withColumn(CONFIG, SchemaBuilder.udt(ConfigUDT.getTypeName(), true))
                        .withColumn(ITEM_SN_COUNTER, DataTypes.INT)
                        .withColumn(FRIEND_MAX, DataTypes.INT)
                        .withColumn(PARTY_ID, DataTypes.INT)
                        .withColumn(GUILD_ID, DataTypes.INT)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, getTableName())
                        .andColumn(ACCOUNT_ID)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, getTableName())
                        .andColumn(CHARACTER_NAME_INDEX)
                        .build()
        );
    }
}
