package kinoko.world.job.cygnus;

import kinoko.server.packet.InPacket;
import kinoko.world.job.JobHandler;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BlazeWizard {
    // BLAZE_WIZARD_1
    public static final int MP_BOOST = 12000005;
    public static final int MAGIC_GUARD = 12001001;
    public static final int MAGIC_ARMOR = 12001002;
    public static final int MAGIC_CLAW = 12001003;
    public static final int FLAME = 12001004;
    // BLAZE_WIZARD_2
    public static final int SPELL_MASTERY = 12100007;
    public static final int MEDITATION = 12101000;
    public static final int SLOW = 12101001;
    public static final int FIRE_ARROW = 12101002;
    public static final int TELEPORT = 12101003;
    public static final int SPELL_BOOSTER = 12101004;
    public static final int ELEMENTAL_RESET = 12101005;
    public static final int FIRE_PILLAR = 12101006;
    // BLAZE_WIZARD_3
    public static final int ELEMENTAL_RESISTANCE = 12110000;
    public static final int ELEMENT_AMPLIFICATION = 12110001;
    public static final int SEAL = 12111002;
    public static final int METEOR_SHOWER = 12111003;
    public static final int IFRIT = 12111004;
    public static final int FLAME_GEAR = 12111005;
    public static final int FIRE_STRIKE = 12111006;
    private static final Logger log = LogManager.getLogger(JobHandler.class);

    public static void handleAttack(User user, Attack attack) {
        final int skillId = attack.skillId;
        final int slv = attack.slv;
        switch (skillId) {
        }
    }

    public static void handleSkill(User user, Skill skill, InPacket inPacket) {
        log.error("Unhandled skill {}", skill.skillId);
    }
}
