package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class GuildMemberUDT {
    public static final String CHARACTER_ID = "character_id";
    public static final String CHARACTER_NAME = "func_key_map";
    public static final String JOB = "job";
    public static final String LEVEL = "level";
    public static final String GUILD_RANK = "guild_rank";
    public static final String ALLIANCE_RANK = "alliance_rank";

    private static final String typeName = "guild_member_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(CHARACTER_ID, DataTypes.INT)
                        .withField(CHARACTER_NAME, DataTypes.TEXT)
                        .withField(JOB, DataTypes.INT)
                        .withField(LEVEL, DataTypes.INT)
                        .withField(GUILD_RANK, DataTypes.INT)
                        .withField(ALLIANCE_RANK, DataTypes.INT)
                        .build()
        );
    }
}
