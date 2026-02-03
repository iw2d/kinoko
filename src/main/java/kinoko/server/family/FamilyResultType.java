package kinoko.server.family;

public enum FamilyResultType {
    // Success operations
    UnregisterJunior(1),       // Junior removed / family ties severed

    // Error / warning messages
    CannotAddJunior(64),         // 0x40
    IncorrectOrOffline(65),      // 0x41
    SameFamily(66),              // 0x42
    DifferentFamily(67),         // 0x43
    JuniorMustBeInSameMap(69),   // 0x45
    AlreadyJuniorOfAnother(70),  // 0x46
    JuniorMustBeLowerRank(71),   // 0x47
    LevelGapTooHigh(72),         // 0x48
    AnotherRequestPending(73),   // 0x49
    AnotherSummonPending(74),    // 0x4A
    SummonFailed(75),            // 0x4B
    MaxGenerationsReached(76),   // 0x4C
    JuniorMustBeOverLevel10(77), // 0x4D
    CannotAddAfterWorldChange(79), // 0x4F
    SeparationNotEnoughMesos1(80), // 0x50
    SeparationNotEnoughMesos2(81), // 0x51
    EntitlementLevelMismatch(82); // 0x52


    private final int value;

    FamilyResultType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FamilyResultType getByValue(int value) {
        for (FamilyResultType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}