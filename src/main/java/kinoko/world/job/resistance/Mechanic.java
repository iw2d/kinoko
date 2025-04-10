package kinoko.world.job.resistance;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserPacket;
import kinoko.packet.user.UserRemote;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.ServerConfig;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.OpenGate;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.field.affectedarea.AffectedAreaType;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAssistType;
import kinoko.world.field.summoned.SummonedMoveAbility;
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
import java.util.List;
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
    public static final int BOTS_N_TOTS_SUMMON = 35121011;
    public static final int LASER_BLAST = 35121012;
    public static final int MECH_SIEGE_MODE_2 = 35121013;

    public static void handleAttack(User user, Mob mob, Attack attack, int delay) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case ATOMIC_HAMMER:
                if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                    mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                break;
            case PUNCH_LAUNCHER:
                if (!mob.isBoss()) {
                    mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                break;
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final int summonDuration = getSummonDuration(user, si.getDuration(slv));
        final Field field = user.getField();
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
                user.setSchedule(skillId, Instant.now().plus(5, ChronoUnit.SECONDS)); // - #u MP every 5 sec
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
            case SATELLITE_SAFETY:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.SafetyDamage, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, 0),
                        CharacterTemporaryStat.SafetyAbsorb, TemporaryStatOption.of(si.getValue(SkillStat.y, slv), skillId, 0)
                ));
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
                    final OpenGate firstGate = new OpenGate(user, true, Instant.now().plus(summonDuration, ChronoUnit.MILLIS));
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
            case SATELLITE:
            case SATELLITE_2:
            case SATELLITE_3:
                // Client sends different skill IDs depending on satellite count
                final Summoned satellite = Summoned.from(skillId, slv, SummonedMoveAbility.WALK, SummonedAssistType.ATTACK_EX, Instant.MAX);
                satellite.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                user.addSummoned(satellite);
                return;
            case ROCK_N_SHOCK:
                // Create summoned
                final Summoned rockAndShock = Summoned.from(skillId, slv, SummonedMoveAbility.STOP, SummonedAssistType.NONE, Instant.now().plus(summonDuration, ChronoUnit.MILLIS));
                rockAndShock.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                user.addSummoned(rockAndShock);
                // Check if triangle is complete
                final List<Summoned> rockAndShockList = user.getSummoned().getOrDefault(ROCK_N_SHOCK, List.of());
                if (rockAndShockList.size() == 3) {
                    // nTeslaCoilState = 2 - (bLeader != 0)
                    for (int i = 0; i < 3; i++) {
                        rockAndShockList.get(i).setTeslaCoilState(2 - (i == 0 ? 1 : 0));
                    }
                    field.broadcastPacket(UserPacket.userTeslaTriangle(user, rockAndShockList));
                    user.setSkillCooltime(skillId, si.getValue(SkillStat.cooltime, slv));
                }
                return;
            case ACCELERATION_BOT_EX_7:
                // Create summoned
                final Summoned accelerationBot = Summoned.from(skillId, slv, SummonedMoveAbility.STOP, SummonedAssistType.NONE, Instant.now().plus(summonDuration, ChronoUnit.MILLIS));
                accelerationBot.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                user.addSummoned(accelerationBot);
                // Initial effect
                field.getMobPool().forEach((mob) -> {
                    if (!mob.isBoss()) {
                        mob.setTemporaryStat(Map.of(
                                MobTemporaryStat.Speed, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, 0),
                                MobTemporaryStat.PDR, MobStatOption.of(si.getValue(SkillStat.y, slv), skillId, 0),
                                MobTemporaryStat.MDR, MobStatOption.of(si.getValue(SkillStat.y, slv), skillId, 0)
                        ), 0);
                    }
                });
                // Set spawn modifier
                field.getMobSpawnModifiers().put(accelerationBot.getId(), (mob) -> {
                    if (!mob.isBoss()) {
                        mob.getMobStat().getTemporaryStats().putAll(Map.of(
                                MobTemporaryStat.Speed, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, 0),
                                MobTemporaryStat.PDR, MobStatOption.of(si.getValue(SkillStat.y, slv), skillId, 0),
                                MobTemporaryStat.MDR, MobStatOption.of(si.getValue(SkillStat.y, slv), skillId, 0)
                        ));
                    }
                });
                return;
            case HEALING_ROBOT_H_LX:
                if (skill.isSummonedSkill()) {
                    // Heal user when prone near summoned
                    final int healAmount = user.getMaxHp() * si.getValue(SkillStat.hp, slv) / 100;
                    user.addHp(healAmount);
                    user.write(UserLocal.effect(Effect.incDecHpEffect(healAmount)));
                    user.getField().broadcastPacket(UserRemote.effect(user, Effect.incDecHpEffect(healAmount)), user);
                } else {
                    // Create summoned
                    final Summoned healingRobot = Summoned.from(skillId, slv, SummonedMoveAbility.STOP, SummonedAssistType.HEAL, Instant.now().plus(summonDuration, ChronoUnit.MILLIS));
                    healingRobot.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                    user.addSummoned(healingRobot);
                }
                return;
            case GIANT_ROBOT_SG_88:
                final Summoned giantRobot = Summoned.from(skillId, slv, SummonedMoveAbility.STOP, SummonedAssistType.ATTACK_MANUAL, Instant.MAX);
                giantRobot.setPosition(field, skill.positionX, skill.positionY, false); // always facing right
                user.addSummoned(giantRobot);
                return;
            case BOTS_N_TOTS:
                if (skill.isSummonedSkill()) {
                    // Create sub-summon
                    final Summoned botsAndTotsSummon = Summoned.from(BOTS_N_TOTS_SUMMON, slv, SummonedMoveAbility.WALK_RANDOM, SummonedAssistType.ATTACK, Instant.now().plus(getSummonDuration(user, 5000), ChronoUnit.MILLIS)); // 5 second base duration
                    botsAndTotsSummon.setPosition(field, skill.summoned.getX(), skill.summoned.getY(), skill.summoned.isLeft());
                    user.addSummoned(botsAndTotsSummon);
                } else {
                    // Create summoned
                    final Summoned botsAndTots = Summoned.from(skillId, slv, SummonedMoveAbility.STOP, SummonedAssistType.SUMMON, Instant.now().plus(summonDuration, ChronoUnit.MILLIS));
                    botsAndTots.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                    user.addSummoned(botsAndTots);
                }
                return;
            case AMPLIFIER_ROBOT_AF_11:
                // Create summoned
                final Summoned amplifierRobot = Summoned.from(skillId, slv, SummonedMoveAbility.STOP, SummonedAssistType.NONE, Instant.now().plus(summonDuration, ChronoUnit.MILLIS));
                amplifierRobot.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                amplifierRobot.setRect(si.getRect(slv).translate(skill.positionX, skill.positionY));
                user.addSummoned(amplifierRobot);
                // Create affected area (Ar01AreaPAD/MAD)
                final AffectedArea affectedArea = new AffectedArea(AffectedAreaType.UserSkill, user, skillId, slv, 0, 0, amplifierRobot.getRect(), si.getElemAttr(), amplifierRobot.getExpireTime());
                field.getAffectedAreaPool().addAffectedArea(affectedArea);
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

    public static void handleRemoveAccelerationBot(Summoned summoned) {
        summoned.getField().getMobSpawnModifiers().remove(summoned.getId());
        summoned.getField().getMobPool().forEach((mob) -> {
            if (!mob.isBoss()) {
                mob.resetTemporaryStat(summoned.getSkillId());
            }
        });
    }

    private static int getSummonDuration(User user, int duration) {
        // Increased duration from Robot Mastery + tick interval for self-destruct
        if (user.getSkillLevel(ROBOT_MASTERY) > 0) {
            return duration + (user.getSkillStatValue(ROBOT_MASTERY, SkillStat.y) * 1000) + ServerConfig.FIELD_TICK_INTERVAL;
        }
        return duration + ServerConfig.FIELD_TICK_INTERVAL;
    }
}