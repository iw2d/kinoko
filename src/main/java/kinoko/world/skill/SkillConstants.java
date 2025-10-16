package kinoko.world.skill;

import kinoko.meta.SkillId;
import kinoko.meta.SkillTargetFlags;
import kinoko.provider.skill.ElementAttribute;
import kinoko.util.Rect;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.job.legend.Aran;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.util.List;
import java.util.Optional;

public final class SkillConstants {
    public static final List<SkillId> SECONDARY_STAT_SKILLS = List.of(SkillId.NW1_NIMBLE_BODY, SkillId.NW1_NIMBLE_BODY, SkillId.PIRATE_BULLET_TIME, // PirateQuickMotion
            SkillId.TB1_QUICK_MOTION, // StrikerQuickMotion
            SkillId.EVAN1_DRAGON_SOUL, SkillId.BEGINNER_BLESSING_OF_THE_FAIRY, SkillId.NOBLESSE_BLESSING_OF_THE_FAIRY, SkillId.CITIZEN_BLESSING_OF_THE_FAIRY, SkillId.EVANBEGINNER_BLESSING_OF_THE_FAIRY, SkillId.LEGEND_BLESSING_OF_THE_FAIRY);
    public static final List<Integer> WILD_HUNTER_JAGUARS = List.of(1932015, 1932030, 1932031, 1932032, 1932033, 1932036);
    public static final int MECHANIC_VEHICLE = 1932016;
    public static final int BATTLESHIP_VEHICLE = 1932000;
    public static final int BATTLESHIP_DURABILITY = 5221999;
    public static final int ENERGY_CHARGE_MAX = 10000;
    public static final Rect DARK_FLARE_RANGE = Rect.of(-400, -200, 400, 200);
    public static final Rect MONSTER_BOMB_RANGE = Rect.of(-220, -150, 220, 0);


    public static SkillId getNoviceSkillAsRace(SkillId skillId, int jobId) {
        if (skillId == SkillId.BEGINNER_FOLLOW_THE_LEAD) {
            return switch (JobConstants.getNoviceSkillRootFromJob(jobId)) {
                case 1000 -> SkillId.NOBLESSE_FOLLOW_THE_LEAD;
                case 2000 -> SkillId.LEGEND_FOLLOW_THE_LEAD;
                case 2001 -> SkillId.EVANBEGINNER_FOLLOW_THE_LEAD;
                case 3000 -> SkillId.CITIZEN_FOLLOW_THE_LEAD;
                default -> SkillId.BEGINNER_FOLLOW_THE_LEAD;
            };
        }
        if (JobConstants.isEvanJob(jobId)) {
            //return skillId + 20010000;
            return SkillId.NONE;// TODO
        } else {
            //return skillId + 10000000 * (jobId / 1000);
            return SkillId.NONE;//TODO
        }
    }

    public static SkillId getItemBonusRateSkill(int jobId) {
        if (JobConstants.isNightLordJob(jobId)) {
            return SkillId.HERMIT_ALCHEMIST;
        } else if (JobConstants.isNightWalkerJob(jobId)) {
            return SkillId.NW3_ALCHEMIST;
        } else if (JobConstants.isResistanceJob(jobId)) {
            return SkillId.CITIZEN_POTION_MASTERY;
        }
        return SkillId.NONE;
    }

    public static SkillId getEnhancedBasicsSkill(int jobId) {
        if (JobConstants.isHeroJob(jobId)) {
            return SkillId.FIGHTER_ENHANCED_BASICS;
        } else if (JobConstants.isPaladinJob(jobId)) {
            return SkillId.PAGE_ENHANCED_BASICS;
        } else if (JobConstants.isDarkKnightJob(jobId)) {
            return SkillId.SPEARNMAN_ENHANCED_BASICS;
        } else if (JobConstants.isBowmasterJob(jobId)) {
            return SkillId.HUNTER_ENHANCED_BASICS;
        } else if (JobConstants.isMarksmanJob(jobId)) {
            return SkillId.CROSSBOWMAN_ENHANCED_BASICS;
        } else {
            return SkillId.NONE;
        }
    }

    public static SkillId getComboAttackSkill(int jobId) {
        return JobConstants.isCygnusJob(jobId) ? SkillId.DW3_COMBO_ATTACK : SkillId.CRUSADER_COMBO_ATTACK;
    }

    public static SkillId getAdvancedComboSkill(int jobId) {
        return JobConstants.isCygnusJob(jobId) ? SkillId.HERO_ADVANCED_COMBO_ATTACK : SkillId.DW3_ADVANCED_COMBO;
    }

    public static SkillId getMpEaterSkill(int jobId) {
        if (JobConstants.isFirePoisonJob(jobId)) {
            return SkillId.FP1_MP_EATER;
        } else if (JobConstants.isIceLightningJob(jobId)) {
            return SkillId.IL1_MP_EATER;
        } else if (JobConstants.isBishopJob(jobId)) {
            return SkillId.CLERIC_MP_EATER;
        } else {
            return SkillId.NONE;
        }
    }

    public static SkillId getBuffMasterySkill(int jobId) {
        if (JobConstants.isFirePoisonJob(jobId)) {
            return SkillId.FP3_BUFF_MASTERY;
        } else if (JobConstants.isIceLightningJob(jobId)) {
            return SkillId.IL3_BUFF_MASTERY;
        } else if (JobConstants.isBishopJob(jobId)) {
            return SkillId.BISHOP_BUFF_MASTERY;
        } else {
            return SkillId.NONE;
        }
    }

    public static SkillId getAmplificationSkill(int jobId) {
        if (JobConstants.isFirePoisonJob(jobId)) {
            return SkillId.FP2_ELEMENT_AMPLIFICATION;
        } else if (JobConstants.isIceLightningJob(jobId)) {
            return SkillId.IL2_ELEMENT_AMPLIFICATION;
        } else if (JobConstants.isBlazeWizardJob(jobId)) {
            return SkillId.BW3_ELEMENT_AMPLIFICATION;
        } else if (JobConstants.isEvanJob(jobId)) {
            return SkillId.EVAN7_MAGIC_AMPLIFICATION;
        } else {
            return SkillId.NONE;
        }
    }

    public static SkillId getMortalBlowSkill(int jobId) {
        if (JobConstants.isBowmasterJob(jobId)) {
            return SkillId.RANGER_MORTAL_BLOW;
        } else if (JobConstants.isMarksmanJob(jobId)) {
            return SkillId.SNIPER_MORTAL_BLOW;
        } else {
            return SkillId.NONE;
        }
    }

    public static SkillId getVenomSkill(int jobId) {
        if (JobConstants.isNightLordJob(jobId)) {
            return SkillId.NIGHTLORD_VENOMOUS_STAR;
        } else if (JobConstants.isNightWalkerJob(jobId)) {
            return SkillId.NW3_VENOM;
        } else if (JobConstants.isShadowerJob(jobId)) {
            return SkillId.SHADOWER_VENOMOUS_STAB;
        } else if (JobConstants.isDualJob(jobId)) {
            return SkillId.DB5_VENOM;
        } else {
            return SkillId.NONE;
        }
    }

    public static SkillId getEnergyChargeSkill(int jobId) {
        return JobConstants.isCygnusJob(jobId) ? SkillId.TB2_ENERGY_CHARGE : SkillId.MARAUDER_ENERGY_CHARGE;
    }

    public static SkillId getPiratesRevengeSkill(int jobId) {
        if (JobConstants.isBuccaneerJob(jobId)) {
            return SkillId.BUCCANEER_PIRATES_REVENGE;
        } else if (JobConstants.isCorsairJob(jobId)) {
            return SkillId.CORSAIR_PIRATES_REVENGE;
        } else {
            return SkillId.NONE;
        }
    }

    public static ElementAttribute getElementByWeaponChargeSkill(SkillId skillId) {
        return switch (skillId) {
            case SkillId.WK_FIRE_CHARGE -> ElementAttribute.FIRE;
            case SkillId.WK_ICE_CHARGE, SkillId.ARAN3_SNOW_CHARGE -> ElementAttribute.ICE;
            case SkillId.WK_LIGHTNING_CHARGE, SkillId.TB2_LIGHTNING_CHARGE -> ElementAttribute.LIGHT;
            case SkillId.PALADIN_DIVINE_CHARGE, SkillId.DW3_SOUL_CHARGE -> ElementAttribute.HOLY;
            default -> ElementAttribute.PHYSICAL;
        };
    }

    public static CharacterTemporaryStat getStatByAuraSkill(SkillId skillId) {
        return switch (skillId) {
            case SkillId.DARK_AURA, SkillId.ADVANCED_DARK_AURA -> CharacterTemporaryStat.DarkAura;
            case SkillId.BLUE_AURA, SkillId.ADVANCED_BLUE_AURA -> CharacterTemporaryStat.BlueAura;
            case SkillId.YELLOW_AURA, SkillId.ADVANCED_YELLOW_AURA -> CharacterTemporaryStat.YellowAura;
            default -> null;
        };
    }

    public static boolean isBeginnerSpAddableSkill(SkillId skillId) {
        final int skillRoot = skillId.getRoot();
        if (!JobConstants.isBeginnerJob(skillRoot)) {
            return false;
        } else if (skillRoot == Job.CITIZEN.getJobId()) {
            return skillId == SkillId.CITIZEN_POTION_MASTERY || skillId == SkillId.CITIZEN_CRYSTAL_THROW || skillId == SkillId.CITIZEN_INFILTRATE;
        }
        final int skillType = skillId.getType();
        return skillType == 1000 || skillType == 1001 || skillType == 1002;
    }


    public static boolean isTeleportSkill(SkillId skillId) {
        return switch (skillId) {
            case SkillId.FP1_TELEPORT, SkillId.IL1_TELEPORT, SkillId.CLERIC_TELEPORT, SkillId.BW2_TELEPORT,
                 SkillId.EVAN2_TELEPORT, SkillId.TELEPORT -> true;
            default -> false;
        };
    }

    public static boolean isThrowBombSkill(SkillId skillId) {
        return switch (skillId) {
            case SkillId.DB5_MONSTER_BOMB, SkillId.GUNSLINGER_GRENADE, SkillId.NW3_POISON_BOMB -> true;
            default -> false;
        };
    }

    public static boolean isNoConsumeAttack(SkillId skillId) {
        if (SkillConstants.isThrowBombSkill(skillId)) {
            return true;
        }
        return switch (skillId) {
            case SkillId.DB3_TORNADO_SPIN_ATTACK, SkillId.DB5_CHAINS_OF_HELL, SkillId.TWISTER_SPIN,
                 SkillId.MECH3_MECH_SIEGE_MODE, SkillId.MECH4_MECH_MISSILE_TANK, SkillId.MECH4_GIANT_ROBOT_SG88,
                 SkillId.MECH4_MECH_SIEGE_MODE -> true;
            default -> false;
        };
    }

    public static boolean isNoCooltimeSkill(SkillId skillId) {
        // Cooltime for these skills require special handling
        return switch (skillId) {
            case SkillId.CORSAIR_BATTLESHIP, SkillId.MECH3_ROCK_N_SHOCK, SkillId.MECH4_SATELLITE_SAFETY -> true;
            default -> false;
        };
    }

    public static boolean isPartySkill(SkillId skillId) {
        return skillId.hasTargetFlag(SkillTargetFlags.Party);
    }

    public static boolean isEncodePositionSkill(SkillId skillId) {
        if (isAntiRepeatBuffSkill(skillId)) {
            // CUserLocal::SendSkillUseRequest
            return true;
        }
        if (isSummonSkill(skillId)) {
            // CUserLocal::DoActiveSkill_Summon
            return true;
        }
        return switch (skillId) { // CUserLocal::DoActiveSkill_SmokeShell
            // CUserLocal::DoActiveSkill_TownPortal
            // CUserLocal::DoActiveSkill_RecoverAura
            // CUserLocal::DoActiveSkill_SummonMonster
            case SkillId.SHADOWER_SMOKESCREEN, SkillId.PARTY_SHIELD, SkillId.PRIEST_MYSTIC_DOOR,
                 SkillId.EVAN8_RECOVERY_AURA, SkillId.CITIZEN_CALL_OF_THE_HUNTER,
                 SkillId.MECH2_OPEN_PORTAL_GX9 -> // CUserLocal::DoActiveSkill_OpenGate
                    true;
            default -> false;
        };
    }

    public static boolean isAntiRepeatBuffSkill(SkillId skillId) {
        return switch (skillId.getId()) {
            case 1001003, 1101006, 1111007, 1121000, 1201006, 1211009, 1211010, 1221000, 1301006, 1301007, 1311007,
                 1321000, 2101001, 2101003, 2121000, 2201001, 2201003, 2221000, 2301004, 2311001, 2311003, 2321000,
                 2321005, 3121000, 3121002, 3221000, 4101004, 4111001, 4121000, 4201003, 4221000, 4311001, 4341000,
                 4341007, 5111007, 5121000, 5121009, 5211007, 5221000, 11001001, 11101003, 12101000, 12101001, 14101003,
                 15111005, 21121000, 22141003, 22171000, 22181000, 32111004, 32121007, 33121007, 35111013 -> true;
            default -> false;
        };
    }

    public static boolean isSummonSkill(SkillId skillId) {
        // Usages of CUserLocal::DoActiveSkill_Summon
        /*switch (skillId) {
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
        }*/
        //TODO
        return false;
    }

    public static boolean isSummonMigrateSkill(SkillId skillId) {
        /*switch (skillId) {
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
        }*/

        //TODO
        return false;
    }

    public static boolean isSummonMultipleSkill(SkillId skillId) {
        return switch (skillId) {
            case SkillId.SUMMON_REAPER_BUFF, SkillId.WH2_ITS_RAINING_MINES, SkillId.MECH3_ROCK_N_SHOCK,
                 SkillId.MECH3_HEALING_ROBOT_HLX, SkillId.MECH4_BOTS_N_TOTS -> true;
            default -> false;
        };
    }

    public static boolean isShootSkillNotUsingShootingWeapon(SkillId skillId) {
        return switch (skillId) {
            case SkillId.NIGHTLORD_TAUNT, SkillId.SHADOWER_TAUNT, SkillId.BUCCANEER_ENERGY_ORB, SkillId.DW2_SOUL_BLADE,
                 SkillId.TB3_SPARK, SkillId.TB3_SHARK_WAVE, SkillId.ARAN2_COMBO_SMASH, SkillId.ARAN3_COMBO_FENRIR,
                 SkillId.ARAN4_COMBO_TEMPEST, SkillId.WH2_JAGUAROSHI_2 -> true;
            default -> false;
        };
    }

    public static boolean isShootSkillNotConsumingBullet(SkillId skillId) {
        //TODO
        return false;
        /*if (isShootSkillNotUsingShootingWeapon(skillId)) {
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
        }*/
    }

    public static boolean isMagicKeydownSkill(SkillId skillId) {
        return switch (skillId.getId()) {
            case 2121001, 2221001, 2321001, 22121000, 22151001 -> true;
            default -> false;
        };
    }

    public static boolean isKeydownSkill(SkillId skillId) {
        return switch (skillId.getId()) {
            case 2121001, 2221001, 2321001, 3121004, 3221001, 4341002, 4341003, 5101004, 5201002, 5221004, 13111002,
                 14111006, 15101003, 22121000, 22151001, 33101005, 33121009, 35001001, 35101009 -> true;
            default -> false;
        };
    }

    public static int getMaxGaugeTime(SkillId skillId) {
        if (!isKeydownSkill(skillId)) {
            return 0;
        }
        return switch (skillId.getId()) {
            case 2121001, 2221001, 2321001, 5101004, 5201002, 14111006, 15101003 -> 1000;
            case 3121004, 5221004, 13111002, 33121009, 35001001, 35101009 -> 2000;
            case 3221001, 33101005 -> 900;
            case 4341002 -> 600;
            case 4341003 -> 1200;
            case 22121000, 22151001 -> 500;
            default -> 0;
        };
    }

    public static SkillId getComboAbilitySkill(int jobId) {
        return jobId != 2000 ? SkillId.ARAN1_COMBO_ABILITY : SkillId.LEGEND_TUTORIAL_SKILL_3; // tutorial skill
    }

    public static int getRequiredComboCount(SkillId skillId) {
        return switch (skillId) {
            case SkillId.ARAN2_COMBO_SMASH, SkillId.ARAN2_COMBO_DRAIN -> 100;
            case SkillId.ARAN3_COMBO_FENRIR -> 100;
            case SkillId.ARAN4_COMBO_TEMPEST, SkillId.ARAN4_COMBO_BARRIER -> 200;
            default -> 0;
        };
    }

    public static boolean isDualAddDamageExceptSkill(SkillId skillId) {
        return skillId.getId() >= 4341002 && skillId.getId() <= 4341004;
    }

    public static boolean isJaguarMeleeAttackSkill(SkillId skillId) {
        return switch (skillId.getId()) {
            case 33101002, 33101007, 33111002, 33111006, 33121002 -> true;
            default -> false;
        };
    }

    public static boolean isNotSwallowableMob(int mobTemplateId) {
        final int mobType = mobTemplateId / 100000;
        return mobType >= 90 && (mobType <= 95 || mobType == 97) || mobTemplateId / 10000 == 999;
    }

    public static boolean isIgnoreMasterLevelForCommon(SkillId skillId) {
        return switch (skillId.getId()) {
            case 1120012, 1220013, 1320011, 2120009, 2220009, 2320010, 3120010, 3120011, 3220009, 3220010, 4120010,
                 4220009, 5120011, 5220012, 32120009, 33120010 -> true;
            default -> false;
        };
    }

    public static boolean isSkillNeedMasterLevel(SkillId skillId) {
        if (isIgnoreMasterLevelForCommon(skillId)) {
            return false;
        }
        final int jobId = skillId.getJobId();
        if (JobConstants.isEvanJob(jobId)) {
            final int jobLevel = JobConstants.getJobLevel(jobId);
            return jobLevel == 9 || jobLevel == 10 || skillId.getId() == 22111001 || skillId.getId() == 22141002 || skillId.getId() == 22140000;
        }
        if (JobConstants.isDualJob(jobId)) {
            return JobConstants.getJobLevel(jobId) == 4 || skillId.getId() == 4311003 || skillId.getId() == 4321000 || skillId.getId() == 4331002 || skillId.getId() == 4331005;
        }
        if (jobId == 100 * (jobId / 100)) {
            return false;
        }
        return jobId % 10 == 2;
    }
}
