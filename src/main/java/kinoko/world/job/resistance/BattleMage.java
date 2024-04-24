package kinoko.world.job.resistance;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.world.job.JobHandler;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BattleMage {
    // BATTLE_MAGE_1
    public static final int TRIPLE_BLOW = 32001000;
    public static final int THE_FINISHER = 32001001;
    public static final int TELEPORT = 32001002;
    public static final int DARK_AURA = 32001003;
    public static final int THE_FINISHER_STANDALONE = 32001007;
    public static final int THE_FINISHER_TRIPLE_BLOW = 32001008;
    public static final int THE_FINISHER_QUAD_BLOW = 32001009;
    public static final int THE_FINISHER_QUINTUPLE_BLOW = 32001010;
    public static final int THE_FINISHER_FINISHING_BLOW = 32001011;
    // BATTLE_MAGE_2
    public static final int STAFF_MASTERY = 32100006;
    public static final int QUAD_BLOW = 32101000;
    public static final int DARK_CHAIN = 32101001;
    public static final int BLUE_AURA = 32101002;
    public static final int YELLOW_AURA = 32101003;
    public static final int BLOOD_DRAIN = 32101004;
    public static final int STAFF_BOOST = 32101005;
    // BATTLE_MAGE_3
    public static final int ADVANCED_BLUE_AURA = 32110000;
    public static final int BATTLE_MASTERY = 32110001;
    public static final int BODY_BOOST_DARK_AURA = 32110007;
    public static final int BODY_BOOST_YELLOW_AURA = 32110008;
    public static final int BODY_BOOST_BLUE_AURA = 32110009;
    public static final int QUINTUPLE_BLOW = 32111002;
    public static final int DARK_SHOCK = 32111003;
    public static final int CONVERSION = 32111004;
    public static final int BODY_BOOST = 32111005;
    public static final int SUMMON_REAPER_BUFF = 32111006;
    public static final int TELEPORT_MASTERY = 32111010;
    public static final int ADVANCED_DARK_CHAIN = 32111011;
    // BATTLE_MAGE_4
    public static final int ADVANCED_DARK_AURA = 32120000;
    public static final int ADVANCED_YELLOW_AURA = 32120001;
    public static final int ENERGIZE = 32120009;
    public static final int FINISHING_BLOW = 32121002;
    public static final int TWISTER_SPIN = 32121003;
    public static final int DARK_GENESIS = 32121004;
    public static final int STANCE = 32121005;
    public static final int PARTY_SHIELD = 32121006;
    public static final int MAPLE_WARRIOR_BAM = 32121007;
    public static final int HEROS_WILL_BAM = 32121008;
    private static final Logger log = LogManager.getLogger(JobHandler.class);

    public static void handleAttack(User user, Attack attack) {
        final int skillId = attack.skillId;
        final int slv = attack.slv;
        switch (skillId) {
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        switch (skillId) {
            case BLUE_AURA:
            case ADVANCED_BLUE_AURA:
                user.setTemporaryStat(CharacterTemporaryStat.BlueAura, TemporaryStatOption.of(slv, skillId, si.getDuration(slv)));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}
