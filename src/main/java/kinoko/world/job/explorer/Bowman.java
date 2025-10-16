package kinoko.world.job.explorer;

import kinoko.meta.SkillId;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAssistType;
import kinoko.world.field.summoned.SummonedMoveAbility;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.Map;

public final class Bowman extends SkillProcessor {
    // ARCHER
    public static final int CRITICAL_SHOT = 3000001;
    public static final int THE_EYE_OF_AMAZON = 3000002;
    public static final int FOCUS = 3001003;
    public static final int ARROW_BLOW = 3001004;
    public static final int DOUBLE_SHOT = 3001005;
    // HUNTER
    public static final int BOW_MASTERY = 3100000;
    public static final int FINAL_ATTACK_BOW = 3100001;
    public static final int ENHANCED_BASICS_BM = 3100006;
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

    public static void handleAttack(User user, Mob mob, Attack attack, int delay) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final SkillId skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId.getId()) {
            case ARROW_BOMB:
            case SILVER_HAWK:
            case GOLDEN_EAGLE:
            case PHOENIX: // knock-down?
                if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                    mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                break;
            case INFERNO:
                mob.setBurnedInfo(BurnedInfo.from(user, si, slv, mob), delay);
                break;
            case VENGEANCE:
                if (!mob.isBoss()) {
                    mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                break;
            case BLIZZARD:
                if (!mob.isBoss()) {
                    mob.setTemporaryStat(MobTemporaryStat.Freeze, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                break;
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final SkillId skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId.getId()) {
            case SILVER_HAWK:
            case GOLDEN_EAGLE:
            case PHOENIX:
            case FROSTPREY:
                final Summoned birb = Summoned.from(si, slv, SummonedMoveAbility.FLY, SummonedAssistType.ATTACK);
                birb.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                user.addSummoned(birb);
                return;
            case CONCENTRATE:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Concentration, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EPAD, TemporaryStatOption.of(si.getValue(SkillStat.epad, slv), skillId, si.getDuration(slv))
                ));
                return;
            case SHARP_EYES_BM:
            case SHARP_EYES_MM:
                final int sharpEyes = (si.getValue(SkillStat.x, slv) << 8) + si.getValue(SkillStat.criticaldamageMax, slv); // (cr << 8) + cd
                user.setTemporaryStat(CharacterTemporaryStat.SharpEyes, TemporaryStatOption.of(sharpEyes, skillId, si.getDuration(slv)));
                return;
            case HAMSTRING:
                user.setTemporaryStat(CharacterTemporaryStat.HamString, TemporaryStatOption.of(slv, skillId, si.getDuration(slv)));
                return;
            case BLIND:
                user.setTemporaryStat(CharacterTemporaryStat.Blind, TemporaryStatOption.of(slv, skillId, si.getDuration(slv)));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}