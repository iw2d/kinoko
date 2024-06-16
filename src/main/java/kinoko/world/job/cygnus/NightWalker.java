package kinoko.world.job.cygnus;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.affectedarea.AffectedArea;
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

public final class NightWalker extends SkillDispatcher {
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

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case DISORDER:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss()) {
                        mob.setTemporaryStat(Map.of(
                                MobTemporaryStat.PAD, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                                MobTemporaryStat.PDR, MobStatOption.of(si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                        ));
                    }
                });
                break;
            case POISON_BOMB:
                final AffectedArea affectedArea = AffectedArea.userSkill(user, si, slv, 0, attack.grenadeX, attack.grenadeY);
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
            case DARK_SIGHT:
                if (slv == si.getMaxLevel()) {
                    user.setTemporaryStat(CharacterTemporaryStat.DarkSight, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                } else {
                    user.setTemporaryStat(Map.of(
                            CharacterTemporaryStat.DarkSight, TemporaryStatOption.of(1, skillId, si.getDuration(slv)),
                            CharacterTemporaryStat.Slow, TemporaryStatOption.of(100 - si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                    ));
                }
                return;
            case DARKNESS:
                final Summoned summoned = Summoned.from(si, slv, SummonedMoveAbility.WALK, SummonedAssistType.ATTACK);
                summoned.setPosition(field, skill.positionX, skill.positionY);
                user.addSummoned(summoned);
                return;
            case HASTE:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Jump, TemporaryStatOption.of(si.getValue(SkillStat.jump, slv), skillId, si.getDuration(slv))
                ));
                return;
            case SHADOW_PARTNER:
                user.setTemporaryStat(CharacterTemporaryStat.ShadowPartner, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case SHADOW_WEB:
                skill.forEachAffectedMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Web, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}