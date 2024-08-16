package kinoko.world.skill;

import java.util.ArrayList;
import java.util.List;

public enum ActionType {
    // ACT
    WALK1(0x0),
    WALK2(0x1),
    STAND1(0x2),
    STAND2(0x3),
    ALERT(0x4),
    SWINGO1(0x5),
    SWINGO2(0x6),
    SWINGO3(0x7),
    SWINGOF(0x8),
    SWINGT1(0x9),
    SWINGT2(0x0A),
    SWINGT3(0x0B),
    SWINGTF(0x0C),
    SWINGP1(0x0D),
    SWINGP2(0x0E),
    SWINGPF(0x0F),
    STABO1(0x10),
    STABO2(0x11),
    STABOF(0x12),
    STABT1(0x13),
    STABT2(0x14),
    STABTF(0x15),
    SWINGD1(0x16),
    SWINGD2(0x17),
    STABD1(0x18),
    TRIPLEBLOW(0x19),
    QUADBLOW(0x1A),
    DEATHBLOW(0x1B),
    FINISHBLOW(0x1C),
    FINISHATTACK_LINK(0x1D),
    FINISHATTACK_LINK2(0x1E),
    SHOOT1(0x1F),
    SHOOT2(0x20),
    SHOOT3(0x21),
    SHOOT4(0x22),
    SHOOT5(0x23),
    SHOOTF(0x24),
    MAGICATTACK1(0x25),
    MAGICATTACK2(0x26),
    MAGICATTACKF(0x27),
    MAGICHEAL(0x28),
    PRONESTAB(0x29),
    PRONE(0x2A),
    FLY1(0x2B),
    JUMP(0x2C),
    LADDER(0x2D),
    ROPE(0x2E),
    DEAD(0x2F),
    SIT(0x30),
    SIT2(0x31),
    SIT3(0x32),
    SIT4(0x33),
    SIT5(0x34),
    SIT6(0x35),
    SIT7(0x36),
    TIRED(0x37),
    SIEGE2_PRONE(0x38),
    PRONESTAB_JAGUAR(0x39),
    ALERT2(0x3A),
    ALERT3(0x3B),
    ALERT4(0x3C),
    ALERT5(0x3D),
    ALERT6(0x3E),
    ALERT7(0x3F),
    LADDER2(0x40),
    ROPE2(0x41),
    SHOOT6(0x42),
    MAGIC1(0x43),
    MAGIC2(0x44),
    MAGIC3(0x45),
    MAGIC5(0x46),
    MAGIC6(0x47),
    BURSTER1(0x48),
    BURSTER2(0x49),
    SAVAGEBLOW(0x4A),
    AVENGER(0x4B),
    ASSAULTER(0x4C),
    PRONE2(0x4D),
    ASSASSINATION(0x4E),
    ASSASSINATIONS(0x4F),
    HUSTLE_DASH(0x50),
    HUSTLE_STOP(0x51),
    HUSTLE_ATTACK(0x52),
    RUSH(0x53),
    RUSH2(0x54),
    BRANDISH1(0x55),
    BRANDISH2(0x56),
    BRAVE_SLASH(0x57),
    SANCTUARY(0x58),
    METEOR(0x59),
    PARALYZE(0x5A),
    BLIZZARD(0x5B),
    GENESIS(0x5C),
    NINJASTORM(0x5D),
    BLAST(0x5E),
    HOLYSHIELD(0x5F),
    SHOWDOWN(0x60),
    RESURRECTION(0x61),
    CHAINLIGHTNING(0x62),
    SMOKESHELL(0x63),
    HANDGUN(0x64),
    SOMERSAULT(0x65),
    STRAIGHT(0x66),
    EBURSTER(0x67),
    BACKSPIN(0x68),
    EORB(0x69),
    SCREW(0x6A),
    DOUBLEUPPER(0x6B),
    DRAGONSTRIKE(0x6C),
    DOUBLEFIRE(0x6D),
    TRIPLEFIRE(0x6E),
    FAKE(0x6F),
    AIRSTRIKE(0x70),
    EDRAIN(0x71),
    OCTOPUS(0x72),
    BACKSTEP(0x73),
    SHOT(0x74),
    RECOVERY(0x75),
    FIREBURNER(0x76),
    COOLINGEFFECT(0x77),
    FIST(0x78),
    TIMELEAP(0x79),
    RAPIDFIRE(0x7A),
    HOMING(0x7B),
    GHOST_WALK(0x7C),
    GHOST_STAND(0x7D),
    GHOST_JUMP(0x7E),
    GHOST_PRONESTAB(0x7F),
    GHOST_FLY(0x80),
    GHOST_LADDER(0x81),
    GHOST_ROPE(0x82),
    GHOST_SIT(0x83),
    CANNON(0x84),
    TORPEDO(0x85),
    DARKSIGHT(0x86),
    BAMBOO(0x87),
    PYRAMID(0x88),
    WAVE(0x89),
    BLADE(0x8A),
    SOUL_DRIVER(0x8B),
    FIRE_STRIKE(0x8C),
    FLAME_GEAR(0x8D),
    STORM_BREAK(0x8E),
    VAMPIRE(0x8F),
    EVENT_FLOATING(0x90),
    SWINGT2_POLEARM(0x91),
    SWINGP1_POLEARM(0x92),
    SWINGP2_POLEARM(0x93),
    DOUBLE_SWING(0x94),
    TRIPLE_SWING(0x95),
    FULL_SWING_DOUBLE(0x96),
    FULL_SWING_TRIPLE(0x97),
    OVER_SWING_DOUBLE(0x98),
    OVER_SWING_TRIPLE(0x99),
    ROLLING_SPIN(0x9A),
    COMBO_SMASH(0x9B),
    COMBO_FENRIR(0x9C),
    COMBO_TEMPEST(0x9D),
    FINAL_CHARGE(0x9E),
    COMBAT_STEP(0x9F),
    FINAL_BLOW(0x0A0),
    FINAL_TOSS(0x0A1),
    MAGIC_MISSILE(0x0A2),
    LIGHTINGBOLT(0x0A3),
    DRAGON_BREATHE(0x0A4),
    BREATHE_PREPARE(0x0A5),
    DRAGON_ICE_BREATHE(0x0A6),
    ICE_BREATHE_PREPARE(0x0A7),
    BLAZE(0x0A8),
    FIRECIRCLE(0x0A9),
    ILLUSION(0x0AA),
    MAGICFLARE(0x0AB),
    ELEMENTAL_RESET(0x0AC),
    MAGIC_REGISTANCE(0x0AD),
    RECOVERY_AURA(0x0AE),
    MAGIC_BOOSTER(0x0AF),
    MAGIC_SHIELD(0x0B0),
    FLAME_WHEEL(0x0B1),
    KILLING_WING(0x0B2),
    ONIX_BLESSING(0x0B3),
    EARTHQUAKE(0x0B4),
    SOULSTONE(0x0B5),
    DRAGONTHRUST(0x0B6),
    GHOST_LETHERING(0x0B7),
    DARKFOG(0x0B8),
    SLOW(0x0B9),
    MAPLE_HERO(0x0BA),
    AWAKENING(0x0BB),
    FLYING_ASSAULTER(0x0BC),
    TRIPLE_STAB(0x0BD),
    FATAL_BLOW(0x0BE),
    SLASH_STORM1(0x0BF),
    SLASH_STORM2(0x0C0),
    SLASH_STORM3(0x0C1),
    FLASH_BANG(0x0C2),
    UPPER_STAB(0x0C3),
    OWL_DEAD(0x0C4),
    CHAIN_PULL(0x0C5),
    CHAIN_ATTACK(0x0C6),
    SUDDEN_DEATH(0x0C7),
    MONSTER_BOMB_PREPARE(0x0C8),
    MONSTER_BOMB_THROW(0x0C9),
    FINAL_CUT(0x0CA),
    FINAL_CUT_PREPARE(0x0CB),
    CYCLONE_PRE(0x0CC),
    CYCLONE(0x0CD),
    CYCLONE_AFTER(0x0CE),
    DOUBLEJUMP(0x0CF),
    KNOCKBACK(0x0D0),
    ROCKET_BOOSTER_START(0x0D1),
    ROCKET_BOOSTER(0x0D2),
    ROCKET_BOOSTER_END(0x0D3),
    CROSS_ROAD(0x0D4),
    NEMESIS(0x0D5),
    WILDBEAST(0x0D6),
    SIEGE1_START(0x0D7),
    SIEGE1(0x0D8),
    SIEGE1_STAND(0x0D9),
    SIEGE1_END(0x0DA),
    SIEGE2_START(0x0DB),
    SIEGE2(0x0DC),
    SIEGE2_STAND(0x0DD),
    SIEGE2_END(0x0DE),
    SIEGE2_WALK(0x0DF),
    SIEGE2_LASER(0x0E0),
    SIEGE_START(0x0E1),
    SIEGE(0x0E2),
    SIEGE_STAND(0x0E3),
    SIEGE_END(0x0E4),
    SONICBOOM(0x0E5),
    REVIVE(0x0E6),
    DARK_SPEAR(0x0E7),
    DARK_CHAIN(0x0E8),
    FLAMETHROWER_START(0x0E9),
    FLAMETHROWER(0x0EA),
    FLAMETHROWER_END(0x0EB),
    FLAMETHROWER2_START(0x0EC),
    FLAMETHROWER2(0x0ED),
    FLAMETHROWER2_END(0x0EE),
    MECHANIC_BOOSTER(0x0EF),
    MSUMMON(0x0F0),
    MSUMMON2(0x0F1),
    GATLINGSHOT(0x0F2),
    GATLINGSHOT2(0x0F3),
    DRILLRUSH(0x0F4),
    EARTHSLUG(0x0F5),
    ROCKET_PUNCH(0x0F6),
    CLAW_CUT(0x0F7),
    SWALLOW(0x0F8),
    SWALLOW_LOOP(0x0F9),
    SWALLOW_ATTACK(0x0FA),
    SWALLOW_PRE(0x0FB),
    FLASH_RAIN(0x0FC),
    MINE(0x0FD),
    CAPTURE(0x0FE),
    RIDE(0x0FF),
    GETOFF(0x100),
    RIDE2(0x101),
    GETOFF2(0x102),
    MECHANIC_RUSH(0x103),
    TANK_MSUMMON(0x104),
    TANK_MSUMMON2(0x105),
    TANK_MRUSH(0x106),
    TANK_RBOOSTER_PRE(0x107),
    TANK_RBOOSTER_AFTER(0x108),
    SHOCKWAVE(0x109),
    DEMOLITION(0x10A),
    SNATCH(0x10B),
    WIND_SPEAR(0x10C),
    WIND_SHOT(0x10D),
    FLY2(0x10E),
    FLY2_MOVE(0x10F),
    FLY2_SKILL(0x110),
    NO(0x111);

    private static final List<ActionType> actionList;

    static {
        actionList = new ArrayList<>();
        for (ActionType type : values()) {
            actionList.add(type);
            assert actionList.get(type.getValue()) == type;
        }
    }

    private final int value;

    ActionType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static boolean isShootAction(ActionType actionType) {
        switch (actionType) {
            case SHOOT1:
            case SHOOT2:
            case SHOOT3:
            case SHOOT4:
            case SHOOT5:
            case SHOOTF:
            case AVENGER:
            case SHOT:
            case FAKE:
            case HANDGUN:
            case DOUBLEFIRE:
            case TRIPLEFIRE:
            case RAPIDFIRE:
            case BACKSTEP:
            case HOMING:
            case STORM_BREAK:
            case WIND_SPEAR:
            case WIND_SHOT:
            case MONSTER_BOMB_PREPARE:
            case FINAL_CUT_PREPARE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isProneStabAction(ActionType actionType) {
        return actionType == PRONESTAB || actionType == PRONESTAB_JAGUAR;
    }

    public static ActionType getByValue(int value) {
        return actionList.get(value);
    }

    public static ActionType getByName(String name) {
        // get_action_code_from_name
        return switch (name) {
            case "walk1" -> getByValue(0x0);
            case "walk2" -> getByValue(0x1);
            case "stand1" -> getByValue(0x2);
            case "stand2" -> getByValue(0x3);
            case "alert" -> getByValue(0x4);
            case "swingO1" -> getByValue(0x5);
            case "swingO2" -> getByValue(0x6);
            case "swingO3" -> getByValue(0x7);
            case "swingOF" -> getByValue(0x8);
            case "swingT1" -> getByValue(0x9);
            case "swingT2" -> getByValue(0xA);
            case "swingT3" -> getByValue(0xB);
            case "swingTF" -> getByValue(0xC);
            case "swingP1" -> getByValue(0xD);
            case "swingP2" -> getByValue(0xE);
            case "swingPF" -> getByValue(0xF);
            case "stabO1" -> getByValue(0x10);
            case "stabO2" -> getByValue(0x11);
            case "stabOF" -> getByValue(0x12);
            case "stabT1" -> getByValue(0x13);
            case "stabT2" -> getByValue(0x14);
            case "stabTF" -> getByValue(0x15);
            case "swingD1" -> getByValue(0x16);
            case "swingD2" -> getByValue(0x17);
            case "stabD1" -> getByValue(0x18);
            case "tripleBlow" -> getByValue(0x19);
            case "quadBlow" -> getByValue(0x1A);
            case "deathBlow" -> getByValue(0x1B);
            case "finishBlow" -> getByValue(0x1C);
            case "finishAttack_link" -> getByValue(0x1D);
            case "finishAttack_link2" -> getByValue(0x1E);
            case "shoot1" -> getByValue(0x1F);
            case "shoot2" -> getByValue(0x20);
            case "shootF" -> getByValue(0x24);
            case "heal" -> getByValue(0x28);
            case "proneStab" -> getByValue(0x29);
            case "prone" -> getByValue(0x2A);
            case "fly" -> getByValue(0x2B);
            case "jump" -> getByValue(0x2C);
            case "ladder" -> getByValue(0x2D);
            case "rope" -> getByValue(0x2E);
            case "dead" -> getByValue(0x2F);
            case "sit" -> getByValue(0x30);
            case "sit2" -> getByValue(0x31);
            case "sit3" -> getByValue(0x32);
            case "sit4" -> getByValue(0x33);
            case "sit5" -> getByValue(0x34);
            case "sit6" -> getByValue(0x35);
            case "sit7" -> getByValue(0x36);
            case "tired" -> getByValue(0x37);
            case "tank_prone" -> getByValue(0x38);
            case "proneStab_jaguar" -> getByValue(0x39);
            case "alert2" -> getByValue(0x3A);
            case "alert3" -> getByValue(0x3B);
            case "alert4" -> getByValue(0x3C);
            case "alert5" -> getByValue(0x3D);
            case "alert6" -> getByValue(0x3E);
            case "alert7" -> getByValue(0x3F);
            case "ladder2" -> getByValue(0x40);
            case "rope2" -> getByValue(0x41);
            case "shoot6" -> getByValue(0x42);
            case "magic1" -> getByValue(0x43);
            case "magic2" -> getByValue(0x44);
            case "magic3" -> getByValue(0x45);
            case "magic5" -> getByValue(0x46);
            case "magic6" -> getByValue(0x47);
            case "burster1" -> getByValue(0x48);
            case "burster2" -> getByValue(0x49);
            case "savage" -> getByValue(0x4A);
            case "avenger" -> getByValue(0x4B);
            case "assaulter" -> getByValue(0x4C);
            case "prone2" -> getByValue(0x4D);
            case "assassination" -> getByValue(0x4E);
            case "assassinationS" -> getByValue(0x4F);
            case "tornadoDash" -> getByValue(0x50);
            case "tornadoDashStop" -> getByValue(0x51);
            case "tornadoRush" -> getByValue(0x52);
            case "rush" -> getByValue(0x53);
            case "rush2" -> getByValue(0x54);
            case "brandish1" -> getByValue(0x55);
            case "brandish2" -> getByValue(0x56);
            case "braveslash" -> getByValue(0x57);
            case "sanctuary" -> getByValue(0x58);
            case "meteor" -> getByValue(0x59);
            case "paralyze" -> getByValue(0x5A);
            case "blizzard" -> getByValue(0x5B);
            case "genesis" -> getByValue(0x5C);
            case "ninjastorm" -> getByValue(0x5D);
            case "blast" -> getByValue(0x5E);
            case "holyshield" -> getByValue(0x5F);
            case "showdown" -> getByValue(0x60);
            case "resurrection" -> getByValue(0x61);
            case "chainlightning" -> getByValue(0x62);
            case "smokeshell" -> getByValue(0x63);
            case "handgun" -> getByValue(0x64);
            case "somersault" -> getByValue(0x65);
            case "straight" -> getByValue(0x66);
            case "eburster" -> getByValue(0x67);
            case "backspin" -> getByValue(0x68);
            case "eorb" -> getByValue(0x69);
            case "screw" -> getByValue(0x6A);
            case "doubleupper" -> getByValue(0x6B);
            case "dragonstrike" -> getByValue(0x6C);
            case "doublefire" -> getByValue(0x6D);
            case "triplefire" -> getByValue(0x6E);
            case "fake" -> getByValue(0x6F);
            case "airstrike" -> getByValue(0x70);
            case "edrain" -> getByValue(0x71);
            case "octopus" -> getByValue(0x72);
            case "backstep" -> getByValue(0x73);
            case "shot" -> getByValue(0x74);
            case "recovery" -> getByValue(0x75);
            case "fireburner" -> getByValue(0x76);
            case "coolingeffect" -> getByValue(0x77);
            case "fist" -> getByValue(0x78);
            case "timeleap" -> getByValue(0x79);
            case "rapidfire" -> getByValue(0x7A);
            case "homing" -> getByValue(0x7B);
            case "ghostwalk" -> getByValue(0x7C);
            case "ghoststand" -> getByValue(0x7D);
            case "ghostjump" -> getByValue(0x7E);
            case "ghostproneStab" -> getByValue(0x7F);
            case "ghostfly" -> getByValue(0x80);
            case "ghostladder" -> getByValue(0x81);
            case "ghostrope" -> getByValue(0x82);
            case "ghostsit" -> getByValue(0x83);
            case "cannon" -> getByValue(0x84);
            case "torpedo" -> getByValue(0x85);
            case "darksight" -> getByValue(0x86);
            case "bamboo" -> getByValue(0x87);
            case "pyramid" -> getByValue(0x88);
            case "wave" -> getByValue(0x89);
            case "blade" -> getByValue(0x8A);
            case "souldriver" -> getByValue(0x8B);
            case "firestrike" -> getByValue(0x8C);
            case "flamegear" -> getByValue(0x8D);
            case "stormbreak" -> getByValue(0x8E);
            case "vampire" -> getByValue(0x8F);
            case "float" -> getByValue(0x90);
            case "swingT2PoleArm" -> getByValue(0x91);
            case "swingP1PoleArm" -> getByValue(0x92);
            case "swingP2PoleArm" -> getByValue(0x93);
            case "doubleSwing" -> getByValue(0x94);
            case "tripleSwing" -> getByValue(0x95);
            case "fullSwingDouble" -> getByValue(0x96);
            case "fullSwingTriple" -> getByValue(0x97);
            case "overSwingDouble" -> getByValue(0x98);
            case "overSwingTriple" -> getByValue(0x99);
            case "rollingSpin" -> getByValue(0x9A);
            case "comboSmash" -> getByValue(0x9B);
            case "comboFenrir" -> getByValue(0x9C);
            case "comboTempest" -> getByValue(0x9D);
            case "finalCharge" -> getByValue(0x9E);
            case "combatStep" -> getByValue(0x9F);
            case "finalBlow" -> getByValue(0xA0);
            case "finalToss" -> getByValue(0xA1);
            case "magicmissile" -> getByValue(0xA2);
            case "lightingBolt" -> getByValue(0xA3);
            case "dragonBreathe" -> getByValue(0xA4);
            case "breathe_prepare" -> getByValue(0xA5);
            case "dragonIceBreathe" -> getByValue(0xA6);
            case "icebreathe_prepare" -> getByValue(0xA7);
            case "blaze" -> getByValue(0xA8);
            case "fireCircle" -> getByValue(0xA9);
            case "illusion" -> getByValue(0xAA);
            case "magicFlare" -> getByValue(0xAB);
            case "elementalReset" -> getByValue(0xAC);
            case "magicRegistance" -> getByValue(0xAD);
            case "recoveryAura" -> getByValue(0xAE);
            case "magicBooster" -> getByValue(0xAF);
            case "magicShield" -> getByValue(0xB0);
            case "flameWheel" -> getByValue(0xB1);
            case "killingWing" -> getByValue(0xB2);
            case "OnixBlessing" -> getByValue(0xB3);
            case "Earthquake" -> getByValue(0xB4);
            case "soulStone" -> getByValue(0xB5);
            case "dragonThrust" -> getByValue(0xB6);
            case "ghostLettering" -> getByValue(0xB7);
            case "darkFog" -> getByValue(0xB8);
            case "slow" -> getByValue(0xB9);
            case "mapleHero" -> getByValue(0xBA);
            case "Awakening" -> getByValue(0xBB);
            case "flyingAssaulter" -> getByValue(0xBC);
            case "tripleStab" -> getByValue(0xBD);
            case "fatalBlow" -> getByValue(0xBE);
            case "slashStorm1" -> getByValue(0xBF);
            case "slashStorm2" -> getByValue(0xC0);
            case "bloodyStorm" -> getByValue(0xC1);
            case "flashBang" -> getByValue(0xC2);
            case "upperStab" -> getByValue(0xC3);
            case "suddenRaid" -> getByValue(0xC4);
            case "chainPull" -> getByValue(0xC5);
            case "chainAttack" -> getByValue(0xC6);
            case "owlDead" -> getByValue(0xC7);
            case "monsterBombPrepare" -> getByValue(0xC8);
            case "monsterBombThrow" -> getByValue(0xC9);
            case "finalCut" -> getByValue(0xCA);
            case "finalCutPrepare" -> getByValue(0xCB);
            case "cyclone_pre" -> getByValue(0xCC);
            case "cyclone" -> getByValue(0xCD);
            case "cyclone_after" -> getByValue(0xCE);
            case "doubleJump" -> getByValue(0xCF);
            case "knockback" -> getByValue(0xD0);
            case "rbooster_pre" -> getByValue(0xD1);
            case "rbooster" -> getByValue(0xD2);
            case "rbooster_after" -> getByValue(0xD3);
            case "crossRoad" -> getByValue(0xD4);
            case "nemesis" -> getByValue(0xD5);
            case "wildbeast" -> getByValue(0xD6);
            case "siege_pre" -> getByValue(0xD7);
            case "siege" -> getByValue(0xD8);
            case "siege_stand" -> getByValue(0xD9);
            case "siege_after" -> getByValue(0xDA);
            case "tank_pre" -> getByValue(0xDB);
            case "tank" -> getByValue(0xDC);
            case "tank_stand" -> getByValue(0xDD);
            case "tank_after" -> getByValue(0xDE);
            case "tank_walk" -> getByValue(0xDF);
            case "tank_laser" -> getByValue(0xE0);
            case "tank_siegepre" -> getByValue(0xE1);
            case "tank_siegeattack" -> getByValue(0xE2);
            case "tank_siegestand" -> getByValue(0xE3);
            case "tank_siegeafter" -> getByValue(0xE4);
            case "sonicBoom" -> getByValue(0xE5);
            case "revive" -> getByValue(0xE6);
            case "darkSpear" -> getByValue(0xE7);
            case "darkChain" -> getByValue(0xE8);
            case "flamethrower_pre" -> getByValue(0xE9);
            case "flamethrower" -> getByValue(0xEA);
            case "flamethrower_after" -> getByValue(0xEB);
            case "flamethrower_pre2" -> getByValue(0xEC);
            case "flamethrower2" -> getByValue(0xED);
            case "flamethrower_after2" -> getByValue(0xEE);
            case "mbooster" -> getByValue(0xEF);
            case "msummon" -> getByValue(0xF0);
            case "msummon2" -> getByValue(0xF1);
            case "gatlingshot" -> getByValue(0xF2);
            case "gatlingshot2" -> getByValue(0xF3);
            case "drillrush" -> getByValue(0xF4);
            case "earthslug" -> getByValue(0xF5);
            case "rpunch" -> getByValue(0xF6);
            case "clawCut" -> getByValue(0xF7);
            case "swallow" -> getByValue(0xF8);
            case "swallow_loop" -> getByValue(0xF9);
            case "swallow_attack" -> getByValue(0xFA);
            case "swallow_pre" -> getByValue(0xFB);
            case "flashRain" -> getByValue(0xFC);
            case "mine" -> getByValue(0xFD);
            case "capture" -> getByValue(0xFE);
            case "ride" -> getByValue(0xFF);
            case "getoff" -> getByValue(0x100);
            case "ride2" -> getByValue(0x101);
            case "getoff2" -> getByValue(0x102);
            case "mRush" -> getByValue(0x103);
            case "tank_msummon" -> getByValue(0x104);
            case "tank_msummon2" -> getByValue(0x105);
            case "tank_mRush" -> getByValue(0x106);
            case "tank_rbooster_pre" -> getByValue(0x107);
            case "tank_rbooster_after" -> getByValue(0x108);
            case "shockwave" -> getByValue(0x109);
            case "demolition" -> getByValue(0x10A);
            case "snatch" -> getByValue(0x10B);
            case "windspear" -> getByValue(0x10C);
            case "windshot" -> getByValue(0x10D);
            case "fly2" -> getByValue(0x10E);
            case "fly2Move" -> getByValue(0x10F);
            case "fly2Skill" -> getByValue(0x110);
            default -> NO;
        };
    }
}
