package kinoko.world.job.cygnus;

import kinoko.world.field.mob.Mob;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;

public final class NightWalker extends SkillProcessor {
    // NIGHT_WALKER_1
    public static final int NIMBLE_BODY = 14000000;
    public static final int KEEN_EYES = 14000001;
    public static final int DISORDER = 14001002;
    public static final int DARK_SIGHT = 14001003;
    public static final int LUCKY_SEVEN = 14001004;
    public static final int DARKNESS = 14001005;
    // NIGHT_WALKER_2
    public static final int CLAW_MASTERY = 14100000;
    public static final int CRITICAL_THROW = 14100001;
    public static final int VANISH = 14100005;
    public static final int CLAW_BOOSTER = 14101002;
    public static final int HASTE = 14101003;
    public static final int FLASH_JUMP = 14101004;
    public static final int VAMPIRE = 14101006;
    // NIGHT_WALKER_3
    public static final int ALCHEMIST = 14110003;
    public static final int VENOM = 14110004;
    public static final int SHADOW_PARTNER = 14111000;
    public static final int SHADOW_WEB = 14111001;
    public static final int AVENGER = 14111002;
    public static final int TRIPLE_THROW = 14111005;
    public static final int POISON_BOMB = 14111006;

    public static void handleAttack(User user, Mob mob, Attack attack, int delay) {
    }

    public static void handleSkill(User user, Skill skill) {
        log.error("Unhandled skill {}", skill.skillId);
    }
}