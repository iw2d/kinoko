package kinoko.world.job;

import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.world.job.cygnus.BlazeWizard;
import kinoko.world.job.cygnus.DawnWarrior;
import kinoko.world.job.cygnus.Noblesse;
import kinoko.world.job.cygnus.WindArcher;
import kinoko.world.job.explorer.*;
import kinoko.world.job.legend.Aran;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.Attack;
import kinoko.world.skill.HitInfo;
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

    public static void handleAttack(User user, Attack attack, SkillInfo si) {
        final int skillRoot = SkillConstants.getSkillRoot(attack.skillId);
        switch (Job.getById(skillRoot)) {
            case WARRIOR, FIGHTER, CRUSADER, HERO, PAGE, WHITE_KNIGHT, PALADIN, SPEARMAN, DRAGON_KNIGHT, DARK_KNIGHT -> {
                Warrior.handleAttack(user, attack, si);
            }
            case MAGICIAN, WIZARD_FP, MAGE_FP, ARCH_MAGE_FP, WIZARD_IL, MAGE_IL, ARCH_MAGE_IL, CLERIC, PRIEST, BISHOP -> {
                Magician.handleAttack(user, attack, si);
            }
            case ARCHER, HUNTER, RANGER, BOWMASTER, CROSSBOWMAN, SNIPER, MARKSMAN -> {
                Bowman.handleAttack(user, attack, si);
            }
            case ROGUE, ASSASSIN, HERMIT, NIGHT_LORD, BANDIT, CHIEF_BANDIT, SHADOWER, BLADE_RECRUIT, BLADE_ACOLYTE, BLADE_SPECIALIST, BLADE_LORD, BLADE_MASTER -> {
                Thief.handleAttack(user, attack, si);
            }
            case PIRATE, BRAWLER, MARAUDER, BUCCANEER, GUNSLINGER, OUTLAW, CORSAIR -> {
                Pirate.handleAttack(user, attack, si);
            }
            case DAWN_WARRIOR_1, DAWN_WARRIOR_2, DAWN_WARRIOR_3 -> {
                DawnWarrior.handleAttack(user, attack, si);
            }
            case BLAZE_WIZARD_1, BLAZE_WIZARD_2, BLAZE_WIZARD_3 -> {
                BlazeWizard.handleAttack(user, attack, si);
            }
            case WIND_ARCHER_1, WIND_ARCHER_2, WIND_ARCHER_3 -> {
                WindArcher.handleAttack(user, attack, si);
            }
            case ARAN_1, ARAN_2, ARAN_3, ARAN_4 -> {
                Aran.handleAttack(user, attack, si);
            }
            case EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10 -> {
                Evan.handleAttack(user, attack, si);
            }
            case BATTLE_MAGE_1, BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4 -> {
                BattleMage.handleAttack(user, attack, si);
            }
            case WILD_HUNTER_1, WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4 -> {
                WildHunter.handleAttack(user, attack, si);
            }
            case MECHANIC_1, MECHANIC_2, MECHANIC_3, MECHANIC_4 -> {
                Mechanic.handleAttack(user, attack, si);
            }
        }
    }

    public static void handleSkill(User user, Skill skill, SkillInfo si) {
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final TemporaryStatOption o1 = new TemporaryStatOption();
        final TemporaryStatOption o2 = new TemporaryStatOption();
        switch (skillId) {
            // Handle common skills
            case Beginner.RECOVERY:
            case Noblesse.RECOVERY:
            case Aran.RECOVERY:
            case Evan.RECOVER:
                o1.nOption = si.getValue(SkillStat.x, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.Regen, o1);
                return;
            case Beginner.NIMBLE_FEET:
            case Noblesse.NIMBLE_FEET:
            case Aran.AGILE_BODY:
            case Evan.NIMBLE_FEET:
                o1.nOption = si.getValue(SkillStat.speed, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.Speed, o1);
                return;
            case Citizen.INFILTRATE:
                o1.nOption = si.getValue(SkillStat.speed, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                o2.nOption = si.getValue(SkillStat.x, slv);
                o2.rOption = skillId;
                o2.tOption = si.getDuration(slv);
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Speed, o1,
                        CharacterTemporaryStat.Sneak, o2
                ));
                return;
            // Handle class specific skills
            default:
                final int skillRoot = SkillConstants.getSkillRoot(skill.skillId);
                switch (Job.getById(skillRoot)) {
                    case WARRIOR, FIGHTER, CRUSADER, HERO, PAGE, WHITE_KNIGHT, PALADIN, SPEARMAN, DRAGON_KNIGHT, DARK_KNIGHT -> {
                        Warrior.handleSkill(user, skill, si);
                        return;
                    }
                    case MAGICIAN, WIZARD_FP, MAGE_FP, ARCH_MAGE_FP, WIZARD_IL, MAGE_IL, ARCH_MAGE_IL, CLERIC, PRIEST, BISHOP -> {
                        Magician.handleSkill(user, skill, si);
                        return;
                    }
                    case ARCHER, HUNTER, RANGER, BOWMASTER, CROSSBOWMAN, SNIPER, MARKSMAN -> {
                        Bowman.handleSkill(user, skill, si);
                        return;
                    }
                    case ROGUE, ASSASSIN, HERMIT, NIGHT_LORD, BANDIT, CHIEF_BANDIT, SHADOWER, BLADE_RECRUIT, BLADE_ACOLYTE, BLADE_SPECIALIST, BLADE_LORD, BLADE_MASTER -> {
                        Thief.handleSkill(user, skill, si);
                        return;
                    }
                    case PIRATE, BRAWLER, MARAUDER, BUCCANEER, GUNSLINGER, OUTLAW, CORSAIR -> {
                        Pirate.handleSkill(user, skill, si);
                        return;
                    }
                    case DAWN_WARRIOR_1, DAWN_WARRIOR_2, DAWN_WARRIOR_3 -> {
                        DawnWarrior.handleSkill(user, skill, si);
                        return;
                    }
                    case BLAZE_WIZARD_1, BLAZE_WIZARD_2, BLAZE_WIZARD_3 -> {
                        BlazeWizard.handleSkill(user, skill, si);
                        return;
                    }
                    case WIND_ARCHER_1, WIND_ARCHER_2, WIND_ARCHER_3 -> {
                        WindArcher.handleSkill(user, skill, si);
                        return;
                    }
                    case ARAN_1, ARAN_2, ARAN_3, ARAN_4 -> {
                        Aran.handleSkill(user, skill, si);
                        return;
                    }
                    case EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10 -> {
                        Evan.handleSkill(user, skill, si);
                        return;
                    }
                    case BATTLE_MAGE_1, BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4 -> {
                        BattleMage.handleSkill(user, skill, si);
                        return;
                    }
                    case WILD_HUNTER_1, WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4 -> {
                        WildHunter.handleSkill(user, skill, si);
                        return;
                    }
                    case MECHANIC_1, MECHANIC_2, MECHANIC_3, MECHANIC_4 -> {
                        Mechanic.handleSkill(user, skill, si);
                        return;
                    }
                }
        }
        log.error("Unhandled skill {}", skill.skillId);
    }

    public static void handleHit(User user, HitInfo hitInfo) {
    }
}
