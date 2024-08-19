package kinoko.world.job.legend;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.Map;

public final class Aran extends SkillProcessor {
    // ARAN_BEGINNER
    public static final int BLESSING_OF_THE_FAIRY = 20000012;
    public static final int FOLLOW_THE_LEAD = 20000024;
    public static final int THREE_SNAILS = 20001000;
    public static final int RECOVERY = 20001001;
    public static final int AGILE_BODY = 20001002;
    public static final int LEGENDARY_SPIRIT = 20001003;
    public static final int MONSTER_RIDER = 20001004;
    public static final int ECHO_OF_HERO = 20001005;
    public static final int MAKER = 20001007;
    public static final int SOARING = 20001026;
    // ARAN_1
    public static final int COMBO_ABILITY = 21000000;
    public static final int DOUBLE_SWING = 21000002;
    public static final int COMBAT_STEP = 21001001;
    public static final int POLEARM_BOOSTER = 21001003;
    // ARAN_2
    public static final int POLEARM_MASTERY = 21100000;
    public static final int TRIPLE_SWING = 21100001;
    public static final int FINAL_CHARGE = 21100002;
    public static final int COMBO_SMASH = 21100004;
    public static final int COMBO_DRAIN = 21100005;
    public static final int BODY_PRESSURE = 21101003;
    // ARAN_3
    public static final int COMBO_CRITICAL = 21110000;
    public static final int FULL_SWING = 21110002;
    public static final int FINAL_TOSS = 21110003;
    public static final int COMBO_FENRIR = 21110004;
    public static final int ROLLING_SPIN = 21110006;
    public static final int FULL_SWING_DOUBLE_SWING = 21110007;
    public static final int FULL_SWING_TRIPLE_SWING = 21110008;
    public static final int SMART_KNOCKBACK = 21111001;
    public static final int SNOW_CHARGE = 21111005;
    // ARAN_4
    public static final int HIGH_MASTERY = 21120001;
    public static final int OVER_SWING = 21120002;
    public static final int HIGH_DEFENSE = 21120004;
    public static final int FINAL_BLOW = 21120005;
    public static final int COMBO_TEMPEST = 21120006;
    public static final int COMBO_BARRIER = 21120007;
    public static final int OVER_SWING_DOUBLE_SWING = 21120009;
    public static final int OVER_SWING_TRIPLE_SWING = 21120010;
    public static final int MAPLE_WARRIOR_ARAN = 21121000;
    public static final int FREEZE_STANDING = 21121003;
    public static final int HEROS_WILL_ARAN = 21121008;

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case BODY_PRESSURE:
                attack.forEachMob(field, (mob, delay) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(Map.of(
                                MobTemporaryStat.Stun, MobStatOption.of(1, skillId, 5000),
                                MobTemporaryStat.BodyPressure, MobStatOption.of(1, skillId, 5000)
                        ), delay); // x = 5 seconds
                    }
                });
                break;
            case FINAL_TOSS:
                attack.forEachMob(field, (mob, delay) -> {
                    if (!mob.isBoss() && !mob.getMobStat().hasOption(MobTemporaryStat.RiseByToss)) {
                        mob.setTemporaryStat(MobTemporaryStat.RiseByToss, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, 1000), delay);
                    }
                });
                break;
            case COMBO_TEMPEST:
                attack.forEachMob(field, (mob, delay) -> {
                    if (!mob.isBoss()) {
                        mob.setTemporaryStat(MobTemporaryStat.Freeze, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
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
            case COMBO_DRAIN:
                user.setTemporaryStat(CharacterTemporaryStat.ComboDrain, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case BODY_PRESSURE:
                user.setTemporaryStat(CharacterTemporaryStat.BodyPressure, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
            case SMART_KNOCKBACK:
                user.setTemporaryStat(CharacterTemporaryStat.SmartKnockback, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case SNOW_CHARGE:
                user.setTemporaryStat(CharacterTemporaryStat.WeaponCharge, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
            case COMBO_BARRIER:
                user.setTemporaryStat(CharacterTemporaryStat.ComboBarrier, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case FREEZE_STANDING:
                user.setTemporaryStat(CharacterTemporaryStat.Stance, TemporaryStatOption.of(si.getValue(SkillStat.prop, slv), skillId, si.getDuration(slv)));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}