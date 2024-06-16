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
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.Map;

public final class ThunderBreaker extends SkillProcessor {
    // THUNDER_BREAKER_1
    public static final int QUICK_MOTION = 15000000;
    public static final int STRAIGHT = 15001001;
    public static final int SOMERSAULT_KICK = 15001002;
    public static final int DASH = 15001003;
    public static final int LIGHTNING = 15001004;
    // THUNDER_BREAKER_2
    public static final int KNUCKLE_MASTERY = 15100001;
    public static final int ENERGY_CHARGE = 15100004;
    public static final int HP_BOOST = 15100007;
    public static final int KNUCKLE_BOOSTER = 15101002;
    public static final int CORKSCREW_BLOW = 15101003;
    public static final int ENERGY_BLAST = 15101005;
    public static final int LIGHTNING_CHARGE = 15101006;
    // THUNDER_BREAKER_3
    public static final int CRITICAL_PUNCH = 15110000;
    public static final int ENERGY_DRAIN = 15111001;
    public static final int TRANSFORMATION = 15111002;
    public static final int SHOCKWAVE = 15111003;
    public static final int BARRAGE = 15111004;
    public static final int SPEED_INFUSION = 15111005;
    public static final int SPARK = 15111006;
    public static final int SHARK_WAVE = 15111007;

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            case DASH:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Dash_Speed, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.Dash_Speed, si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Dash_Jump, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.Dash_Jump, si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                ));
                return;
            case LIGHTNING:
                final Summoned summoned = Summoned.from(si, slv, SummonedMoveAbility.WALK, SummonedAssistType.ATTACK);
                summoned.setPosition(field, skill.positionX, skill.positionY);
                user.addSummoned(summoned);
                return;
            case LIGHTNING_CHARGE:
                user.setTemporaryStat(CharacterTemporaryStat.WeaponCharge, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
            case TRANSFORMATION:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Morph, TemporaryStatOption.of(si.getValue(SkillStat.morph, slv) + user.getGender() * 100, skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EPAD, TemporaryStatOption.of(si.getValue(SkillStat.epad, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EPDD, TemporaryStatOption.of(si.getValue(SkillStat.epdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EMDD, TemporaryStatOption.of(si.getValue(SkillStat.emdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Jump, TemporaryStatOption.of(si.getValue(SkillStat.jump, slv), skillId, si.getDuration(slv))
                ));
                return;
            case SPEED_INFUSION:
                user.setTemporaryStat(CharacterTemporaryStat.PartyBooster, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.PartyBooster, si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case SPARK:
                user.setTemporaryStat(CharacterTemporaryStat.Spark, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}
