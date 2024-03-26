package kinoko.provider.map;

import java.util.HashSet;
import java.util.Set;

public enum FieldOption {
    // FIELDOPT
    MOVE_LIMIT(0x1),
    SKILL_LIMIT(0x2),
    SUMMON_LIMIT(0x4),
    MYSTIC_DOOR_LIMIT(0x8),
    MIGRATE_LIMIT(0x10),
    PORTAL_SCROLL_LIMIT(0x20),
    TELEPORT_ITEM_LIMIT(0x40),
    MINIGAME_LIMIT(0x80),
    SPECIFIC_PORTAL_SCROLL_LIMIT(0x100),
    TAMING_MOB_LIMIT(0x200),
    STAT_CHANGE_ITEM_CONSUME_LIMIT(0x400),
    PARTY_BOSS_CHANGE_LIMIT(0x800),
    NO_MOB_CAPACITY_LIMIT(0x1000),
    WEDDING_INVITATION_LIMIT(0x2000),
    CASH_WEATHER_CONSUME_LIMIT(0x4000),
    NO_PET(0x8000),
    ANTI_MACRO_LIMIT(0x10_000),
    FALL_DOWN_LIMIT(0x20_000),
    SUMMON_NPC_LIMIT(0x40_000),
    NO_EXP_DECREASE(0x80_000),
    NO_DAMAGE_ON_FALLING(0x100_000),
    PARCEL_OPEN_LIMIT(0x200_000),
    DROP_LIMIT(0x400_000),
    ROCKET_BOOSTER_LIMIT(0x800_000);

    private final int value;

    FieldOption(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static Set<FieldOption> getByLimit(int fieldLimit) {
        final Set<FieldOption> fieldOptions = new HashSet<>();
        for (FieldOption option : values()) {
            if ((fieldLimit & option.getValue()) != 0) {
                fieldOptions.add(option);
            }
        }
        return fieldOptions;
    }
}
