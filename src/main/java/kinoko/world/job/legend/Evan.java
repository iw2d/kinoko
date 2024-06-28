package kinoko.world.job.legend;

import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.field.affectedarea.AffectedAreaType;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class Evan extends SkillProcessor {
    // EVAN_BEGINNER
    public static final int BLESSING_OF_THE_FAIRY = 20010012;
    public static final int THREE_SNAILS = 20011000;
    public static final int RECOVER = 20011001;
    public static final int NIMBLE_FEET = 20011002;
    public static final int LEGENDARY_SPIRIT = 20011003;
    public static final int MONSTER_RIDER = 20011004;
    public static final int HEROS_ECHO = 20011005;
    public static final int MAKER = 20011007;
    public static final int FOLLOW_THE_LEAD = 20011024;
    // EVAN_1
    public static final int DRAGON_SOUL = 22000000;
    public static final int MAGIC_MISSILE = 22001001;
    // EVAN_2
    public static final int FIRE_CIRCLE = 22101000;
    public static final int TELEPORT = 22101001;
    // EVAN_3
    public static final int LIGHTNING_BOLT = 22111000;
    public static final int MAGIC_GUARD = 22111001;
    // EVAN_4
    public static final int SPELL_MASTERY = 22120002;
    public static final int ICE_BREATH = 22121000;
    public static final int ELEMENTAL_RESET = 22121001;
    // EVAN_5
    public static final int MAGIC_FLARE = 22131000;
    public static final int MAGIC_SHIELD = 22131001;
    // EVAN_6
    public static final int CRITICAL_MAGIC = 22140000;
    public static final int DRAGON_THRUST = 22141001;
    public static final int MAGIC_BOOSTER = 22141002;
    public static final int SLOW = 22141003;
    // EVAN_7
    public static final int MAGIC_AMPLIFICATION = 22150000;
    public static final int FIRE_BREATH = 22151001;
    public static final int KILLER_WINGS = 22151002;
    public static final int MAGIC_RESISTANCE = 22151003;
    // EVAN_8
    public static final int DRAGON_FURY = 22160000;
    public static final int EARTHQUAKE = 22161001;
    public static final int PHANTOM_IMPRINT = 22161002;
    public static final int RECOVERY_AURA = 22161003;
    // EVAN_9
    public static final int MAGIC_MASTERY = 22170001;
    public static final int MAPLE_WARRIOR_EVAN = 22171000;
    public static final int ILLUSION = 22171002;
    public static final int FLAME_WHEEL = 22171003;
    public static final int HEROS_WILL_EVAN = 22171004;
    // EVAN_10
    public static final int BLESSING_OF_THE_ONYX = 22181000;
    public static final int BLAZE = 22181001;
    public static final int DARK_FOG = 22181002;
    public static final int SOUL_STONE = 22181003;

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case ICE_BREATH:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Freeze, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
            case FIRE_BREATH:
            case BLAZE:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
            case KILLER_WINGS:
                attack.forEachMob(field, (mob) -> {
                    if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.GuidedBullet)) {
                        user.resetTemporaryStat(Set.of(CharacterTemporaryStat.GuidedBullet));
                    }
                    user.setTemporaryStat(CharacterTemporaryStat.GuidedBullet, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.GuidedBullet, 1, skillId, mob.getId()));
                });
                break;
            case PHANTOM_IMPRINT:
                attack.forEachMob(field, (mob) -> {
                    mob.setTemporaryStat(MobTemporaryStat.Weakness, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                });
                break;
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skill.skillId) {
            case MAGIC_SHIELD:
                user.setTemporaryStat(CharacterTemporaryStat.MagicShield, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case SLOW:
                user.setTemporaryStat(CharacterTemporaryStat.EvanSlow, TemporaryStatOption.of(slv, skillId, si.getDuration(slv)));
                return;
            case MAGIC_RESISTANCE:
                user.setTemporaryStat(CharacterTemporaryStat.MagicResistance, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case RECOVERY_AURA:
                // Recover x% hp over duration, divided by a 60 tick interval, TODO : figure out actual formula
                final AffectedArea affectedArea = AffectedArea.from(AffectedAreaType.BlessedMist, user, si, slv, 0, 60, skill.positionX, skill.positionY);
                field.getAffectedAreaPool().addAffectedArea(affectedArea);
                return;
            case BLESSING_OF_THE_ONYX:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.PDD, TemporaryStatOption.of(si.getValue(SkillStat.pdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.MDD, TemporaryStatOption.of(si.getValue(SkillStat.mdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.MAD, TemporaryStatOption.of(si.getValue(SkillStat.mad, slv), skillId, si.getDuration(slv))
                ));
                return;
            case SOUL_STONE:
                user.setTemporaryStat(CharacterTemporaryStat.SoulStone, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }

    public static void handleDragonFuryEffect(User user) {
        final int skillId = Evan.DRAGON_FURY;
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return;
        }
        final Effect berserkEffect = Effect.skillUseEnable(skillId, slv, user.getLevel(), isDragonFury(user));
        user.write(UserLocal.effect(berserkEffect));
        user.getField().broadcastPacket(UserRemote.effect(user, berserkEffect), user);
    }

    public static boolean isDragonFury(User user) {
        final int skillId = Evan.DRAGON_FURY;
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return false;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for dragon fury skill ID : {}", skillId);
            return false;
        }
        final SkillInfo si = skillInfoResult.get();
        final int rangeMin = si.getValue(SkillStat.x, slv);
        final int rangeMax = si.getValue(SkillStat.y, slv);
        final int percentage = (int) ((double) user.getMp() / user.getMaxMp() * 100);
        return percentage >= rangeMin && percentage <= rangeMax;
    }
}