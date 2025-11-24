package kinoko.server;

import kinoko.util.Util;

public final class ServerConstants {
    public static final int GAME_VERSION = 95;
    public static final String PATCH = "1";
    public static final byte LOCALE = 8;

    public static final byte[] CENTRAL_HOST = Util.getHost(Util.getEnv("CENTRAL_HOST", "127.0.0.1"));
    public static final int CENTRAL_PORT = Util.getEnv("CENTRAL_PORT", 8282);

    public static final byte[] SERVER_HOST = Util.getHost(Util.getEnv("SERVER_HOST", "127.0.0.1"));
    public static final int LOGIN_PORT = 8484;
    public static final int CHANNEL_PORT = 8585;


    // ----------------- Database -----------------
    // Supports localized and containerized env variables.
    // It is advised to set these variables in the .env file.

    // General
    public static final String DATABASE_TYPE = Util.getEnv("DB_TYPE","cassandra").toLowerCase();
    public static final String DATABASE_HOST = Util.getEnv("DB_HOST", "127.0.0.1");
    public static final int DATABASE_PORT = Util.getEnv("DB_PORT", 9042);  // Defaulting to Cassandra port
    public static final String DATABASE_NAME = Util.getEnv("DB_NAME", "kinoko");  // Cassandra KeySpace, Postgres DB Name

    // Postgres Specific
    public static final String DATABASE_USER = Util.getEnv("DB_USER", "postgres");
    public static final String DATABASE_PASSWORD = Util.getEnv("DB_PASS","admin");

    // Cassandra Specific
    public static final String DATABASE_DATACENTER = Util.getEnv("DB_DATACENTER","datacenter1");
    public static final String DATABASE_PROFILE = Util.getEnv("DB_PROFILE_ONE","profile_one");


    // --------------- Family -----------------
    public static final int MAX_LEVEL_GAP_FOR_FAMILY = 20;  // max allowed level difference between a senior and junior.
    public static final int FAMILY_REP_PER_KILL = 3;
    public static final int FAMILY_REP_PER_BOSS_KILL = 20;
    public static final int FAMILY_REP_PER_LEVEL_UP = 200;
}

