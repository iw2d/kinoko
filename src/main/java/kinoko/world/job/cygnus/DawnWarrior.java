package kinoko.world.job.cygnus;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAssistType;
import kinoko.world.field.summoned.SummonedMoveAbility;
import kinoko.world.job.explorer.Warrior;
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

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case PANIC:
                Warrior.resetComboCounter(user);
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Blind, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                    }
                });
                break;
            case COMA:
                Warrior.resetComboCounter(user);
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            case IRON_BODY:
                user.setTemporaryStat(CharacterTemporaryStat.PDD, TemporaryStatOption.of(si.getValue(SkillStat.pdd, slv), skillId, si.getDuration(slv)));
                return;
            case SOUL:
                final Summoned summoned = Summoned.from(si, slv, SummonedMoveAbility.WALK, SummonedAssistType.ATTACK);
                summoned.setPosition(field, skill.positionX, skill.positionY);
                user.addSummoned(summoned);
                return;
            case RAGE:
                user.setTemporaryStat(CharacterTemporaryStat.PAD, TemporaryStatOption.of(si.getValue(SkillStat.pad, slv), skillId, si.getDuration(slv)));
                return;
            case COMBO_ATTACK:
                user.setTemporaryStat(CharacterTemporaryStat.ComboCounter, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}