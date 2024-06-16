package kinoko.world.job.cygnus;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
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

public final class BlazeWizard extends SkillDispatcher {
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

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case METEOR_SHOWER:
                attack.forEachMob(field, (mob) -> {
                    mob.setBurnedInfo(BurnedInfo.from(user, si, slv, mob));
                });
                break;
            case FLAME_GEAR:
                final AffectedArea affectedArea = AffectedArea.userSkill(user, si, slv, 0, attack.userX, attack.userY);
                user.getField().getAffectedAreaPool().addAffectedArea(affectedArea);
                break;
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            case MAGIC_GUARD:
                user.setTemporaryStat(CharacterTemporaryStat.MagicGuard, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case MAGIC_ARMOR:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.PDD, TemporaryStatOption.of(si.getValue(SkillStat.pdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.MDD, TemporaryStatOption.of(si.getValue(SkillStat.mdd, slv), skillId, si.getDuration(slv))
                ));
                return;
            case FLAME:
            case IFRIT:
                final Summoned summoned = Summoned.from(si, slv, SummonedMoveAbility.WALK, SummonedAssistType.ATTACK);
                summoned.setPosition(field, skill.positionX, skill.positionY);
                user.addSummoned(summoned);
                return;
            case MEDITATION:
                user.setTemporaryStat(CharacterTemporaryStat.MAD, TemporaryStatOption.of(si.getValue(SkillStat.mad, slv), skillId, si.getDuration(slv)));
                return;
            case SLOW:
                skill.forEachAffectedMob(field, (mob) -> {
                    if (!mob.isSlowUsed()) {
                        mob.setTemporaryStat(MobTemporaryStat.Speed, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                        mob.setSlowUsed(true); // cannot be used on the same monsters more than twice in a row
                    }
                });
                return;
            case ELEMENTAL_RESET:
                user.setTemporaryStat(CharacterTemporaryStat.ElementalReset, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case SEAL:
                skill.forEachAffectedMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Seal, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}
