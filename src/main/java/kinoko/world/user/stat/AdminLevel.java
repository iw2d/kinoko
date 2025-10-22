package kinoko.world.user.stat;

public enum AdminLevel {
    ADMIN((short) 0),  // ADMIN_LEVEL_1
    MANAGER((short) 1),  // ADMIN_LEVEL_2
    SUPER_GM((short) 2),  // ADMIN_LEVEL_3
    GM((short) 3),  // ADMIN_LEVEL_4
    JR_GM((short) 4),  // ADMIN_LEVEL_5
    TESTER((short) 5),  // ADMIN_LEVEL_10
    PLAYER((short) 6);  // No Corresponding Client Ver for nSubGradeCode.

    private final short value;

    /**
     * Creates a new admin level with the given numeric value.
     * @param value The short value representing this level.
     */
    AdminLevel(short value) {
        this.value = value;
    }

    /**
     * Gets the numeric value representing this admin level.
     * @return The short value of the admin level.
     */
    public short getValue() {
        return value;
    }

    /**
     * Returns the corresponding AdminLevel for the given numeric value.
     * If the value does not match any level, defaults to PLAYER.
     * @param value The numeric value to look up.
     * @return The corresponding AdminLevel, or PLAYER if not found.
     */
    public static AdminLevel fromValue(short value) {
        for (AdminLevel level : values()) {
            if (level.value == value) {
                return level;
            }
        }
        return PLAYER;
    }

    /**
     * Checks if this level has at least the same or higher authority as another level.
     * Lower numeric values indicate higher privileges.
     * @param other The other AdminLevel to compare against.
     * @return True if this level has equal or higher authority; false otherwise.
     */
    public boolean isAtLeast(AdminLevel other) {
        return this.value <= other.value;
    }
}