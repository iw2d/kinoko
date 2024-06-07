package kinoko.provider.map;

import java.util.HashSet;
import java.util.Set;

public enum FieldOption {
    // FIELDOPT
    MOVELIMIT(0x1),
    SKILLLIMIT(0x2),
    SUMMONLIMIT(0x4),
    MYSTICDOORLIMIT(0x8),
    MIGRATELIMIT(0x10),
    PORTALSCROLLLIMIT(0x20),
    TELEPORTITEMLIMIT(0x40),
    MINIGAMELIMIT(0x80),
    SPECIFICPORTALSCROLLLIMIT(0x100),
    TAMINGMOBLIMIT(0x200),
    STATCHANGEITEMCONSUMELIMIT(0x400),
    PARTYBOSSCHANGELIMIT(0x800),
    NOMOBCAPACITYLIMIT(0x1000),
    WEDDINGINVITATIONLIMIT(0x2000),
    CASHWEATHERCONSUMELIMIT(0x4000),
    NOPET(0x8000),
    ANTIMACROLIMIT(0x10_000),
    FALLDOWNLIMIT(0x20_000),
    SUMMONNPCLIMIT(0x40_000),
    NOEXPDECREASE(0x80_000),
    NODAMAGEONFALLING(0x100_000),
    PARCELOPENLIMIT(0x200_000),
    DROPLIMIT(0x400_000),
    ROCKETBOOSTERLIMIT(0x800_000);

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
