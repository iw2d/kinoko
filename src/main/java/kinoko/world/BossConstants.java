package kinoko.world;

public final class BossConstants {
    public static final int BALROG_ENTRY_MAP = 105100100; // Balrog Temple : Bottom of the Temple
    public static final short BALROG_TIME_LIMIT = 3600; // 30 min
    public static final short BALROG_RELEASE_LEFT_CLAW_INTERVAL = 60; // 10 minutes
    public static final int BALROG_RUNS_PER_DAY = 1;
    public static final short BALROG_SPAWN_X = 412;
    public static final short BALROG_SPAWN_Y = 258;
    public static final int BALROG_LEFT_ARM = 8830001;
    public static final int BALROG_RIGHT_ARM = 8830002;
    public static final int BALROG_FAKE_LEFT_ARM = 8830004;
    public static final int BALROG_FAKE_RIGHT_ARM = 8830005;
    public static final long BALROG_COOLDOWN = 21600000; // 6 hrs

    // NORMAL BALROG CONSTANTS
    public static final Integer[] NORMAL_BALROG_IDS = {                     // All Normal Balrog Boss Template ID's
            8830010, 8830000, 8830001, 8830002, 8830004, 8830005,
    };

    public static final int BALROG_NORMAL_TREASURE_THIEF = 9402045;         // Normal Balrog Loot Mob
    public static final int BALROG_NORMAL_TREASURE_THIEF_HP = 1000000;      // Normal Balrog Loot Mob HP

    public static final int BALROG_NORMAL_BATTLE_MAP = 105100400;           // Normal Balrog Battle Map
    public static final int BALROG_NORMAL_WIN_MAP = 105100401;              // Normal Balrog Victory Map

    public static final int BALROG_NORMAL_BODY_HP = 3600000;                // Normal Balrog Body HP
    public static final int BALROG_NORMAL_ARM_HP = 1800000;                 // Normal Balrog Arm HP

    public static final int BALROG_NORMAL_DAMAGE_SINK = 8830010;            // Normal Balrog Damage Sink Template ID
    public static final int BALROG_NORMAL_BODY = 8830007;                   // Normal Balrog Body Template ID

    // MYSTIC BALROG CONSTANTS
    public static final Integer[] MYSTIC_BALROG_IDS = {                     // All Mystic Balrog Boss Template ID's
            8830010, 8830000, 8830001, 8830002, 8830004, 8830005,
    };

    public static final int BALROG_MYSTIC_TREASURE_THIEF = 9402046;         // Mystic Balrog Loot Mob
    public static final int BALROG_MYSTIC_TREASURE_THIEF_HP = 100000000;    // Mystic Balrog Loot Mob HP

    public static final int BALROG_MYSTIC_BATTLE_MAP = 105100300;           // Mystic Balrog Battle Map
    public static final int BALROG_MYSTIC_WIN_MAP = 105100301;              // Mystic Balrog Victory Map

    public static final long BALROG_MYSTIC_BODY_HP = 180000000000L;         // Mystic Balrog Body HP
    public static final long BALROG_MYSTIC_ARM_HP = 90000000000L;           // Mystic Balrog Arm HP

    public static final int BALROG_MYSTIC_DAMAGE_SINK = 8830010;            // Mystic Balrog Damage Sink Template ID
    public static final int BALROG_MYSTIC_BODY = 8830000;                   // Mystic Balrog Body Template ID
}