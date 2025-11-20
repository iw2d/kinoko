package kinoko.server.family;


/**
 * Represents a Family Entitlement in the server.
 *
 * Each entitlement defines a privilege or buff that a player or family can use.
 *
 * Fields:
 * - usageLimit: the maximum number of times this entitlement can be used.
 * - repCost: the cost in reputation points to use this entitlement.
 * - expiresAfterMinutes: how long the effect lasts in minutes.
 * - name: display name of the entitlement.
 * - description: detailed description of the effect.
 * - type (byte): determines the target of the entitlement:
 *      1 = affects the player individually (self or single target)
 *      2 = affects all family members (group-wide effect)
 *
 * Example usage:
 *   FamilyEntitlement.FAMILY_EXP.getType(); // returns 2
 */
public enum FamilyEntitlement {
    FAMILY_REUNION(1, 100, "Family Reunion",
            "[Target] Me\n[Effect] Teleport directly to the Family member of your choice.",
            1440, (byte) 1),

    SUMMON_FAMILY(1, 200, "Summon Family",
            "[Target] 1 Family member\n[Effect] Summon a Family member of choice to the map you're in.",
            1440, (byte) 1),

    FAMILY_HASTE(1, 500, "Quicker Together",
            "[Target] All Family Members\n[Effect] All family members, regardless of map, " +
                    "are blessed with Family Haste.",
            1440, (byte) 2),

    FAMILY_EXP(1, 5000, "A Better Experience",
            "[Target] All Family Members\n[Effect] For 15 minutes, all family members receive 1.2x experience, " +
                    "regardless of map.",
            1440, (byte) 2),

    FAMILY_DROP(1, 5000, "All The Drops",
            "[Target] All Family Members\n[Effect] For 15 minutes, " +
                    "all family members receive 1.2x drop rate, regardless of map.",
            1440, (byte) 2),

    SELF_DROP_1_5(1, 8000, "My Drop Rate 1.5x (15 min)",
            "[Target] Me\n[Time] 15 min\n[Effect] Monster drop rate will be increased #c1.5x#.",
            1440, (byte) 1),

    SELF_EXP_1_5(1, 8000, "My EXP 1.5x (15 min)",
            "[Target] Me\n[Time] 15 min\n[Effect] EXP earned from hunting will be increased #c1.5x#.",
            1440, (byte) 1);

    private final int usageLimit, repCost, expiresAfterMinutes;
    private final String name, description;
    private final byte type;

    FamilyEntitlement(int usageLimit, int repCost, String name, String description, int expiresAfterMinutes, byte type) {
        this.usageLimit = usageLimit;
        this.repCost = repCost;
        this.name = name;
        this.description = description;
        this.expiresAfterMinutes = expiresAfterMinutes;
        this.type = type;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public int getRepCost() {
        return repCost;
    }

    public int getExpiresAfterMinutes(){ return expiresAfterMinutes;}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public byte getType(){
        return type;
    }
}

