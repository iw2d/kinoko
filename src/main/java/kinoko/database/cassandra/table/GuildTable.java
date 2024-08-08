package kinoko.database.cassandra.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.cassandra.type.GuildBoardEntryUDT;
import kinoko.database.cassandra.type.GuildMemberUDT;

public final class GuildTable {
    public static final String GUILD_ID = "guild_id";
    public static final String GUILD_NAME = "guild_name";
    public static final String GUILD_NAME_INDEX = "guild_name_index";
    public static final String GRADE_NAMES = "grade_names";
    public static final String MEMBERS = "members";
    public static final String MEMBER_MAX = "member_max";
    public static final String MARK_BG = "mark_bg";
    public static final String MARK_BG_COLOR = "mark_bg_color";
    public static final String MARK = "mark";
    public static final String MARK_COLOR = "mark_color";
    public static final String NOTICE = "notice";
    public static final String POINTS = "points";
    public static final String LEVEL = "level";
    public static final String BOARD_ENTRY_LIST = "board_entry_list";
    public static final String BOARD_ENTRY_NOTICE = "board_entry_notice";
    public static final String BOARD_ENTRY_COUNTER = "board_entry_counter";

    private static final String tableName = "guild_table";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, getTableName())
                        .ifNotExists()
                        .withPartitionKey(GUILD_ID, DataTypes.INT)
                        .withColumn(GUILD_NAME, DataTypes.TEXT)
                        .withColumn(GUILD_NAME_INDEX, DataTypes.TEXT)
                        .withColumn(GRADE_NAMES, DataTypes.frozenListOf(DataTypes.TEXT))
                        .withColumn(MEMBERS, DataTypes.frozenListOf(SchemaBuilder.udt(GuildMemberUDT.getTypeName(), true)))
                        .withColumn(MEMBER_MAX, DataTypes.INT)
                        .withColumn(MARK_BG, DataTypes.SMALLINT)
                        .withColumn(MARK_BG_COLOR, DataTypes.TINYINT)
                        .withColumn(MARK, DataTypes.SMALLINT)
                        .withColumn(MARK_COLOR, DataTypes.TINYINT)
                        .withColumn(NOTICE, DataTypes.TEXT)
                        .withColumn(POINTS, DataTypes.INT)
                        .withColumn(LEVEL, DataTypes.TINYINT)
                        .withColumn(BOARD_ENTRY_LIST, DataTypes.frozenListOf(SchemaBuilder.udt(GuildBoardEntryUDT.getTypeName(), true)))
                        .withColumn(BOARD_ENTRY_NOTICE, SchemaBuilder.udt(GuildBoardEntryUDT.getTypeName(), true))
                        .withColumn(BOARD_ENTRY_COUNTER, DataTypes.INT)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, getTableName())
                        .andColumn(GUILD_NAME_INDEX)
                        .build()
        );
    }
}