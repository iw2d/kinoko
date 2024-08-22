package kinoko.world.job.explorer;

import kinoko.packet.field.MobPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
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
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.DiceInfo;
import kinoko.world.user.stat.TemporaryStatOption;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public final class Pirate extends SkillProcessor {
    // PIRATE
    public static final int BULLET_TIME = 5000000;
    public static final int FLASH_FIST = 5001001;
    public static final int SOMMERSAULT_KICK = 5001002;
    public static final int DOUBLE_SHOT = 5001003;
    public static final int DASH = 5001005;
    // BRAWLER
    public static final int KNUCKLE_MASTERY = 5100001;
    public static final int CRITICAL_PUNCH = 5100008;
    public static final int HP_BOOST = 5100009;
    public static final int BACKSPIN_BLOW = 5101002;
    public static final int DOUBLE_UPPERCUT = 5101003;
    public static final int CORKSCREW_BLOW = 5101004;
    public static final int MP_RECOVERY = 5101005;
    public static final int KNUCKLE_BOOSTER = 5101006;
    public static final int OAK_BARREL = 5101007;
    // MARAUDER
    public static final int STUN_MASTERY = 5110000;
    public static final int ENERGY_CHARGE = 5110001;
    public static final int BRAWLING_MASTERY = 5110008;
    public static final int ENERGY_BLAST = 5111002;
    public static final int ENERGY_DRAIN = 5111004;
    public static final int TRANSFORMATION = 5111005;
    public static final int SHOCKWAVE = 5111006;
    public static final int ROLL_OF_THE_DICE_BUCC = 5111007;
    // BUCCANEER
    public static final int PIRATES_REVENGE_BUCC = 5120011;
    public static final int MAPLE_WARRIOR_BUCC = 5121000;
    public static final int DRAGON_STRIKE = 5121001;
    public static final int ENERGY_ORB = 5121002;
    public static final int SUPER_TRANSFORMATION = 5121003;
    public static final int DEMOLITION = 5121004;
    public static final int SNATCH = 5121005;
    public static final int BARRAGE = 5121007;
    public static final int PIRATES_RAGE = 5121008; // Hero's Will
    public static final int SPEED_INFUSION = 5121009;
    public static final int TIME_LEAP = 5121010;
    // GUNSLINGER
    public static final int GUN_MASTERY = 5200000;
    public static final int CRITICAL_SHOT = 5200007;
    public static final int INVISIBLE_SHOT = 5201001;
    public static final int GRENADE = 5201002;
    public static final int GUN_BOOSTER = 5201003;
    public static final int BLANK_SHOT = 5201004;
    public static final int WINGS = 5201005;
    public static final int RECOIL_SHOT = 5201006;
    // OUTLAW
    public static final int BURST_FIRE = 5210000;
    public static final int OCTOPUS = 5211001;
    public static final int GAVIOTA = 5211002;
    public static final int FLAMETHROWER = 5211004;
    public static final int ICE_SPLITTER = 5211005;
    public static final int HOMING_BEACON = 5211006;
    public static final int ROLL_OF_THE_DICE_SAIR = 5211007;
    // CORSAIR
    public static final int ELEMENTAL_BOOST = 5220001;
    public static final int WRATH_OF_THE_OCTOPI = 5220002;
    public static final int BULLSEYE = 5220011;
    public static final int PIRATES_REVENGE_SAIR = 5220012;
    public static final int MAPLE_WARRIOR_SAIR = 5221000;
    public static final int AIR_STRIKE = 5221003;
    public static final int RAPID_FIRE = 5221004;
    public static final int BATTLESHIP = 5221006;
    public static final int BATTLESHIP_CANNON = 5221007;
    public static final int BATTLESHIP_TORPEDO = 5221008;
    public static final int HYPNOTIZE = 5221009;
    public static final int HEROS_WILL_SAIR = 5221010;

    public static void handleAttack(User user, Mob mob, Attack attack, int delay) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case BACKSPIN_BLOW:
            case DOUBLE_UPPERCUT:
            case SNATCH:
            case BLANK_SHOT:
                if (!mob.isBoss()) {
                    mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                break;
            case ENERGY_BLAST:
                if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                    mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                break;
            case GRENADE:
                mob.setBurnedInfo(BurnedInfo.from(user, si, slv, mob), delay);
                break;
            case GAVIOTA:
                user.removeSummoned((summoned) -> summoned.getSkillId() == skillId);
                break;
            case FLAMETHROWER:
                final int dot = si.getValue(SkillStat.dot, slv) + user.getSkillStatValue(ELEMENTAL_BOOST, SkillStat.x);
                mob.setBurnedInfo(BurnedInfo.from(user, si, slv, dot, mob), delay);
                break;
            case ICE_SPLITTER:
                final int time = si.getValue(SkillStat.time, slv) + user.getSkillStatValue(ELEMENTAL_BOOST, SkillStat.y);
                mob.setTemporaryStat(MobTemporaryStat.Freeze, MobStatOption.of(1, skillId, time * 1000), delay);
                break;
            case HOMING_BEACON:
            case BULLSEYE:
                if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.GuidedBullet)) {
                    user.resetTemporaryStat(Set.of(CharacterTemporaryStat.GuidedBullet));
                }
                user.setTemporaryStat(CharacterTemporaryStat.GuidedBullet, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.GuidedBullet, skillId == BULLSEYE ? si.getValue(SkillStat.x, slv) : 1, skillId, mob.getId()));
                break;
            case HYPNOTIZE:
                if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                    mob.setTemporaryStat(MobTemporaryStat.Dazzle, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                    if (mob.getController() != user) {
                        mob.setController(user);
                        user.write(MobPacket.mobChangeController(mob, true));
                        field.broadcastPacket(MobPacket.mobChangeController(mob, false), user);
                    }
                }
                break;
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            // COMMON
            case ROLL_OF_THE_DICE_BUCC:
            case ROLL_OF_THE_DICE_SAIR:
                final int roll = Util.getRandom(1, 6);
                user.write(UserLocal.effect(Effect.skillAffectedSelect(roll, skillId, slv)));
                field.broadcastPacket(UserRemote.effect(user, Effect.skillAffectedSelect(roll, skillId, slv)), user);
                if (roll != 1) {
                    final DiceInfo diceInfo = DiceInfo.from(roll, si, slv);
                    user.setTemporaryStat(CharacterTemporaryStat.Dice, TemporaryStatOption.ofDice(roll, skillId, si.getDuration(slv), diceInfo));
                }
                return;

            // BUCC
            case MP_RECOVERY:
                final int hp = user.getMaxHp() * si.getValue(SkillStat.x, slv) / 100;
                user.addMp(hp * si.getValue(SkillStat.y, slv) / 100);
                return;
            case OAK_BARREL:
                user.setTemporaryStat(CharacterTemporaryStat.Morph, TemporaryStatOption.of(si.getValue(SkillStat.morph, slv), skillId, si.getDuration(slv)));
                return;
            case TIME_LEAP:
                final var iter = user.getSkillManager().getSkillCooltimes().keySet().iterator();
                while (iter.hasNext()) {
                    final int toReset = iter.next();
                    if (toReset != TIME_LEAP) {
                        user.write(UserLocal.skillCooltimeSet(toReset, 0));
                        iter.remove();
                    }
                }
                return;

            // SAIR
            case GRENADE:
                // Handled in attack
                return;
            case OCTOPUS:
            case WRATH_OF_THE_OCTOPI:
                final Summoned octopus = Summoned.from(si, slv, SummonedMoveAbility.STOP, SummonedAssistType.ATTACK);
                octopus.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                user.addSummoned(octopus);
                return;
            case GAVIOTA:
                final Summoned gaviota = Summoned.from(si, slv, SummonedMoveAbility.FLY, SummonedAssistType.ATTACK);
                gaviota.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                user.addSummoned(gaviota);
                return;
            case BATTLESHIP:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.RideVehicle, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.RideVehicle, SkillConstants.BATTLESHIP_VEHICLE, skillId, 0),
                        CharacterTemporaryStat.EPAD, TemporaryStatOption.of(si.getValue(SkillStat.epad, slv), skillId, 0),
                        CharacterTemporaryStat.EMHP, TemporaryStatOption.of(si.getValue(SkillStat.emhp, slv), skillId, 0),
                        CharacterTemporaryStat.EMMP, TemporaryStatOption.of(si.getValue(SkillStat.emmp, slv), skillId, 0),
                        CharacterTemporaryStat.EPDD, TemporaryStatOption.of(si.getValue(SkillStat.epdd, slv), skillId, 0),
                        CharacterTemporaryStat.EMDD, TemporaryStatOption.of(si.getValue(SkillStat.emdd, slv), skillId, 0)
                ));
                setBattleshipDurability(user, getBattleshipDurability(user));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }

    public static int getBattleshipDurability(User user) {
        final Instant cooltime = user.getSkillManager().getSkillCooltimes().get(SkillConstants.BATTLESHIP_DURABILITY);
        if (cooltime != null) {
            return (int) cooltime.getEpochSecond();
        } else {
            // get_max_durability_of_vehicle
            return 300 * user.getLevel() + 500 * (user.getSkillLevel(BATTLESHIP) - 72);
        }
    }

    public static void setBattleshipDurability(User user, int durability) {
        final Instant cooltime = Instant.ofEpochSecond(durability);
        user.getSkillManager().getSkillCooltimes().put(SkillConstants.BATTLESHIP_DURABILITY, cooltime);
        user.write(UserLocal.skillCooltimeSet(SkillConstants.BATTLESHIP_DURABILITY, durability));
    }
}