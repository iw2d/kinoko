package kinoko.world.job.cygnus;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

public final class DawnWarrior extends SkillProcessor {
    // DAWN_WARRIOR_1
    public static final int HP_BOOST = 11000005;
    public static final int IRON_BODY = 11001001;
    public static final int POWER_STRIKE = 11001002;
    public static final int SLASH_BLAST = 11001003;
    public static final int SOUL = 11001004;
    // DAWN_WARRIOR_2
    public static final int SWORD_MASTERY = 11100000;
    public static final int SWORD_BOOSTER = 11101001;
    public static final int FINAL_ATTACK = 11101002;
    public static final int RAGE = 11101003;
    public static final int SOUL_BLADE = 11101004;
    public static final int SOUL_RUSH = 11101005;
    // DAWN_WARRIOR_3
    public static final int MP_RECOVERY_RATE_ENHANCEMENT = 11110000;
    public static final int ADVANCED_COMBO = 11110005;
    public static final int COMBO_ATTACK = 11111001;
    public static final int PANIC = 11111002;
    public static final int COMA = 11111003;
    public static final int BRANDISH = 11111004;
    public static final int SOUL_DRIVER = 11111006;
    public static final int SOUL_CHARGE = 11111007;

    public static void handleAttack(User user, Mob mob, Attack attack, int delay) {
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            case FINAL_ATTACK:
                user.setTemporaryStat(CharacterTemporaryStat.SoulMasterFinal, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
            case SOUL_CHARGE:
                user.setTemporaryStat(CharacterTemporaryStat.WeaponCharge, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}