package kinoko.database.cassandra.type;

public final class TemporaryStatUDT {
    public static final String N_OPTION = "n_option";
    public static final String R_OPTION = "r_option";
    public static final String T_OPTION = "t_option";
    public static final String DICE_INFO = "dice_info";
    public static final String EXPIRE_TIME = "expire_time";
    public static final String TWO_STATE_TYPE = "two_state_type";

    private static final String typeName = "temporary_stat_type";

    public static String getTypeName() {
        return typeName;
    }
}
