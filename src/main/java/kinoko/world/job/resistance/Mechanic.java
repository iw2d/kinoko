package kinoko.world.job.resistance;

import kinoko.world.job.JobHandler;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Mechanic {
    // MECHANIC_1
    public static final int FLAME_LAUNCHER = 35001001;
    public static final int MECH_PROTOTYPE = 35001002;
    public static final int ME_07_DRILLHANDS = 35001003;
    public static final int GATLING_GUN = 35001004;
    // MECHANIC_2
    public static final int MECHANIC_MASTERY = 35100000;
    public static final int HEAVY_WEAPON_MASTERY = 35100008;
    public static final int ATOMIC_HAMMER = 35101003;
    public static final int ROCKET_BOOSTER = 35101004;
    public static final int OPEN_PORTAL_GX_9 = 35101005;
    public static final int MECHANIC_RAGE = 35101006;
    public static final int PERFECT_ARMOR = 35101007;
    public static final int ENHANCED_FLAME_LAUNCHER = 35101009;
    public static final int ENHANCED_GATLING_GUN = 35101010;
    // MECHANIC_3
    public static final int METAL_FIST_MASTERY = 35110014;
    public static final int SATELLITE = 35111001;
    public static final int ROCK_N_SHOCK = 35111002;
    public static final int MECH_SIEGE_MODE = 35111004;
    public static final int ACCELERATION_BOT_EX_7 = 35111005;
    public static final int SATELLITE_2 = 35111009;
    public static final int SATELLITE_3 = 35111010;
    public static final int HEALING_ROBOT_H_LX = 35111011;
    public static final int ROLL_OF_THE_DICE = 35111013;
    public static final int PUNCH_LAUNCHER = 35111015;
    // MECHANIC_4
    public static final int EXTREME_MECH = 35120000;
    public static final int ROBOT_MASTERY = 35120001;
    public static final int GIANT_ROBOT_SG_88 = 35121003;
    public static final int MECH_MISSILE_TANK = 35121005;
    public static final int SATELLITE_SAFETY = 35121006;
    public static final int MAPLE_WARRIOR_MECH = 35121007;
    public static final int HEROS_WILL_MECH = 35121008;
    public static final int BOTS_N_TOTS = 35121009;
    public static final int AMPLIFIER_ROBOT_AF_11 = 35121010;
    public static final int BOTS_N_TOTS_DUMMY = 35121011;
    public static final int LASER_BLAST = 35121012;
    public static final int MECH_SIEGE_MODE_2 = 35121013;
    private static final Logger log = LogManager.getLogger(JobHandler.class);

    public static void handleAttack(User user, Attack attack) {
        final int skillId = attack.skillId;
        final int slv = attack.slv;
        switch (skillId) {
        }
    }

    public static void handleSkill(User user, Skill skill) {
        log.error("Unhandled skill {}", skill.skillId);
    }
}