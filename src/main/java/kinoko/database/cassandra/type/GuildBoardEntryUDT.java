package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class GuildBoardEntryUDT {
    public static final String ENTRY_ID = "entry_id";
    public static final String CHARACTER_ID = "character_id";
    public static final String TITLE = "title";
    public static final String TEXT = "text";
    public static final String DATE = "date";
    public static final String EMOTICON = "emoticon";
    public static final String COMMENTS = "comments";
    public static final String COMMENT_SN_COUNTER = "comment_sn_counter";

    private static final String typeName = "guild_board_entry_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(ENTRY_ID, DataTypes.INT)
                        .withField(CHARACTER_ID, DataTypes.INT)
                        .withField(TITLE, DataTypes.TEXT)
                        .withField(TEXT, DataTypes.TEXT)
                        .withField(DATE, DataTypes.TIMESTAMP)
                        .withField(EMOTICON, DataTypes.INT)
                        .withField(COMMENTS, DataTypes.frozenListOf(SchemaBuilder.udt(GuildBoardCommentUDT.getTypeName(), true)))
                        .withField(COMMENT_SN_COUNTER, DataTypes.INT)
                        .build()
        );
    }
}
