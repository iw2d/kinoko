package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class MiniGameRecordUDT {
    public static final String OMOK_WINS = "omok_wins";
    public static final String OMOK_TIES = "omok_ties";
    public static final String OMOK_LOSSES = "omok_losses";
    public static final String OMOK_SCORE = "omok_score";
    public static final String MEMORY_WINS = "memory_wins";
    public static final String MEMORY_TIES = "memory_ties";
    public static final String MEMORY_LOSSES = "memory_losses";
    public static final String MEMORY_SCORE = "memory_score";

    private static final String typeName = "minigame_record_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(OMOK_WINS, DataTypes.INT)
                        .withField(OMOK_TIES, DataTypes.INT)
                        .withField(OMOK_LOSSES, DataTypes.INT)
                        .withField(OMOK_SCORE, DataTypes.DOUBLE)
                        .withField(MEMORY_WINS, DataTypes.INT)
                        .withField(MEMORY_TIES, DataTypes.INT)
                        .withField(MEMORY_LOSSES, DataTypes.INT)
                        .withField(MEMORY_SCORE, DataTypes.DOUBLE)
                        .build()
        );
    }
}
