package kinoko.world.job;

import java.util.HashMap;
import java.util.Map;

public enum Job {
    // EXPLORERS -------------------------------------------------------------------------------------------------------
    BEGINNER(0),
    WARRIOR(100),
    FIGHTER(110),
    CRUSADER(111),
    HERO(112),
    PAGE(120),
    WHITE_KNIGHT(121),
    PALADIN(122),
    SPEARMAN(130),
    DRAGON_KNIGHT(131),
    DARK_KNIGHT(132),
    MAGICIAN(200),
    WIZARD_FP(210),
    MAGE_FP(211),
    ARCH_MAGE_FP(212),
    WIZARD_IL(220),
    MAGE_IL(221),
    ARCH_MAGE_IL(222),
    CLERIC(230),
    PRIEST(231),
    BISHOP(232),
    ARCHER(300),
    HUNTER(310),
    RANGER(311),
    BOWMASTER(312),
    CROSSBOWMAN(320),
    SNIPER(321),
    MARKSMAN(322),
    ROGUE(400),
    ASSASSIN(410),
    HERMIT(411),
    NIGHT_LORD(412),
    BANDIT(420),
    CHIEF_BANDIT(421),
    SHADOWER(422),
    BLADE_RECRUIT(430),
    BLADE_ACOLYTE(431),
    BLADE_SPECIALIST(432),
    BLADE_LORD(433),
    BLADE_MASTER(434),
    PIRATE(500),
    BRAWLER(510),
    MARAUDER(511),
    BUCCANEER(512),
    GUNSLINGER(520),
    OUTLAW(521),
    CORSAIR(522),

    // CYGNUS KNIGHTS --------------------------------------------------------------------------------------------------
    NOBLESSE(1000),
    DAWN_WARRIOR_1(1100),
    DAWN_WARRIOR_2(1110),
    DAWN_WARRIOR_3(1111),
    BLAZE_WIZARD_1(1200),
    BLAZE_WIZARD_2(1210),
    BLAZE_WIZARD_3(1211),
    WIND_ARCHER_1(1300),
    WIND_ARCHER_2(1310),
    WIND_ARCHER_3(1311),
    NIGHT_WALKER_1(1400),
    NIGHT_WALKER_2(1410),
    NIGHT_WALKER_3(1411),
    THUNDER_BREAKER_1(1500),
    THUNDER_BREAKER_2(1510),
    THUNDER_BREAKER_3(1511),

    // ARAN -------------------------------------------------------------------
    ARAN_BEGINNER(2000),
    ARAN_1(2100),
    ARAN_2(2110),
    ARAN_3(2111),
    ARAN_4(2112),

    // EVAN ------------------------------------------------------------------------------------------------------------
    EVAN_BEGINNER(2001),
    EVAN_1(2200),
    EVAN_2(2210),
    EVAN_3(2211),
    EVAN_4(2212),
    EVAN_5(2213),
    EVAN_6(2214),
    EVAN_7(2215),
    EVAN_8(2216),
    EVAN_9(2217),
    EVAN_10(2218),

    // RESISTANCE ------------------------------------------------------------------------------------------------------
    CITIZEN(3000),
    BATTLE_MAGE_1(3200),
    BATTLE_MAGE_2(3210),
    BATTLE_MAGE_3(3211),
    BATTLE_MAGE_4(3212),
    WILD_HUNTER_1(3300),
    WILD_HUNTER_2(3310),
    WILD_HUNTER_3(3311),
    WILD_HUNTER_4(3312),
    MECHANIC_1(3500),
    MECHANIC_2(3510),
    MECHANIC_3(3511),
    MECHANIC_4(3512),

    // MISC ------------------------------------------------------------------------------------------------------------
    MANAGER(800),
    GM(900),
    SUPER_GM(910),
    ADDITIONAL_SKILLS(9000);

    private static final Map<Short, Job> jobMap;

    static {
        jobMap = new HashMap<>();
        for (Job job : values()) {
            jobMap.put(job.getJobId(), job);
        }
    }

    private final short jobId;

    Job(int jobId) {
        this.jobId = (short) jobId;
    }

    public final short getJobId() {
        return jobId;
    }

    public static Job getById(short jobId) {
        return jobMap.get(jobId);
    }

    public static Job getById(int jobId) {
        return getById((short) jobId);
    }
}
