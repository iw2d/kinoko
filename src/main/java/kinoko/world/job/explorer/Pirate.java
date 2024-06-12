package kinoko.world.job.explorer;

import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillDispatcher;
import kinoko.world.user.User;

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
        final int skillId = attack.skillId;
        final int slv = attack.slv;
        switch (skillId) {
        }
    }

    public static void handleSkill(User user, Skill skill) {
        log.error("Unhandled skill {}", skill.skillId);
    }
}