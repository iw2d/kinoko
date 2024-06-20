package kinoko.world.skill;

import kinoko.provider.skill.ElementAttribute;
import kinoko.util.Rect;
import kinoko.world.job.JobConstants;
import kinoko.world.job.cygnus.*;
import kinoko.world.job.explorer.*;
import kinoko.world.job.legend.Aran;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.util.List;

public final class SkillConstants {
    public static final List<Integer> SECONDARY_STAT_SKILLS = List.of(
            Thief.NIMBLE_BODY,
            NightWalker.NIMBLE_BODY,
            Pirate.BULLET_TIME, // PirateQuickMotion
            ThunderBreaker.QUICK_MOTION, // StrikerQuickMotion
            Evan.DRAGON_SOUL,
            Beginner.BLESSING_OF_THE_FAIRY,
            Noblesse.BLESSING_OF_THE_FAIRY,
            Citizen.BLESSING_OF_THE_FAIRY,
            Aran.BLESSING_OF_THE_FAIRY,
            Evan.BLESSING_OF_THE_FAIRY
    );
    public static final List<Integer> WILD_HUNTER_JAGUARS = List.of(
            1932015, 1932030, 1932031, 1932032, 1932033, 1932036
    );
    public static final int MECHANIC_VEHICLE = 1932016;
    public static final int BATTLESHIP_VEHICLE = 1932000;
    public static final int BATTLESHIP_DURABILITY = 5221999;
    public static final int ENERGY_CHARGE_MAX = 10000;
    public static final Rect DARK_FLARE_RANGE = new Rect(-400, -200, 400, 200);
    public static final Rect MONSTER_BOMB_RANGE = new Rect(-220, -150, 220, 0);

    public static int getSkillRoot(int skillId) {
        return skillId / 10000;
    }

    public static int getNoviceSkillAsRace(int skillId, int jobId) {
        if (JobConstants.isEvanJob(jobId)) {
            return skillId + 20010000;
        } else {
            return skillId + 10000000 * (jobId / 1000);
        }
    }

    public static int getItemBonusRateSkill(int jobId) {
        if (JobConstants.isNightLordJob(jobId)) {
            return Thief.ALCHEMIST;
        } else if (JobConstants.isNightWalkerJob(jobId)) {
            return NightWalker.ALCHEMIST;
        } else if (JobConstants.isResistanceJob(jobId)) {
            return Citizen.POTION_MASTERY;
        }
        return 0;
    }

    public static int getEnhancedBasicsSkill(int jobId) {
        if (JobConstants.isHeroJob(jobId)) {
            return Warrior.ENHANCED_BASICS_HERO;
        } else if (JobConstants.isPaladinJob(jobId)) {
            return Warrior.ENHANCED_BASICS_PALADIN;
        } else if (JobConstants.isDarkKnightJob(jobId)) {
            return Warrior.ENHANCED_BASICS_DRK;
        } else if (JobConstants.isBowmasterJob(jobId)) {
            return Bowman.ENHANCED_BASICS_BM;
        } else if (JobConstants.isMarksmanJob(jobId)) {
            return Bowman.ENHANCED_BASICS_MM;
        } else {
            return 0;
        }
    }

    public static int getComboAttackSkill(int jobId) {
        return JobConstants.isCygnusJob(jobId) ? DawnWarrior.COMBO_ATTACK : Warrior.COMBO_ATTACK;
    }

    public static int getAdvancedComboSkill(int jobId) {
        return JobConstants.isCygnusJob(jobId) ? DawnWarrior.ADVANCED_COMBO : Warrior.ADVANCED_COMBO_ATTACK;
    }

    public static int getMpEaterSkill(int jobId) {
        if (JobConstants.isFirePoisonJob(jobId)) {
            return Magician.MP_EATER_FP;
        } else if (JobConstants.isIceLightningJob(jobId)) {
            return Magician.MP_EATER_IL;
        } else if (JobConstants.isBishopJob(jobId)) {
            return Magician.MP_EATER_BISH;
        } else {
            return 0;
        }
    }

    public static int getBuffMasterySkill(int jobId) {
        if (JobConstants.isFirePoisonJob(jobId)) {
            return Magician.BUFF_MASTERY_FP;
        } else if (JobConstants.isIceLightningJob(jobId)) {
            return Magician.BUFF_MASTERY_IL;
        } else if (JobConstants.isBishopJob(jobId)) {
            return Magician.BUFF_MASTERY_BISH;
        } else {
            return 0;
        }
    }

    public static int getAmplificationSkill(int jobId) {
        if (JobConstants.isFirePoisonJob(jobId)) {
            return Magician.ELEMENT_AMPLIFICATION_FP;
        } else if (JobConstants.isIceLightningJob(jobId)) {
            return Magician.ELEMENT_AMPLIFICATION_IL;
        } else if (JobConstants.isBlazeWizardJob(jobId)) {
            return BlazeWizard.ELEMENT_AMPLIFICATION;
        } else if (JobConstants.isEvanJob(jobId)) {
            return Evan.MAGIC_AMPLIFICATION;
        } else {
            return 0;
        }
    }

    public static int getMortalBlowSkill(int jobId) {
        if (JobConstants.isBowmasterJob(jobId)) {
            return Bowman.MORTAL_BLOW_BM;
        } else if (JobConstants.isMarksmanJob(jobId)) {
            return Bowman.MORTAL_BLOW_MM;
        } else {
            return 0;
        }
    }

    public static int getVenomSkill(int jobId) {
        if (JobConstants.isNightLordJob(jobId)) {
            return Thief.VENOMOUS_STAR;
        } else if (JobConstants.isShadowerJob(jobId)) {
            return Thief.VENOMOUS_STAB;
        } else if (JobConstants.isDualJob(jobId)) {
            return Thief.VENOM_DB;
        } else {
            return 0;
        }
    }

    public static int getEnergyChargeSkill(int jobId) {
        return JobConstants.isCygnusJob(jobId) ? ThunderBreaker.ENERGY_CHARGE : Pirate.ENERGY_CHARGE;
    }

    public static int getPiratesRevengeSkill(int jobId) {
        if (JobConstants.isBuccaneerJob(jobId)) {
            return Pirate.PIRATES_REVENGE_BUCC;
        } else if (JobConstants.isCorsairJob(jobId)) {
            return Pirate.PIRATES_REVENGE_SAIR;
        } else {
            return 0;
        }
    }

    public static ElementAttribute getElementByWeaponChargeSkill(int skillId) {
        switch (skillId) {
            case Warrior.FIRE_CHARGE:
                return ElementAttribute.FIRE;
            case Warrior.ICE_CHARGE:
            case Aran.SNOW_CHARGE:
                return ElementAttribute.ICE;
            case Warrior.LIGHTNING_CHARGE:
            case ThunderBreaker.LIGHTNING_CHARGE:
                return ElementAttribute.LIGHT;
            case Warrior.DIVINE_CHARGE:
            case DawnWarrior.SOUL_CHARGE:
                return ElementAttribute.HOLY;
            default:
                return ElementAttribute.PHYSICAL;
        }
    }

    public static CharacterTemporaryStat getStatByAuraSkill(int skillId) {
        switch (skillId) {
            case BattleMage.DARK_AURA:
            case BattleMage.ADVANCED_DARK_AURA:
                return CharacterTemporaryStat.DarkAura;
            case BattleMage.BLUE_AURA:
            case BattleMage.ADVANCED_BLUE_AURA:
                return CharacterTemporaryStat.BlueAura;
            case BattleMage.YELLOW_AURA:
            case BattleMage.ADVANCED_YELLOW_AURA:
                return CharacterTemporaryStat.YellowAura;
            default:
                return null;
        }
    }

    public static boolean isBeginnerSpAddableSkill(int skillId) {
        if (!JobConstants.isBeginnerJob(getSkillRoot(skillId))) {
            return false;
        }
        final int skillType = skillId % 10000;
        return skillType == 1000 || skillType == 1001 || skillType == 1002;
    }


    public static boolean isTeleportSkill(int skillId) {
        switch (skillId) {
            case Magician.TELEPORT_FP:
            case Magician.TELEPORT_IL:
            case Magician.TELEPORT_BISH:
            case BlazeWizard.TELEPORT:
            case Evan.TELEPORT:
            case BattleMage.TELEPORT:
                return true;
            default:
                return false;
        }
    }

    public static boolean isThrowBombSkill(int skillId) {
        switch (skillId) {
            case Thief.MONSTER_BOMB:
            case Pirate.GRENADE:
            case NightWalker.POISON_BOMB:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNoConsumeAttack(int skillId) {
        if (SkillConstants.isThrowBombSkill(skillId)) {
            return true;
        }
        switch (skillId) {
            case BattleMage.TWISTER_SPIN:
            case Mechanic.MECH_SIEGE_MODE:
            case Mechanic.MECH_MISSILE_TANK:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPartySkill(int skillId) {
        if (skillId == Magician.HEAL) {
            // CUserLocal::DoActiveSkill_Heal
            return true;
        }
        // Usages of CUserLocal::DoActiveSkill_StatChange with dwTargetFlag & 2 (SCT_Party)
        switch (skillId) {
            // CUserLocal::DoActiveSkill
            case Warrior.RAGE:
            case Warrior.COMBAT_ORDERS:
            case Magician.MEDITATION_FP:
            case Magician.MEDITATION_IL:
            case Magician.BLESS:
            case Magician.DISPEL:
            case Magician.HOLY_SYMBOL:
            case Magician.HOLY_SHIELD:
            case Magician.RESURRECTION:
            case Bowman.SHARP_EYES_BM:
            case Bowman.SHARP_EYES_MM:
            case Thief.HASTE_NL:
            case Thief.HASTE_SHAD:
            case Thief.MESO_UP:
            case Thief.THORNS:
            case Pirate.SPEED_INFUSION:
            case Pirate.TIME_LEAP:
            case DawnWarrior.RAGE:
            case BlazeWizard.MEDITATION:
            case NightWalker.HASTE:
            case ThunderBreaker.SPEED_INFUSION:
            case Aran.COMBO_BARRIER:
            case Evan.MAGIC_SHIELD:
            case Evan.MAGIC_RESISTANCE:
            case Evan.BLESSING_OF_THE_ONYX:
            case Evan.SOUL_STONE:
            case WildHunter.SHARP_EYES_WH:
            case Warrior.MAPLE_WARRIOR_HERO:
            case Warrior.MAPLE_WARRIOR_PALADIN:
            case Warrior.MAPLE_WARRIOR_DRK:
            case Magician.MAPLE_WARRIOR_FP:
            case Magician.MAPLE_WARRIOR_IL:
            case Magician.MAPLE_WARRIOR_BISH:
            case Bowman.MAPLE_WARRIOR_BM:
            case Bowman.MAPLE_WARRIOR_MM:
            case Thief.MAPLE_WARRIOR_NL:
            case Thief.MAPLE_WARRIOR_SHAD:
            case Thief.MAPLE_WARRIOR_DB:
            case Pirate.MAPLE_WARRIOR_BUCC:
            case Pirate.MAPLE_WARRIOR_SAIR:
            case Aran.MAPLE_WARRIOR_ARAN:
            case Evan.MAPLE_WARRIOR_EVAN:
            case BattleMage.MAPLE_WARRIOR_BAM:
            case WildHunter.MAPLE_WARRIOR_WH:
            case Mechanic.MAPLE_WARRIOR_MECH:
            case WildHunter.JAGUAR_OSHI_DIGESTED: // CUserLocal::TryDoingSwallowBuff
                return true;
            default:
                return false;
        }
    }

    public static boolean isEncodePositionSkill(int skillId) {
        if (isAntiRepeatBuffSkill(skillId)) {
            // CUserLocal::SendSkillUseRequest
            return true;
        }
        if (isSummonSkill(skillId)) {
            // CUserLocal::DoActiveSkill_Summon
            return true;
        }
        switch (skillId) {
            case Thief.SMOKESCREEN, BattleMage.PARTY_SHIELD: // CUserLocal::DoActiveSkill_SmokeShell
            case Magician.MYSTIC_DOOR: // CUserLocal::DoActiveSkill_TownPortal
            case Evan.RECOVERY_AURA: // CUserLocal::DoActiveSkill_RecoverAura
            case Citizen.CALL_OF_THE_HUNTER: // CUserLocal::DoActiveSkill_SummonMonster
            case Mechanic.OPEN_PORTAL_GX_9: // CUserLocal::DoActiveSkill_OpenGate
                return true;
            default:
                return false;
        }
    }

    public static boolean isAntiRepeatBuffSkill(int skillId) {
        switch (skillId) {
            case 1001003:
            case 1101006:
            case 1111007:
            case 1121000:
            case 1201006:
            case 1211009:
            case 1211010:
            case 1221000:
            case 1301006:
            case 1301007:
            case 1311007:
            case 1321000:
            case 2101001:
            case 2101003:
            case 2121000:
            case 2201001:
            case 2201003:
            case 2221000:
            case 2301004:
            case 2311001:
            case 2311003:
            case 2321000:
            case 2321005:
            case 3121000:
            case 3121002:
            case 3221000:
            case 4101004:
            case 4111001:
            case 4121000:
            case 4201003:
            case 4221000:
            case 4311001:
            case 4341000:
            case 4341007:
            case 5111007:
            case 5121000:
            case 5121009:
            case 5211007:
            case 5221000:
            case 11001001:
            case 11101003:
            case 12101000:
            case 12101001:
            case 14101003:
            case 15111005:
            case 21121000:
            case 22141003:
            case 22171000:
            case 22181000:
            case 32111004:
            case 32121007:
            case 33121007:
            case 35111013:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSummonSkill(int skillId) {
        // Usages of CUserLocal::DoActiveSkill_Summon
        switch (skillId) {
            // CUserLocal::DoActiveSkill
            case Warrior.BEHOLDER:
            case Magician.IFRIT:
            case Magician.ELQUINES:
            case Magician.SUMMON_DRAGON:
            case Magician.BAHAMUT:
            case Bowman.PUPPET_BM:
            case Bowman.PUPPET_MM:
            case Bowman.SILVER_HAWK:
            case Bowman.GOLDEN_EAGLE:
            case Bowman.PHOENIX:
            case Bowman.FROSTPREY:
            case Thief.DARK_FLARE_NL:
            case Thief.DARK_FLARE_SHAD:
            case Thief.MIRRORED_TARGET:
            case Pirate.OCTOPUS:
            case Pirate.GAVIOTA:
            case Pirate.WRATH_OF_THE_OCTOPI:
            case DawnWarrior.SOUL:
            case BlazeWizard.FLAME:
            case BlazeWizard.IFRIT:
            case WindArcher.STORM:
            case WindArcher.PUPPET:
            case NightWalker.DARKNESS:
            case ThunderBreaker.LIGHTNING:
            case WildHunter.WILD_TRAP:
            case WildHunter.SILVER_HAWK:
            case Mechanic.ACCELERATION_BOT_EX_7:
            case Mechanic.SATELLITE:
            case Mechanic.SATELLITE_2:
            case Mechanic.SATELLITE_3:
            case Mechanic.ROCK_N_SHOCK:
            case Mechanic.HEALING_ROBOT_H_LX:
            case Mechanic.BOTS_N_TOTS:
            case Mechanic.AMPLIFIER_ROBOT_AF_11:
            case WildHunter.ITS_RAINING_MINES_HIDDEN: // CUserLocal::TryDoingMine
            case Mechanic.GIANT_ROBOT_SG_88: // CUserLocal::DoActiveSkill_RepeatSkill
                return true;
            default:
                return false;
        }
    }

    public static boolean isSummonMigrateSkill(int skillId) {
        switch (skillId) {
            case Warrior.BEHOLDER:
            case Magician.IFRIT:
            case Magician.ELQUINES:
            case Magician.SUMMON_DRAGON:
            case Magician.BAHAMUT:
            case Bowman.SILVER_HAWK:
            case Bowman.GOLDEN_EAGLE:
            case Bowman.PHOENIX:
            case Bowman.FROSTPREY:
            case Pirate.GAVIOTA:
            case DawnWarrior.SOUL:
            case BlazeWizard.FLAME:
            case BlazeWizard.IFRIT:
            case WindArcher.STORM:
            case WindArcher.PUPPET:
            case NightWalker.DARKNESS:
            case ThunderBreaker.LIGHTNING:
            case WildHunter.SILVER_HAWK:
            case Mechanic.SATELLITE:
            case Mechanic.SATELLITE_2:
            case Mechanic.SATELLITE_3:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSummonMultipleSkill(int skillId) {
        switch (skillId) {
            case BattleMage.SUMMON_REAPER_BUFF:
            case WildHunter.ITS_RAINING_MINES_HIDDEN:
            case Mechanic.ROCK_N_SHOCK:
            case Mechanic.HEALING_ROBOT_H_LX:
            case Mechanic.BOTS_N_TOTS_SUMMON:
                return true;
            default:
                return false;
        }
    }

    public static boolean isShootSkillNotUsingShootingWeapon(int skillId) {
        switch (skillId) {
            case Thief.TAUNT_NL:
            case Thief.TAUNT_SHAD:
            case Pirate.ENERGY_ORB:
            case DawnWarrior.SOUL_BLADE:
            case ThunderBreaker.SPARK:
            case ThunderBreaker.SHARK_WAVE:
            case Aran.COMBO_SMASH:
            case Aran.COMBO_FENRIR:
            case Aran.COMBO_TEMPEST:
            case WildHunter.JAGUAR_OSHI_ATTACK:
                return true;
            default:
                return false;
        }
    }

    public static boolean isShootSkillNotConsumingBullet(int skillId) {
        if (isShootSkillNotUsingShootingWeapon(skillId)) {
            return true;
        }
        switch (skillId) {
            case Bowman.POWER_KNOCKBACK_BM:
            case Bowman.POWER_KNOCKBACK_MM:
            case Thief.SHADOW_MESO:
            case WindArcher.STORM_BREAK:
            case NightWalker.VAMPIRE:
            case WildHunter.JAGUAR_RAWR:
            case Mechanic.FLAME_LAUNCHER:
            case Mechanic.GATLING_GUN:
            case Mechanic.ENHANCED_FLAME_LAUNCHER:
            case Mechanic.ENHANCED_GATLING_GUN:
            case Mechanic.MECH_SIEGE_MODE:
            case Mechanic.PUNCH_LAUNCHER:
            case Mechanic.MECH_MISSILE_TANK:
            case Mechanic.LASER_BLAST:
            case Mechanic.MECH_SIEGE_MODE_2:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMagicKeydownSkill(int skillId) {
        switch (skillId) {
            case 2121001:
            case 2221001:
            case 2321001:
            case 22121000:
            case 22151000:
                return true;
            default:
                return false;
        }
    }

    public static boolean isKeydownSkill(int skillId) {
        switch (skillId) {
            case 2121001:
            case 2221001:
            case 2321001:
            case 3121004:
            case 3221001:
            case 4341002:
            case 4341003:
            case 5101004:
            case 5201002:
            case 5221004:
            case 13111002:
            case 14111006:
            case 15101003:
            case 22121000:
            case 22151001:
            case 33101005:
            case 33121009:
            case 35001001:
            case 35101009:
                return true;
            default:
                return false;
        }
    }

    public static int getMaxGaugeTime(int skillId) {
        if (!isKeydownSkill(skillId)) {
            return 0;
        }
        switch (skillId) {
            case 2121001:
            case 2221001:
            case 2321001:
            case 5101004:
            case 5201002:
            case 14111006:
            case 15101003:
                return 1000;
            case 3121004:
            case 5221004:
            case 13111002:
            case 33121009:
            case 35001001:
            case 35101009:
                return 2000;
            case 3221001:
            case 33101005:
                return 900;
            case 4341002:
                return 600;
            case 4341003:
                return 1200;
            case 22121000:
            case 22151001:
                return 500;
            default:
                return 0;
        }
    }

    public static int getComboAbilitySkill(int jobId) {
        return jobId != 2000 ? Aran.COMBO_ABILITY : 20000017; // tutorial skill
    }

    public static int getRequiredComboCount(int skillId) {
        switch (skillId) {
            case Aran.COMBO_SMASH:
            case Aran.COMBO_DRAIN:
                return 100;
            case Aran.COMBO_FENRIR:
                return 100;
            case Aran.COMBO_TEMPEST:
            case Aran.COMBO_BARRIER:
                return 200;
            default:
                return 0;
        }
    }

    public static boolean isDualAddDamageExceptSkill(int skillId) {
        return skillId >= 4341002 && skillId <= 4341004;
    }

    public static boolean isJaguarMeleeAttackSkill(int skillId) {
        switch (skillId) {
            case 33101002:
            case 33101007:
            case 33111002:
            case 33111006:
            case 33121002:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNotSwallowableMob(int mobTemplateId) {
        final int mobType = mobTemplateId / 100000;
        return mobType >= 90 && (mobType <= 95 || mobType == 97) || mobTemplateId / 10000 == 999;
    }

    public static boolean isIgnoreMasterLevelForCommon(int skillId) {
        switch (skillId) {
            case 1120012:
            case 1220013:
            case 1320011:
            case 2120009:
            case 2220009:
            case 2320010:
            case 3120010:
            case 3120011:
            case 3220009:
            case 3220010:
            case 4120010:
            case 4220009:
            case 5120011:
            case 5220012:
            case 32120009:
            case 33120010:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSkillNeedMasterLevel(int skillId) {
        if (isIgnoreMasterLevelForCommon(skillId)) {
            return false;
        }
        final int jobId = skillId / 10000;
        if (JobConstants.isEvanJob(jobId)) {
            final int jobLevel = JobConstants.getJobLevel(jobId);
            return jobLevel == 9 || jobLevel == 10 || skillId == 22111001 || skillId == 22141002 || skillId == 22140000;
        }
        if (JobConstants.isDualJob(jobId)) {
            return JobConstants.getJobLevel(jobId) == 4 ||
                    skillId == 4311003 ||
                    skillId == 4321000 ||
                    skillId == 4331002 ||
                    skillId == 4331005;
        }
        if (jobId == 100 * (jobId / 100)) {
            return false;
        }
        return jobId % 10 == 2;
    }
}
