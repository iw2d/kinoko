package kinoko.world;

public final class GameConstants {
    public static final int CHARACTER_MAX_SLOTS = 15;
    public static final int INVENTORY_MAX_SLOTS = 96;

    public static final int MAX_DAMAGE = 999_999;

    public static final int MOB_ATTACK_CHANCE = 75;
    public static final int MOB_ATTACK_COOLTIME_MIN = 3;
    public static final int MOB_ATTACK_COOLTIME_MAX = 13;
    public static final int MOB_ATTACK_COOLTIME_MAX_BOSS = 7;

    public static boolean isValidCharacterName(String name) {
        return name.length() >= 4 && name.length() <= 13 && name.matches("[a-zA-Z0-9]+");
    }
}
