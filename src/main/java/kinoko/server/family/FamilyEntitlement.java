package kinoko.server.family;


import kinoko.util.Timing;

/**
 * Represents a Family Entitlement in the server.
 *
 * Each entitlement defines a privilege or buff that a player or family can use.
 *
 * Fields:
 * - usageLimit: the maximum number of times this entitlement can be used.
 * - repCost: the cost in reputation points to use this entitlement.
 * - usageResetAfterMinutes: When the entitlement usage can be reset (typically 24 hours)
 * - name: display name of the entitlement.
 * - description: detailed description of the effect.
 * - type (byte): determines how the entitlement is applied:
 *      1 = can be used on a specific family member (requires a FamilyMember input)
 *      2 = does not require a specific target (applies automatically or globally)
 * - modifier (double): The modifier of a base stat.
 * - expiresAfterMinutes (int): Minutes until the buff ends.
 */
public enum FamilyEntitlement {
    FAMILY_REUNION(1, 100, "Family Reunion",
            "[Target] Me\n[Effect] Teleport directly to the Family member of your choice.",
            Timing.DAY_MINUTES, (byte) 1, null, null),

    SUMMON_FAMILY(1, 200, "Summon Family",
            "[Target] 1 Family member\n[Effect] Summon a Family member of choice to the map you're in.",
            Timing.DAY_MINUTES, (byte) 1, null, null),

    FAMILY_HASTE(1, 500, "Quicker Together",
            "[Target] All Family Members\n[Effect] All family members, regardless of map, " +
                    "are blessed with Family Haste.",
            Timing.DAY_MINUTES, (byte) 2, null, null),

    FAMILY_EXP(1, 5000, "A Better Experience",
            "[Target] All Family Members\n[Effect] For 15 minutes, all family members receive 1.2x experience, " +
                    "regardless of map.",
            Timing.DAY_MINUTES, (byte) 2, 1.2, 15),

    FAMILY_DROP(1, 5000, "All The Drops",
            "[Target] All Family Members\n[Effect] For 15 minutes, " +
                    "all family members receive 1.2x drop rate, regardless of map.",
            Timing.DAY_MINUTES, (byte) 2, 1.2, 15),

    SELF_DROP_1_5(1, 8000, "My Drop Rate 1.5x (15 min)",
            "[Target] Me\n[Time] 15 min\n[Effect] Monster drop rate will be increased #c1.5x#.",
            Timing.DAY_MINUTES, (byte) 2, 1.5, 15),

    SELF_EXP_1_5(1, 8000, "My EXP 1.5x (15 min)",
            "[Target] Me\n[Time] 15 min\n[Effect] EXP earned from hunting will be increased #c1.5x#.",
            Timing.DAY_MINUTES, (byte) 2, 1.5, 15);

    private final int usageLimit, repCost, usageResetAfterMinutes;
    private final Integer expiresAfterMinutes;
    private final Double modifier;
    private final String name, description;
    private final byte type;

    FamilyEntitlement(int usageLimit, int repCost, String name, String description,
                      int usageResetAfterMinutes, byte type, Double modifier, Integer expiresAfterMinutes) {
        this.usageLimit = usageLimit;
        this.repCost = repCost;
        this.name = name;
        this.description = description;
        this.usageResetAfterMinutes = usageResetAfterMinutes;
        this.type = type;
        this.modifier = modifier;
        this.expiresAfterMinutes = expiresAfterMinutes;

    }
    public Integer getExpiresAfterMinutes() {
        return expiresAfterMinutes;
    }

    public Double getModifier() {
        return modifier;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public int getRepCost() {
        return repCost;
    }

    public int getUsageResetAfterMinutes(){ return usageResetAfterMinutes;}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public byte getType(){
        return type;
    }

    public String getStatName() {
        if (this == FAMILY_EXP || this == SELF_EXP_1_5) {
            return "EXP";
        }
        if (this == FAMILY_DROP || this == SELF_DROP_1_5) {
            return "DROP";
        }
        return ""; // e.g. teleport/haste/etc
    }
}

