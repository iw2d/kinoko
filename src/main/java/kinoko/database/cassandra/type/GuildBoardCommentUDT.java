package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class GuildBoardCommentUDT {
    public static final String COMMENT_SN = "comment_sn";
    public static final String CHARACTER_ID = "character_id";
    public static final String TEXT = "text";
    public static final String DATE = "date";

    private static final String typeName = "guild_board_comment_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(COMMENT_SN, DataTypes.INT)
                        .withField(CHARACTER_ID, DataTypes.INT)
                        .withField(TEXT, DataTypes.TEXT)
                        .withField(DATE, DataTypes.TIMESTAMP)
                        .build()
        );
    }
}
