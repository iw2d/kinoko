package kinoko.world.job.cygnus;

import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillDispatcher;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WindArcher {
    // WIND_ARCHER_1
    public static final int CRITICAL_SHOT = 13000000;
    public static final int THE_EYE_OF_AMAZON = 13000001;
    public static final int FOCUS = 13001002;
    public static final int DOUBLE_SHOT = 13001003;
    public static final int STORM = 13001004;
    // WIND_ARCHER_2
    public static final int BOW_MASTERY = 13100000;
    public static final int THRUST = 13100004;
    public static final int BOW_BOOSTER = 13101001;
    public static final int FINAL_ATTACK = 13101002;
    public static final int SOUL_ARROW = 13101003;
    public static final int STORM_BREAK = 13101005;
    public static final int WIND_WALK = 13101006;
    // WIND_ARCHER_3
    public static final int BOW_EXPERT = 13110003;
    public static final int ARROW_RAIN = 13111000;
    public static final int STRAFE = 13111001;
    public static final int HURRICANE = 13111002;
    public static final int PUPPET = 13111004;
    public static final int EAGLE_EYE = 13111005;
    public static final int WIND_PIERCING = 13111006;
    public static final int WIND_SHOT = 13111007;

    private static final Logger log = LogManager.getLogger(SkillDispatcher.class);

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
