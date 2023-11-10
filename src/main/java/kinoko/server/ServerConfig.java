package kinoko.server;

import kinoko.world.GameConstants;

public final class ServerConfig {
    public static final int WORLD_ID = 0;
    public static final String WORLD_NAME = "Kinoko";
    public static final int CHANNELS_PER_WORLD = 5;

    public static final boolean AUTO_CREATE_ACCOUNT = true;
    public static final boolean REQUIRE_SECONDARY_PASSWORD = true;
    public static final String WZ_DIRECTORY = "wz";
    public static final String DAT_DIRECTORY = "dat";

    public static final int CHARACTER_BASE_SLOTS = 3;
    public static final int INVENTORY_BASE_SLOTS = 24;
    public static final int INVENTORY_CASH_SLOTS = GameConstants.INVENTORY_MAX_SLOTS;
}
