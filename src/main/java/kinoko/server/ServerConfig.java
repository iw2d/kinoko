package kinoko.server;

import kinoko.world.GameConstants;

public final class ServerConfig {
    public static final int WORLD_ID = 0;
    public static final String WORLD_NAME = "Kinoko";
    public static final int CHANNELS_PER_WORLD = 5;
    public static final int CENTRAL_REQUEST_TTL = 5;
    public static final int FIELD_TICK_INTERVAL = 100; // 100 ms tick
    public static final int SHUTDOWN_TIMEOUT = 30;

    public static final boolean AUTO_CREATE_ACCOUNT = true;
    public static final boolean REQUIRE_SECONDARY_PASSWORD = true;
    public static final String WZ_DIRECTORY = "wz";
    public static final String DATA_DIRECTORY = "data";
    public static final String SCRIPT_DIRECTORY = "scripts";

    public static final int CHARACTER_BASE_SLOTS = 3;
    public static final int INVENTORY_BASE_SLOTS = 24;
    public static final int INVENTORY_CASH_SLOTS = GameConstants.INVENTORY_SLOT_MAX;
    public static final int TRUNK_BASE_SLOTS = 4;
    public static final int FRIEND_MAX_BASE = 20;

    public static final String COMMAND_PREFIX = "!";
}
