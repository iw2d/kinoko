package kinoko.world.job;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.InPacket;
import kinoko.world.job.cygnus.*;
import kinoko.world.job.explorer.*;
import kinoko.world.job.legend.Aran;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillConstants;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public final class JobHandler {
    private static final Logger log = LogManager.getLogger(JobHandler.class);

    public static void handleAttack(User user, Attack attack) {
        final int skillRoot = SkillConstants.getSkillRoot(attack.skillId);
        switch (Job.getById(skillRoot)) {
            case WARRIOR, FIGHTER, CRUSADER, HERO, PAGE, WHITE_KNIGHT, PALADIN, SPEARMAN, DRAGON_KNIGHT, DARK_KNIGHT -> {
                Warrior.handleAttack(user, attack);
            }
            case MAGICIAN, WIZARD_FP, MAGE_FP, ARCH_MAGE_FP, WIZARD_IL, MAGE_IL, ARCH_MAGE_IL, CLERIC, PRIEST, BISHOP -> {
                Magician.handleAttack(user, attack);
            }
            case ARCHER, HUNTER, RANGER, BOWMASTER, CROSSBOWMAN, SNIPER, MARKSMAN -> {
                Bowman.handleAttack(user, attack);
            }
            case ROGUE, ASSASSIN, HERMIT, NIGHT_LORD, BANDIT, CHIEF_BANDIT, SHADOWER, BLADE_RECRUIT, BLADE_ACOLYTE, BLADE_SPECIALIST, BLADE_LORD, BLADE_MASTER -> {
                Thief.handleAttack(user, attack);
            }
            case PIRATE, BRAWLER, MARAUDER, BUCCANEER, GUNSLINGER, OUTLAW, CORSAIR -> {
                Pirate.handleAttack(user, attack);
            }
            case DAWN_WARRIOR_1, DAWN_WARRIOR_2, DAWN_WARRIOR_3 -> {
                DawnWarrior.handleAttack(user, attack);
            }
            case BLAZE_WIZARD_1, BLAZE_WIZARD_2, BLAZE_WIZARD_3 -> {
                BlazeWizard.handleAttack(user, attack);
            }
            case WIND_ARCHER_1, WIND_ARCHER_2, WIND_ARCHER_3 -> {
                WindArcher.handleAttack(user, attack);
            }
            case ARAN_1, ARAN_2, ARAN_3, ARAN_4 -> {
                Aran.handleAttack(user, attack);
            }
            case EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10 -> {
                Evan.handleAttack(user, attack);
            }
            case BATTLE_MAGE_1, BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4 -> {
                BattleMage.handleAttack(user, attack);
            }
            case WILD_HUNTER_1, WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4 -> {
                WildHunter.handleAttack(user, attack);
            }
            case MECHANIC_1, MECHANIC_2, MECHANIC_3, MECHANIC_4 -> {
                Mechanic.handleAttack(user, attack);
            }
        }
    }

    public static void handleSkill(User user, Skill skill, InPacket inPacket) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;
        switch (skillId) {
            // BEGINNER SKILLS -----------------------------------------------------------------------------------------
            case Beginner.RECOVERY:
            case Noblesse.RECOVERY:
            case Aran.RECOVERY:
            case Evan.RECOVER:
                user.setTemporaryStat(CharacterTemporaryStat.Regen, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case Beginner.NIMBLE_FEET:
            case Noblesse.NIMBLE_FEET:
            case Aran.AGILE_BODY:
            case Evan.NIMBLE_FEET:
                user.setTemporaryStat(CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)));
                return;
            case Citizen.INFILTRATE:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Sneak, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv))
                ));
                return;
            case Beginner.ECHO_OF_HERO:
            case Noblesse.ECHO_OF_HERO:
            case Aran.ECHO_OF_HERO:
            case Evan.HEROS_ECHO:
            case Citizen.HEROS_ECHO:
                user.setTemporaryStat(CharacterTemporaryStat.MaxLevelBuff, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;

            // OTHER COMMON SKILLS -------------------------------------------------------------------------------------
            case Warrior.FIRE_CHARGE:
            case Warrior.ICE_CHARGE:
            case Warrior.LIGHTNING_CHARGE:
            case Warrior.DIVINE_CHARGE:
            case DawnWarrior.SOUL_CHARGE:
            case ThunderBreaker.LIGHTNING_CHARGE:
            case Aran.SNOW_CHARGE:
                user.setTemporaryStat(CharacterTemporaryStat.WeaponCharge, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case Thief.DARK_SIGHT:
            case NightWalker.DARK_SIGHT:
                if (slv == si.getMaxLevel()) {
                    user.setTemporaryStat(CharacterTemporaryStat.DarkSight, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                } else {
                    user.setTemporaryStat(Map.of(
                            CharacterTemporaryStat.DarkSight, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                            CharacterTemporaryStat.Slow, TemporaryStatOption.of(100 - si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                    ));
                }
                return;
            case Thief.HASTE_NL:
            case Thief.HASTE_SHAD:
            case Thief.SELF_HASTE:
            case NightWalker.HASTE:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Jump, TemporaryStatOption.of(si.getValue(SkillStat.jump, slv), skillId, si.getDuration(slv))
                ));
                return;
            case Thief.FLASH_JUMP_NL:
            case Thief.FLASH_JUMP_SHAD:
            case Thief.FLASH_JUMP_DB:
            case NightWalker.FLASH_JUMP:
                return;

            // WEAPON BOOSTER ------------------------------------------------------------------------------------------
            case Warrior.WEAPON_BOOSTER_HERO:
            case Warrior.WEAPON_BOOSTER_PALADIN:
            case Warrior.WEAPON_BOOSTER_DRK:
            case Magician.SPELL_BOOSTER_FP:
            case Magician.SPELL_BOOSTER_IL:
            case Bowman.BOW_BOOSTER:
            case Bowman.CROSSBOW_BOOSTER:
            case Thief.CLAW_BOOSTER:
            case Thief.DAGGER_BOOSTER:
            case Thief.KATARA_BOOSTER:
            case Pirate.KNUCKLE_BOOSTER:
            case Pirate.GUN_MASTERY:
            case DawnWarrior.SWORD_BOOSTER:
            case BlazeWizard.SPELL_BOOSTER:
            case WindArcher.BOW_BOOSTER:
            case NightWalker.CLAW_BOOSTER:
            case ThunderBreaker.KNUCKLE_BOOSTER:
            case Aran.POLEARM_BOOSTER:
            case Evan.MAGIC_BOOSTER:
            case BattleMage.STAFF_BOOST:
            case WildHunter.CROSSBOW_BOOSTER:
            case Mechanic.MECHANIC_RAGE:
                user.setTemporaryStat(CharacterTemporaryStat.Booster, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;

            // MAPLE WARRIOR -------------------------------------------------------------------------------------------
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
                user.setTemporaryStat(CharacterTemporaryStat.BasicStatUp, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;

            // HERO'S WILL ---------------------------------------------------------------------------------------------
            case Warrior.HEROS_WILL_HERO:
            case Warrior.HEROS_WILL_PALADIN:
            case Warrior.HEROS_WILL_DRK:
            case Magician.HEROS_WILL_FP:
            case Magician.HEROS_WILL_IL:
            case Magician.HEROS_WILL_BISH:
            case Bowman.HEROS_WILL_BM:
            case Bowman.HEROS_WILL_MM:
            case Thief.HEROS_WILL_NL:
            case Thief.HEROS_WILL_SHAD:
            case Thief.HEROS_WILL_DB:
            case Pirate.PIRATES_RAGE:
            case Pirate.HEROS_WILL_SAIR:
            case Aran.HEROS_WILL_ARAN:
            case Evan.HEROS_WILL_EVAN:
            case BattleMage.HEROS_WILL_BAM:
            case WildHunter.HEROS_WILL_WH:
            case Mechanic.HEROS_WILL_MECH:
                // TODO
                return;

            // CLASS SPECIFIC SKILLS -----------------------------------------------------------------------------------
            default:
                final int skillRoot = SkillConstants.getSkillRoot(skill.skillId);
                switch (Job.getById(skillRoot)) {
                    case WARRIOR, FIGHTER, CRUSADER, HERO, PAGE, WHITE_KNIGHT, PALADIN, SPEARMAN, DRAGON_KNIGHT, DARK_KNIGHT -> {
                        Warrior.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case MAGICIAN, WIZARD_FP, MAGE_FP, ARCH_MAGE_FP, WIZARD_IL, MAGE_IL, ARCH_MAGE_IL, CLERIC, PRIEST, BISHOP -> {
                        Magician.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case ARCHER, HUNTER, RANGER, BOWMASTER, CROSSBOWMAN, SNIPER, MARKSMAN -> {
                        Bowman.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case ROGUE, ASSASSIN, HERMIT, NIGHT_LORD, BANDIT, CHIEF_BANDIT, SHADOWER, BLADE_RECRUIT, BLADE_ACOLYTE, BLADE_SPECIALIST, BLADE_LORD, BLADE_MASTER -> {
                        Thief.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case PIRATE, BRAWLER, MARAUDER, BUCCANEER, GUNSLINGER, OUTLAW, CORSAIR -> {
                        Pirate.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case DAWN_WARRIOR_1, DAWN_WARRIOR_2, DAWN_WARRIOR_3 -> {
                        DawnWarrior.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case BLAZE_WIZARD_1, BLAZE_WIZARD_2, BLAZE_WIZARD_3 -> {
                        BlazeWizard.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case WIND_ARCHER_1, WIND_ARCHER_2, WIND_ARCHER_3 -> {
                        WindArcher.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case ARAN_1, ARAN_2, ARAN_3, ARAN_4 -> {
                        Aran.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10 -> {
                        Evan.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case BATTLE_MAGE_1, BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4 -> {
                        BattleMage.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case WILD_HUNTER_1, WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4 -> {
                        WildHunter.handleSkill(user, skill, inPacket);
                        return;
                    }
                    case MECHANIC_1, MECHANIC_2, MECHANIC_3, MECHANIC_4 -> {
                        Mechanic.handleSkill(user, skill, inPacket);
                        return;
                    }
                }
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}
