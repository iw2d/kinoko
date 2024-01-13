package kinoko.packet.world;

import java.util.Set;

// TODO: refactor into CharacterStat?
public enum StatFlag {
    SKIN(0x1),
    FACE(0x2),
    HAIR(0x4),
    PET_1(0x8),
    LEVEL(0x10),
    JOB(0x20),
    STR(0x40),
    DEX(0x80),
    INT(0x100),
    LUK(0x200),
    HP(0x400),
    MAX_HP(0x800),
    MP(0x1000),
    MAX_MP(0x2000),
    AP(0x4000),
    SP(0x8000),
    EXP(0x10000),
    POP(0x20000),
    MONEY(0x40000),
    PET_2(0x80000),
    PET_3(0x100000),
    TEMP_EXP(0x200000);

    private final int value;

    StatFlag(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static int from(Set<StatFlag> flags) {
        return flags.stream()
                .mapToInt(StatFlag::getValue)
                .reduce(0, (a, b) -> a | b);
    }
}
