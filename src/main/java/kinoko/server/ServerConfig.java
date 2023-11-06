package kinoko.server;

public final class ServerConfig {
    public static final int WORLD_ID = 0;
    public static final String WORLD_NAME = "Kinoko";
    public static final int CHANNELS_PER_WORLD = 5;

    public static final boolean AUTO_CREATE_ACCOUNT = false;
    public static final String DATABASE_URL = "jdbc:postgresql://127.0.0.1:5432/kinoko";
    public static final String DATABASE_USER = "postgres";
    public static final String DATABASE_PASS = "password";
    public static final String WZ_DIRECTORY = "wz";
}
