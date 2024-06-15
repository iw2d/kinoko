package kinoko.world.job.explorer;

import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillDispatcher;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.DiceInfo;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.Map;

public final class Pirate extends SkillDispatcher {
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

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case BACKSPIN_BLOW:
            case DOUBLE_UPPERCUT:
            case SNATCH:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss()) {
                        mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
            case ENERGY_BLAST:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
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
        switch (skillId) {
            // COMMON
            case DASH:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Dash_Speed, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.Dash_Speed, si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Dash_Jump, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.Dash_Jump, si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                ));
                return;
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
            case TRANSFORMATION:
            case SUPER_TRANSFORMATION:
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
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}