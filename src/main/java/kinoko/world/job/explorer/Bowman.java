package kinoko.world.job.explorer;

import kinoko.provider.skill.SkillInfo;
import kinoko.world.job.JobHandler;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Bowman {
    // ARCHER
    public static final int CRITICAL_SHOT = 3000001;
    public static final int THE_EYE_OF_AMAZON = 3000002;
    public static final int FOCUS = 3001003;
    public static final int ARROW_BLOW = 3001004;
    public static final int DOUBLE_SHOT = 3001005;
    // HUNTER
    public static final int BOW_MASTERY = 3100000;
    public static final int FINAL_ATTACK_BOW = 3100001;
    public static final int ENHANCED_BASICS_BOW = 3100006;
    public static final int BOW_BOOSTER = 3101002;
    public static final int POWER_KNOCKBACK_BM = 3101003;
    public static final int SOUL_ARROW_BM = 3101004;
    public static final int ARROW_BOMB = 3101005;
    // RANGER
    public static final int THRUST_BM = 3110000;
    public static final int MORTAL_BLOW_BM = 3110001;
    public static final int EVASION_BOOST_BM = 3110007;
    public static final int PUPPET_BM = 3111002;
    public static final int INFERNO = 3111003;
    public static final int ARROW_RAIN = 3111004;
    public static final int SILVER_HAWK = 3111005;
    public static final int STRAFE_BM = 3111006;
    // BOWMASTER
    public static final int BOW_EXPERT = 3120005;
    public static final int VENGEANCE = 3120010;
    public static final int MARKSMANSHIP_BM = 3120011;
    public static final int MAPLE_WARRIOR_BM = 3121000;
    public static final int SHARP_EYES_BM = 3121002;
    public static final int DRAGONS_BREATH_BM = 3121003;
    public static final int HURRICANE = 3121004;
    public static final int PHOENIX = 3121006;
    public static final int HAMSTRING = 3121007;
    public static final int CONCENTRATE = 3121008;
    public static final int HEROS_WILL_BM = 3121009;
    // CROSSBOWMAN
    public static final int CROSSBOW_MASTERY = 3200000;
    public static final int FINAL_ATTACK_MM = 3200001;
    public static final int ENHANCED_BASICS_MM = 3200006;
    public static final int CROSSBOW_BOOSTER = 3201002;
    public static final int POWER_KNOCKBACK_MM = 3201003;
    public static final int SOUL_ARROW_MM = 3201004;
    public static final int IRON_ARROW = 3201005;
    // SNIPER
    public static final int THRUST_MM = 3210000;
    public static final int MORTAL_BLOW_MM = 3210001;
    public static final int EVASION_BOOST_MM = 3210007;
    public static final int PUPPET_MM = 3211002;
    public static final int BLIZZARD = 3211003;
    public static final int ARROW_ERUPTION = 3211004;
    public static final int GOLDEN_EAGLE = 3211005;
    public static final int STRAFE_MM = 3211006;
    // MARKSMAN
    public static final int MARKSMAN_BOOST = 3220004;
    public static final int MARKSMANSHIP_MM = 3220009;
    public static final int ULTIMATE_STRAFE = 3220010;
    public static final int MAPLE_WARRIOR_MM = 3221000;
    public static final int PIERCING_ARROW = 3221001;
    public static final int SHARP_EYES_MM = 3221002;
    public static final int DRAGONS_BREATH_MM = 3221003;
    public static final int FROSTPREY = 3221005;
    public static final int BLIND = 3221006;
    public static final int SNIPE = 3221007;
    public static final int HEROS_WILL_MM = 3221008;
    private static final Logger log = LogManager.getLogger(JobHandler.class);

    public static void handleAttack(User user, Attack attack, SkillInfo si) {
        final int skillId = attack.skillId;
        final int slv = attack.slv;
        switch (skillId) {
        }
    }

    public static void handleSkill(User user, Skill skill, SkillInfo si) {
        log.error("Unhandled skill {}", skill.skillId);
    }
}