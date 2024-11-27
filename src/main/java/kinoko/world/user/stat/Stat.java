package kinoko.world.user.stat;

import java.util.List;
import java.util.Set;

public enum Stat {
    // CS
    SKIN(0x1),
    FACE(0x2),
    HAIR(0x4),
    PETSN(0x8),
    LEVEL(0x10),
    JOB(0x20),
    STR(0x40),
    DEX(0x80),
    INT(0x100),
    LUK(0x200),
    HP(0x400),
    MHP(0x800),
    MP(0x1000),
    MMP(0x2000),
    AP(0x4000),
    SP(0x8000),
    EXP(0x10000),
    POP(0x20000),
    MONEY(0x40000),
    PETSN2(0x80000),
    PETSN3(0x100000),
    TEMPEXP(0x200000),
    FATIGUE(0x400000),
    CHARISMAEXP(0x800000),
    INSIGHTEXP(0x1000000),
    WILLEXP(0x2000000),
    CRAFTEXP(0x4000000),
    SENSEEXP(0x8000000),
    CHARMEXP(0x10000000),
    NCSLIMIT(0x20000000),
    PVP(0x40000000),
    PVPMODELEVEL(0x80000000L);

    public static final List<Stat> ENCODE_ORDER = List.of(values());

    private final long value;

    Stat(long value) {
        this.value = value;
    }

    public final long getValue() {
        return value;
    }

    public static Stat getByValue(long value) {
        for (Stat stat : values()) {
            if (stat.getValue() == value) {
                return stat;
            }
        }
        return null;
    }

    public static long from(Set<Stat> stats) {
        return stats.stream()
                .mapToLong(Stat::getValue)
                .reduce(0, (a, b) -> a | b);
    }
}
