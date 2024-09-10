package kinoko.server.cashshop;

import java.util.Set;

public enum CommodityFlag {
    // CM
    ITEMID(0x1),
    COUNT(0x2),
    PRICE(0x4),
    BONUS(0x8),
    PRIORITY(0x10),
    PERIOD(0x20),
    MAPLEPOINT(0x40),
    MESO(0x80),
    FORPREMIUMUSER(0x100),
    COMMODITYGENDER(0x200),
    ONSALE(0x400),
    CLASS(0x800),
    LIMIT(0x1000),
    PBCASH(0x2000),
    PBPOINT(0x4000),
    PBGIFT(0x8000),
    PACKAGESN(0x10000),
    REQPOP(0x20000),
    REQLEV(0x40000),
    ALL(0x7FFFF);

    private final int value;

    CommodityFlag(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static int from(Set<CommodityFlag> flags) {
        return flags.stream()
                .map(CommodityFlag::getValue)
                .reduce(0, (a, b) -> (a | b));
    }
}
