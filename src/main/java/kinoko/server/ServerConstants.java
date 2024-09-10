package kinoko.server;

import kinoko.util.Util;

public final class ServerConstants {
    public static final int GAME_VERSION = 95;
    public static final String PATCH = "1";
    public static final byte LOCALE = 8;

    public static final byte[] CENTRAL_HOST = new byte[]{ 127, 0, 0, 1 };
    public static final int CENTRAL_PORT = 8282;

    public static final byte[] SERVER_HOST = new byte[]{ 127, 0, 0, 1 };
    public static final int LOGIN_PORT = 8484;
    public static final int CHANNEL_PORT = 8585;

    public static final String DATABASE_HOST = Util.getEnv("DATABASE_HOST", "127.0.0.1");
    public static final int DATABASE_PORT = 9042;
}

