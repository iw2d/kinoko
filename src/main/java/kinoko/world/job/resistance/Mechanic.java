package kinoko.world.job.resistance;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.OpenGate;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.DiceInfo;
import kinoko.world.user.stat.TemporaryStatOption;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

public final class Mechanic extends SkillProcessor {
    // MECHANIC_1
    public static final int FLAME_LAUNCHER = 35001001;
    public static final int MECH_PROTOTYPE = 35001002;
    public static final int ME_07_DRILLHANDS = 35001003;
    public static final int GATLING_GUN = 35001004;
    // MECHANIC_2
    public static final int MECHANIC_MASTERY = 35100000;
    public static final int HEAVY_WEAPON_MASTERY = 35100008;
    public static final int ATOMIC_HAMMER = 35101003;
    public static final int ROCKET_BOOSTER = 35101004;
    public static final int OPEN_PORTAL_GX_9 = 35101005;
    public static final int MECHANIC_RAGE = 35101006;
    public static final int PERFECT_ARMOR = 35101007;
    public static final int ENHANCED_FLAME_LAUNCHER = 35101009;
    public static final int ENHANCED_GATLING_GUN = 35101010;
    // MECHANIC_3
    public static final int METAL_FIST_MASTERY = 35110014;
    public static final int SATELLITE = 35111001;
    public static final int ROCK_N_SHOCK = 35111002;
    public static final int MECH_SIEGE_MODE = 35111004;
    public static final int ACCELERATION_BOT_EX_7 = 35111005;
    public static final int SATELLITE_2 = 35111009;
    public static final int SATELLITE_3 = 35111010;
    public static final int HEALING_ROBOT_H_LX = 35111011;
    public static final int ROLL_OF_THE_DICE = 35111013;
    public static final int PUNCH_LAUNCHER = 35111015;
    // MECHANIC_4
    public static final int EXTREME_MECH = 35120000;
    public static final int ROBOT_MASTERY = 35120001;
    public static final int GIANT_ROBOT_SG_88 = 35121003;
    public static final int MECH_MISSILE_TANK = 35121005;
    public static final int SATELLITE_SAFETY = 35121006;
    public static final int MAPLE_WARRIOR_MECH = 35121007;
    public static final int HEROS_WILL_MECH = 35121008;
    public static final int BOTS_N_TOTS = 35121009;
    public static final int AMPLIFIER_ROBOT_AF_11 = 35121010;
    public static final int BOTS_N_TOTS_DUMMY = 35121011;
    public static final int LASER_BLAST = 35121012;
    public static final int MECH_SIEGE_MODE_2 = 35121013;

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case ATOMIC_HAMMER:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
            case PUNCH_LAUNCHER:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss()) {
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
        System.out.println(skillId);
        switch (skillId) {
            // MECH
            case MECH_PROTOTYPE:
                handleMech(user, skillId);
                return;
            case MECH_SIEGE_MODE:
                final int mechanicMode = user.getSecondaryStat().getOption(CharacterTemporaryStat.Mechanic).rOption;
                handleMech(user, mechanicMode == MECH_MISSILE_TANK ? MECH_SIEGE_MODE_2 : MECH_SIEGE_MODE);
                return;
            case MECH_MISSILE_TANK:
                handleMech(user, skillId);
                user.getSkillManager().setSkillSchedule(skillId, Instant.now().plus(5, ChronoUnit.SECONDS)); // - #u MP every 5 sec
                return;

            // BUFFS
            case PERFECT_ARMOR:
                if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.ManaReflection)) {
                    user.resetTemporaryStat(skillId);
                } else {
                    user.setTemporaryStat(CharacterTemporaryStat.ManaReflection, TemporaryStatOption.of(slv, skillId, 0));
                }
                return;
            case ROLL_OF_THE_DICE:
                final int roll = Util.getRandom(1, 6);
                user.write(UserLocal.effect(Effect.skillAffectedSelect(roll, skillId, slv)));
                field.broadcastPacket(UserRemote.effect(user, Effect.skillAffectedSelect(roll, skillId, slv)), user);
                if (roll != 1) {
                    final DiceInfo diceInfo = DiceInfo.from(roll, si, slv);
                    user.setTemporaryStat(CharacterTemporaryStat.Dice, TemporaryStatOption.ofDice(roll, skillId, si.getDuration(slv), diceInfo));
                }
                return;

            // SUMMONS
            case OPEN_PORTAL_GX_9:
                // Destroy existing gates
                if (user.getOpenGate() != null && user.getOpenGate().getSecondGate() != null) {
                    user.getOpenGate().destroy();
                    user.setOpenGate(null);
                }
                // Create gate
                if (user.getOpenGate() == null) {
                    final OpenGate firstGate = new OpenGate(user, true, Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS));
                    firstGate.setPosition(field, skill.positionX, skill.positionY);
                    user.setOpenGate(firstGate);
                    field.broadcastPacket(FieldPacket.openGateCreated(user, firstGate, true));
                } else {
                    final OpenGate secondGate = new OpenGate(user, false, Instant.MIN); // expire time depends on first gate
                    secondGate.setPosition(field, skill.positionX, skill.positionY);
                    user.getOpenGate().setSecondGate(secondGate);
                    field.broadcastPacket(FieldPacket.openGateCreated(user, secondGate, true));
                }
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }

    public static void handleMech(User user, int skillId) {
        final int statSkillId = user.getSkillLevel(EXTREME_MECH) > 0 ? EXTREME_MECH : MECH_PROTOTYPE;
        final int slv = user.getSkillLevel(statSkillId);
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(statSkillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for mech stat skill ID : {}", statSkillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        user.setTemporaryStat(Map.of(
                CharacterTemporaryStat.RideVehicle, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.RideVehicle, SkillConstants.MECHANIC_VEHICLE, skillId, 0),
                CharacterTemporaryStat.Mechanic, TemporaryStatOption.of(slv, skillId, 0),
                CharacterTemporaryStat.EMHP, TemporaryStatOption.of(si.getValue(SkillStat.emhp, slv), skillId, 0),
                CharacterTemporaryStat.EMMP, TemporaryStatOption.of(si.getValue(SkillStat.emmp, slv), skillId, 0),
                CharacterTemporaryStat.EPAD, TemporaryStatOption.of(si.getValue(SkillStat.epad, slv), skillId, 0),
                CharacterTemporaryStat.EPDD, TemporaryStatOption.of(si.getValue(SkillStat.epdd, slv), skillId, 0),
                CharacterTemporaryStat.EMDD, TemporaryStatOption.of(si.getValue(SkillStat.emdd, slv), skillId, 0)
        ));
    }
}