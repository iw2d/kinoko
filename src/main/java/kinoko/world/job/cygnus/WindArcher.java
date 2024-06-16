package kinoko.world.job.cygnus;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.world.field.Field;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAssistType;
import kinoko.world.field.summoned.SummonedMoveAbility;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillDispatcher;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.Map;

public final class WindArcher extends SkillDispatcher {
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

        final Field field = user.getField();
        switch (skillId) {
            case FOCUS:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.ACC, TemporaryStatOption.of(si.getValue(SkillStat.acc, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EVA, TemporaryStatOption.of(si.getValue(SkillStat.eva, slv), skillId, si.getDuration(slv))
                ));
                return;
            case STORM:
                final Summoned summoned = Summoned.from(si, slv, SummonedMoveAbility.WALK, SummonedAssistType.ATTACK);
                summoned.setPosition(field, skill.positionX, skill.positionY);
                user.addSummoned(summoned);
                return;
            case SOUL_ARROW:
                user.setTemporaryStat(CharacterTemporaryStat.SoulArrow, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
            case WIND_WALK:
                if (slv == si.getMaxLevel()) {
                    user.setTemporaryStat(CharacterTemporaryStat.WindWalk, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                } else {
                    user.setTemporaryStat(Map.of(
                            CharacterTemporaryStat.WindWalk, TemporaryStatOption.of(1, skillId, si.getDuration(slv)),
                            CharacterTemporaryStat.Slow, TemporaryStatOption.of(100 - si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                    ));
                }
                return;
            case PUPPET:
                final Summoned puppet = Summoned.from(si, slv, SummonedMoveAbility.STOP, SummonedAssistType.NONE);
                puppet.setHp(si.getValue(SkillStat.x, slv));
                puppet.setPosition(field, skill.positionX, skill.positionY);
                user.addSummoned(puppet);
                return;
            case EAGLE_EYE:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Morph, TemporaryStatOption.of(si.getValue(SkillStat.morph, slv) + user.getGender() * 100, skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EPAD, TemporaryStatOption.of(si.getValue(SkillStat.epad, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EPDD, TemporaryStatOption.of(si.getValue(SkillStat.epdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EMDD, TemporaryStatOption.of(si.getValue(SkillStat.emdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Jump, TemporaryStatOption.of(si.getValue(SkillStat.jump, slv), skillId, si.getDuration(slv))
                ));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}
