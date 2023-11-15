package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class CharacterStatUDT {
    public static final String GENDER = "gender";
    public static final String SKIN = "skin";
    public static final String FACE = "face";
    public static final String HAIR = "hair";
    public static final String LEVEL = "level";
    public static final String JOB = "job";
    public static final String SUB_JOB = "sub_job";
    public static final String BASE_STR = "base_str";
    public static final String BASE_DEX = "base_dex";
    public static final String BASE_INT = "base_int";
    public static final String BASE_LUK = "base_luk";
    public static final String HP = "hp";
    public static final String MAX_HP = "max_hp";
    public static final String MP = "mp";
    public static final String MAX_MP = "max_mp";
    public static final String AP = "ap";
    public static final String SP = "sp";
    public static final String EXP = "exp";
    public static final String POP = "pop";
    public static final String POS_MAP = "pos_map";
    public static final String PORTAL = "portal";

    private static final String typeName = "character_stat_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(CharacterStatUDT.GENDER, DataTypes.TINYINT)
                        .withField(CharacterStatUDT.SKIN, DataTypes.TINYINT)
                        .withField(CharacterStatUDT.FACE, DataTypes.INT)
                        .withField(CharacterStatUDT.HAIR, DataTypes.INT)
                        .withField(CharacterStatUDT.LEVEL, DataTypes.TINYINT)
                        .withField(CharacterStatUDT.JOB, DataTypes.SMALLINT)
                        .withField(CharacterStatUDT.SUB_JOB, DataTypes.SMALLINT)
                        .withField(CharacterStatUDT.BASE_STR, DataTypes.SMALLINT)
                        .withField(CharacterStatUDT.BASE_DEX, DataTypes.SMALLINT)
                        .withField(CharacterStatUDT.BASE_INT, DataTypes.SMALLINT)
                        .withField(CharacterStatUDT.BASE_LUK, DataTypes.SMALLINT)
                        .withField(CharacterStatUDT.HP, DataTypes.INT)
                        .withField(CharacterStatUDT.MAX_HP, DataTypes.INT)
                        .withField(CharacterStatUDT.MP, DataTypes.INT)
                        .withField(CharacterStatUDT.MAX_MP, DataTypes.INT)
                        .withField(CharacterStatUDT.AP, DataTypes.SMALLINT)
                        .withField(CharacterStatUDT.SP, DataTypes.frozenListOf(DataTypes.INT))
                        .withField(CharacterStatUDT.EXP, DataTypes.INT)
                        .withField(CharacterStatUDT.POP, DataTypes.SMALLINT)
                        .withField(CharacterStatUDT.POS_MAP, DataTypes.INT)
                        .withField(CharacterStatUDT.PORTAL, DataTypes.TINYINT)
                        .build()
        );
    }
}
