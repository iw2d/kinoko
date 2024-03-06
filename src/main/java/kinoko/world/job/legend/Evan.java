package kinoko.world.job.legend;

import kinoko.provider.skill.SkillInfo;
import kinoko.world.job.JobHandler;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Evan {
    // EVAN_BEGINNER
    public static final int BLESSING_OF_THE_FAIRY = 20010012;
    public static final int THREE_SNAILS = 20011000;
    public static final int RECOVER = 20011001;
    public static final int NIMBLE_FEET = 20011002;
    public static final int LEGENDARY_SPIRIT = 20011003;
    public static final int MONSTER_RIDER = 20011004;
    public static final int HEROS_ECHO = 20011005;
    public static final int MAKER = 20011007;
    // EVAN_1
    public static final int DRAGON_SOUL = 22000000;
    public static final int MAGIC_MISSILE = 22001001;
    // EVAN_2
    public static final int FIRE_CIRCLE = 22101000;
    public static final int TELEPORT = 22101001;
    // EVAN_3
    public static final int LIGHTNING_BOLT = 22111000;
    public static final int MAGIC_GUARD = 22111001;
    // EVAN_4
    public static final int SPELL_MASTERY = 22120002;
    public static final int ICE_BREATH = 22121000;
    public static final int ELEMENTAL_RESET = 22121001;
    // EVAN_5
    public static final int MAGIC_FLARE = 22131000;
    public static final int MAGIC_SHIELD = 22131001;
    // EVAN_6
    public static final int CRITICAL_MAGIC = 22140000;
    public static final int DRAGON_THRUST = 22141001;
    public static final int MAGIC_BOOSTER = 22141002;
    public static final int SLOW = 22141003;
    // EVAN_7
    public static final int MAGIC_AMPLIFICATION = 22150000;
    public static final int FIRE_BREATH = 22151001;
    public static final int KILLER_WINGS = 22151002;
    public static final int MAGIC_RESISTANCE = 22151003;
    // EVAN_8
    public static final int DRAGON_FURY = 22160000;
    public static final int EARTHQUAKE = 22161001;
    public static final int PHANTOM_IMPRINT = 22161002;
    public static final int RECOVERY_AURA = 22161003;
    // EVAN_9
    public static final int MAGIC_MASTERY = 22170001;
    public static final int MAPLE_WARRIOR_EVAN = 22171000;
    public static final int ILLUSION = 22171002;
    public static final int FLAME_WHEEL = 22171003;
    public static final int HEROS_WILL_EVAN = 22171004;
    // EVAN_10
    public static final int BLESSING_OF_THE_ONYX = 22181000;
    public static final int BLAZE = 22181001;
    public static final int DARK_FOG = 22181002;
    public static final int SOUL_STONE = 22181003;
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