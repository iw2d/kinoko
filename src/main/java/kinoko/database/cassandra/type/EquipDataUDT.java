package kinoko.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public final class EquipDataUDT {
    public static final String INC_STR = "inc_str";
    public static final String INC_DEX = "inc_dex";
    public static final String INC_INT = "inc_int";
    public static final String INC_LUK = "inc_luk";
    public static final String INC_MAX_HP = "inc_max_hp";
    public static final String INC_MAX_MP = "inc_max_mp";
    public static final String INC_PAD = "inc_pad";
    public static final String INC_MAD = "inc_mad";
    public static final String INC_PDD = "inc_pdd";
    public static final String INC_MDD = "inc_mdd";
    public static final String INC_ACC = "inc_acc";
    public static final String INC_EVA = "inc_eva";
    public static final String INC_CRAFT = "inc_craft";
    public static final String INC_SPEED = "inc_speed";
    public static final String INC_JUMP = "inc_jump";

    public static final String RUC = "ruc";
    public static final String CUC = "cuc";
    public static final String IUC = "iuc";
    public static final String CHUC = "chuc";

    public static final String GRADE = "grade";
    public static final String OPTION_1 = "option_1";
    public static final String OPTION_2 = "option_2";
    public static final String OPTION_3 = "option_3";
    public static final String SOCKET_1 = "socket_1";
    public static final String SOCKET_2 = "socket_2";

    public static final String LEVEL_UP_TYPE = "level_up_type";
    public static final String LEVEL = "level";
    public static final String EXP = "exp";
    public static final String DURABILITY = "durability";

    private static final String typeName = "equip_info_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(INC_STR, DataTypes.SMALLINT)
                        .withField(INC_DEX, DataTypes.SMALLINT)
                        .withField(INC_INT, DataTypes.SMALLINT)
                        .withField(INC_LUK, DataTypes.SMALLINT)
                        .withField(INC_MAX_HP, DataTypes.SMALLINT)
                        .withField(INC_MAX_MP, DataTypes.SMALLINT)
                        .withField(INC_PAD, DataTypes.SMALLINT)
                        .withField(INC_MAD, DataTypes.SMALLINT)
                        .withField(INC_PDD, DataTypes.SMALLINT)
                        .withField(INC_MDD, DataTypes.SMALLINT)
                        .withField(INC_ACC, DataTypes.SMALLINT)
                        .withField(INC_EVA, DataTypes.SMALLINT)
                        .withField(INC_CRAFT, DataTypes.SMALLINT)
                        .withField(INC_SPEED, DataTypes.SMALLINT)
                        .withField(INC_JUMP, DataTypes.SMALLINT)
                        .withField(RUC, DataTypes.TINYINT)
                        .withField(CUC, DataTypes.TINYINT)
                        .withField(IUC, DataTypes.INT)
                        .withField(CHUC, DataTypes.TINYINT)
                        .withField(GRADE, DataTypes.TINYINT)
                        .withField(OPTION_1, DataTypes.SMALLINT)
                        .withField(OPTION_2, DataTypes.SMALLINT)
                        .withField(OPTION_3, DataTypes.SMALLINT)
                        .withField(SOCKET_1, DataTypes.SMALLINT)
                        .withField(SOCKET_2, DataTypes.SMALLINT)
                        .withField(LEVEL_UP_TYPE, DataTypes.TINYINT)
                        .withField(LEVEL, DataTypes.TINYINT)
                        .withField(EXP, DataTypes.INT)
                        .withField(DURABILITY, DataTypes.INT)
                        .build()
        );
    }
}
