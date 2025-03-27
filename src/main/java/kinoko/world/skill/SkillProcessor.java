package kinoko.world.skill;

import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Locked;
import kinoko.util.Rect;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAssistType;
import kinoko.world.field.summoned.SummonedMoveAbility;
import kinoko.world.item.BodyPart;
import kinoko.world.item.Item;
import kinoko.world.job.Job;
import kinoko.world.job.cygnus.*;
import kinoko.world.job.explorer.*;
import kinoko.world.job.legend.Aran;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class SkillProcessor {
    protected static final Logger log = LogManager.getLogger(SkillProcessor.class);


    // PROCESS ATTACK --------------------------------------------------------------------------------------------------

    public static void processAttack(Locked<User> locked, Locked<Mob> lockedMob, Attack attack, int delay) {
        final User user = locked.get();
        final Mob mob = lockedMob.get();
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case Warrior.PANIC:
            case DawnWarrior.PANIC:
                if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                    mob.setTemporaryStat(MobTemporaryStat.Blind, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)), delay);
                }
                return;
            case Warrior.COMA:
            case DawnWarrior.COMA:
                if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                    mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                return;
            case Magician.POISON_BREATH:
            case Magician.FIRE_DEMON:
            case Magician.ICE_DEMON:
            case Magician.METEOR_SHOWER:
            case Magician.BLIZZARD:
            case BlazeWizard.METEOR_SHOWER:
                mob.setBurnedInfo(BurnedInfo.from(user, si, slv, mob), delay);
                return;
            case Magician.TELEPORT_MASTERY_FP:
            case Magician.TELEPORT_MASTERY_IL:
            case Magician.TELEPORT_MASTERY_BISH:
            case BattleMage.TELEPORT_MASTERY:
                if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.subProp, slv))) {
                    mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)), delay);
                }
                return;
            case Thief.DISORDER:
            case NightWalker.DISORDER:
                if (!mob.isBoss()) {
                    mob.setTemporaryStat(Map.of(
                            MobTemporaryStat.PAD, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                            MobTemporaryStat.PDR, MobStatOption.of(si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                    ), delay);
                }
                return;
        }

        final int skillRoot = SkillConstants.getSkillRoot(attack.skillId);
        switch (Job.getById(skillRoot)) {
            case WARRIOR, FIGHTER, CRUSADER, HERO, PAGE, WHITE_KNIGHT, PALADIN, SPEARMAN, DRAGON_KNIGHT, DARK_KNIGHT -> {
                Warrior.handleAttack(user, mob, attack, delay);
            }
            case MAGICIAN, WIZARD_FP, MAGE_FP, ARCH_MAGE_FP, WIZARD_IL, MAGE_IL, ARCH_MAGE_IL, CLERIC, PRIEST, BISHOP -> {
                Magician.handleAttack(user, mob, attack, delay);
            }
            case ARCHER, HUNTER, RANGER, BOWMASTER, CROSSBOWMAN, SNIPER, MARKSMAN -> {
                Bowman.handleAttack(user, mob, attack, delay);
            }
            case ROGUE, ASSASSIN, HERMIT, NIGHT_LORD, BANDIT, CHIEF_BANDIT, SHADOWER, BLADE_RECRUIT, BLADE_ACOLYTE, BLADE_SPECIALIST, BLADE_LORD, BLADE_MASTER -> {
                Thief.handleAttack(user, mob, attack, delay);
            }
            case PIRATE, BRAWLER, MARAUDER, BUCCANEER, GUNSLINGER, OUTLAW, CORSAIR -> {
                Pirate.handleAttack(user, mob, attack, delay);
            }
            case DAWN_WARRIOR_1, DAWN_WARRIOR_2, DAWN_WARRIOR_3 -> {
                DawnWarrior.handleAttack(user, mob, attack, delay);
            }
            case BLAZE_WIZARD_1, BLAZE_WIZARD_2, BLAZE_WIZARD_3 -> {
                BlazeWizard.handleAttack(user, mob, attack, delay);
            }
            case WIND_ARCHER_1, WIND_ARCHER_2, WIND_ARCHER_3 -> {
                WindArcher.handleAttack(user, mob, attack, delay);
            }
            case NIGHT_WALKER_1, NIGHT_WALKER_2, NIGHT_WALKER_3 -> {
                NightWalker.handleAttack(user, mob, attack, delay);
            }
            case THUNDER_BREAKER_1, THUNDER_BREAKER_2, THUNDER_BREAKER_3 -> {
                ThunderBreaker.handleAttack(user, mob, attack, delay);
            }
            case ARAN_1, ARAN_2, ARAN_3, ARAN_4 -> {
                Aran.handleAttack(user, mob, attack, delay);
            }
            case EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10 -> {
                Evan.handleAttack(user, mob, attack, delay);
            }
            case BATTLE_MAGE_1, BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4 -> {
                BattleMage.handleAttack(user, mob, attack, delay);
            }
            case WILD_HUNTER_1, WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4 -> {
                WildHunter.handleAttack(user, mob, attack, delay);
            }
            case MECHANIC_1, MECHANIC_2, MECHANIC_3, MECHANIC_4 -> {
                Mechanic.handleAttack(user, mob, attack, delay);
            }
        }
    }


    // PROCESS SKILL ---------------------------------------------------------------------------------------------------

    public static void processSkill(Locked<User> locked, Skill skill) {
        final User user = locked.get();
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            // BEGINNER SKILLS -----------------------------------------------------------------------------------------
            case Beginner.RECOVERY:
            case Noblesse.RECOVERY:
            case Aran.RECOVERY:
            case Evan.RECOVER:
                user.setTemporaryStat(CharacterTemporaryStat.Regen, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                user.getSkillManager().setSkillSchedule(skillId, Instant.now().plus(5, ChronoUnit.SECONDS));
                return;
            case Beginner.NIMBLE_FEET:
            case Noblesse.NIMBLE_FEET:
            case Aran.AGILE_BODY:
            case Evan.NIMBLE_FEET:
                user.setTemporaryStat(CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)));
                return;
            case Beginner.MONSTER_RIDER:
            case Noblesse.MONSTER_RIDER:
            case Aran.MONSTER_RIDER:
            case Evan.MONSTER_RIDER:
                final Item tamingMobItem = user.getInventoryManager().getEquipped().getItem(BodyPart.TAMINGMOB.getValue());
                if (tamingMobItem == null) {
                    log.error("Tried to use Monster Rider skill without a taming mob");
                    return;
                }
                user.setTemporaryStat(CharacterTemporaryStat.RideVehicle, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.RideVehicle, tamingMobItem.getItemId(), skillId, 0));
                return;
            case Beginner.SOARING:
            case Noblesse.SOARING:
            case Aran.SOARING:
            case Evan.SOARING:
            case Citizen.SOARING:
                if (!user.getField().getMapInfo().isFly()) {
                    log.error("Tried to use Soaring skill outside of a flying map");
                    return;
                }
                user.setTemporaryStat(CharacterTemporaryStat.Flying, TemporaryStatOption.of(1, skillId, 0));
                return;
            case Beginner.ECHO_OF_HERO:
            case Noblesse.ECHO_OF_HERO:
            case Aran.ECHO_OF_HERO:
            case Evan.HEROS_ECHO:
            case Citizen.HEROS_ECHO:
                user.setTemporaryStat(CharacterTemporaryStat.MaxLevelBuff, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                skill.forEachAffectedUser(field, (other) -> {
                    try (var lockedOther = other.acquire()) {
                        other.setTemporaryStat(CharacterTemporaryStat.MaxLevelBuff, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                        other.write(UserLocal.effect(Effect.skillAffected(skill.skillId, skill.slv)));
                        field.broadcastPacket(UserRemote.effect(other, Effect.skillAffected(skill.skillId, skill.slv)), other);
                    }
                });
                return;

            // COPY SKILLS ---------------------------------------------------------------------------------------------
            case Warrior.IRON_BODY:
            case DawnWarrior.IRON_BODY:
                user.setTemporaryStat(CharacterTemporaryStat.PDD, TemporaryStatOption.of(si.getValue(SkillStat.pdd, slv), skillId, si.getDuration(slv)));
                return;
            case Warrior.RAGE:
            case DawnWarrior.RAGE:
                user.setTemporaryStat(CharacterTemporaryStat.PAD, TemporaryStatOption.of(si.getValue(SkillStat.pad, slv), skillId, si.getDuration(slv)));
                return;
            case Warrior.COMBO_ATTACK:
            case DawnWarrior.COMBO_ATTACK:
                user.setTemporaryStat(CharacterTemporaryStat.ComboCounter, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
            case Magician.MAGIC_GUARD:
            case BlazeWizard.MAGIC_GUARD:
            case Evan.MAGIC_GUARD:
                user.setTemporaryStat(CharacterTemporaryStat.MagicGuard, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, getBuffedDuration(user, si.getDuration(slv))));
                return;
            case Magician.MAGIC_ARMOR:
            case BlazeWizard.MAGIC_ARMOR:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.PDD, TemporaryStatOption.of(si.getValue(SkillStat.pdd, slv), skillId, getBuffedDuration(user, si.getDuration(slv))),
                        CharacterTemporaryStat.MDD, TemporaryStatOption.of(si.getValue(SkillStat.mdd, slv), skillId, getBuffedDuration(user, si.getDuration(slv)))
                ));
                return;
            case Magician.MEDITATION_FP:
            case Magician.MEDITATION_IL:
            case BlazeWizard.MEDITATION:
                user.setTemporaryStat(CharacterTemporaryStat.MAD, TemporaryStatOption.of(si.getValue(SkillStat.mad, slv), skillId, getBuffedDuration(user, si.getDuration(slv))));
                return;
            case Magician.SLOW_FP:
            case Magician.SLOW_IL:
            case BlazeWizard.SLOW:
                skill.forEachAffectedMob(field, (mob) -> {
                    if (!mob.isSlowUsed()) {
                        mob.setTemporaryStat(MobTemporaryStat.Speed, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)), 0);
                        mob.setSlowUsed(true); // cannot be used on the same monsters more than twice in a row
                    }
                });
                return;
            case Magician.SEAL_FP:
            case Magician.SEAL_IL:
            case BlazeWizard.SEAL:
                skill.forEachAffectedMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Seal, MobStatOption.of(1, skillId, si.getDuration(slv)), 0);
                    }
                });
                return;
            case Magician.TELEPORT_MASTERY_FP:
            case Magician.TELEPORT_MASTERY_IL:
            case Magician.TELEPORT_MASTERY_BISH:
            case BattleMage.TELEPORT_MASTERY:
                user.setTemporaryStat(CharacterTemporaryStat.TeleportMasteryOn, TemporaryStatOption.of(si.getValue(SkillStat.y, slv), skillId, 0));
                return;
            case Magician.ELEMENTAL_DECREASE_FP:
            case Magician.ELEMENTAL_DECREASE_IL:
            case BlazeWizard.ELEMENTAL_RESET:
            case Evan.ELEMENTAL_RESET:
                user.setTemporaryStat(CharacterTemporaryStat.ElementalReset, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, getBuffedDuration(user, si.getDuration(slv))));
                return;
            case Magician.IFRIT:
            case Magician.ELQUINES:
            case Magician.BAHAMUT:
            case BlazeWizard.IFRIT:
            case DawnWarrior.SOUL:
            case BlazeWizard.FLAME:
            case WindArcher.STORM:
            case NightWalker.DARKNESS:
            case ThunderBreaker.LIGHTNING:
                final Summoned summoned = Summoned.from(si, slv, SummonedMoveAbility.WALK, SummonedAssistType.ATTACK);
                summoned.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                user.addSummoned(summoned);
                return;
            case Bowman.FOCUS:
            case WindArcher.FOCUS:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.ACC, TemporaryStatOption.of(si.getValue(SkillStat.acc, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EVA, TemporaryStatOption.of(si.getValue(SkillStat.eva, slv), skillId, si.getDuration(slv))
                ));
                return;
            case Bowman.SOUL_ARROW_BM:
            case Bowman.SOUL_ARROW_MM:
            case WindArcher.SOUL_ARROW:
            case WildHunter.SOUL_ARROW_WH:
                user.setTemporaryStat(CharacterTemporaryStat.SoulArrow, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
            case Bowman.PUPPET_BM:
            case Bowman.PUPPET_MM:
            case WindArcher.PUPPET:
                final Summoned puppet = Summoned.from(si, slv, SummonedMoveAbility.STOP, SummonedAssistType.NONE);
                puppet.setHp(si.getValue(SkillStat.x, slv));
                puppet.setPosition(field, skill.positionX, skill.positionY, skill.summonLeft);
                user.addSummoned(puppet);
                return;
            case Thief.DARK_SIGHT:
            case NightWalker.DARK_SIGHT:
            case WindArcher.WIND_WALK:
                final CharacterTemporaryStat darkSightStat = skillId == WindArcher.WIND_WALK ?
                        CharacterTemporaryStat.WindWalk :
                        CharacterTemporaryStat.DarkSight;
                if (slv == si.getMaxLevel()) {
                    user.setTemporaryStat(darkSightStat, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                } else {
                    user.setTemporaryStat(Map.of(
                            darkSightStat, TemporaryStatOption.of(1, skillId, si.getDuration(slv)),
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
            case Thief.SHADOW_PARTNER_NL:
            case Thief.SHADOW_PARTNER_SHAD:
            case Thief.MIRROR_IMAGE:
            case NightWalker.SHADOW_PARTNER:
                user.setTemporaryStat(CharacterTemporaryStat.ShadowPartner, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case Thief.SHADOW_WEB:
            case NightWalker.SHADOW_WEB:
                skill.forEachAffectedMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Web, MobStatOption.of(1, skillId, si.getDuration(slv)), 0);
                    }
                });
                return;
            case Pirate.DASH:
            case ThunderBreaker.DASH:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Dash_Speed, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.Dash_Speed, si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Dash_Jump, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.Dash_Jump, si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                ));
                return;
            case Pirate.TRANSFORMATION:
            case Pirate.SUPER_TRANSFORMATION:
            case ThunderBreaker.TRANSFORMATION:
            case WindArcher.EAGLE_EYE:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Morph, TemporaryStatOption.of(si.getValue(SkillStat.morph, slv) + user.getGender() * 100, skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EPAD, TemporaryStatOption.of(si.getValue(SkillStat.epad, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EPDD, TemporaryStatOption.of(si.getValue(SkillStat.epdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.EMDD, TemporaryStatOption.of(si.getValue(SkillStat.emdd, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Jump, TemporaryStatOption.of(si.getValue(SkillStat.jump, slv), skillId, si.getDuration(slv))
                ));
                return;
            case Pirate.SPEED_INFUSION:
            case ThunderBreaker.SPEED_INFUSION:
                user.setTemporaryStat(CharacterTemporaryStat.PartyBooster, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.PartyBooster, si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;

            // COMMON SKILLS -------------------------------------------------------------------------------------------
            case Magician.TELEPORT_FP:
            case Magician.TELEPORT_IL:
            case Magician.TELEPORT_BISH:
            case Thief.FLASH_JUMP_NL:
            case Thief.FLASH_JUMP_SHAD:
            case Thief.FLASH_JUMP_DB:
            case DawnWarrior.SOUL_RUSH:
            case BlazeWizard.TELEPORT:
            case NightWalker.FLASH_JUMP:
            case Aran.COMBAT_STEP:
            case Evan.TELEPORT:
            case BattleMage.TELEPORT:
            case WildHunter.JAG_JUMP:
            case Mechanic.ROCKET_BOOSTER:
            case Citizen.MECHANIC_DASH:
                // noop
                return;
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
            case Pirate.GUN_BOOSTER:
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
                user.resetTemporaryStat(Set.of(
                        CharacterTemporaryStat.Poison,
                        CharacterTemporaryStat.Seal,
                        CharacterTemporaryStat.Darkness,
                        CharacterTemporaryStat.Weakness,
                        CharacterTemporaryStat.Curse,
                        CharacterTemporaryStat.Slow,
                        CharacterTemporaryStat.Attract,
                        CharacterTemporaryStat.ReverseInput,
                        CharacterTemporaryStat.StopPortion,
                        CharacterTemporaryStat.StopMotion,
                        CharacterTemporaryStat.Undead
                ));
                return;
        }

        // CLASS SPECIFIC SKILLS ---------------------------------------------------------------------------------------
        final int skillRoot = SkillConstants.getSkillRoot(skill.skillId);
        switch (Job.getById(skillRoot)) {
            case CITIZEN -> {
                Citizen.handleSkill(user, skill);
            }
            case WARRIOR, FIGHTER, CRUSADER, HERO, PAGE, WHITE_KNIGHT, PALADIN, SPEARMAN, DRAGON_KNIGHT, DARK_KNIGHT -> {
                Warrior.handleSkill(user, skill);
            }
            case MAGICIAN, WIZARD_FP, MAGE_FP, ARCH_MAGE_FP, WIZARD_IL, MAGE_IL, ARCH_MAGE_IL, CLERIC, PRIEST, BISHOP -> {
                Magician.handleSkill(user, skill);
            }
            case ARCHER, HUNTER, RANGER, BOWMASTER, CROSSBOWMAN, SNIPER, MARKSMAN -> {
                Bowman.handleSkill(user, skill);
            }
            case ROGUE, ASSASSIN, HERMIT, NIGHT_LORD, BANDIT, CHIEF_BANDIT, SHADOWER, BLADE_RECRUIT, BLADE_ACOLYTE, BLADE_SPECIALIST, BLADE_LORD, BLADE_MASTER -> {
                Thief.handleSkill(user, skill);
            }
            case PIRATE, BRAWLER, MARAUDER, BUCCANEER, GUNSLINGER, OUTLAW, CORSAIR -> {
                Pirate.handleSkill(user, skill);
            }
            case DAWN_WARRIOR_1, DAWN_WARRIOR_2, DAWN_WARRIOR_3 -> {
                DawnWarrior.handleSkill(user, skill);
            }
            case BLAZE_WIZARD_1, BLAZE_WIZARD_2, BLAZE_WIZARD_3 -> {
                BlazeWizard.handleSkill(user, skill);
            }
            case WIND_ARCHER_1, WIND_ARCHER_2, WIND_ARCHER_3 -> {
                WindArcher.handleSkill(user, skill);
            }
            case NIGHT_WALKER_1, NIGHT_WALKER_2, NIGHT_WALKER_3 -> {
                NightWalker.handleSkill(user, skill);
            }
            case THUNDER_BREAKER_1, THUNDER_BREAKER_2, THUNDER_BREAKER_3 -> {
                ThunderBreaker.handleSkill(user, skill);
            }
            case ARAN_1, ARAN_2, ARAN_3, ARAN_4 -> {
                Aran.handleSkill(user, skill);
            }
            case EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10 -> {
                Evan.handleSkill(user, skill);
            }
            case BATTLE_MAGE_1, BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4 -> {
                BattleMage.handleSkill(user, skill);
            }
            case WILD_HUNTER_1, WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4 -> {
                WildHunter.handleSkill(user, skill);
            }
            case MECHANIC_1, MECHANIC_2, MECHANIC_3, MECHANIC_4 -> {
                Mechanic.handleSkill(user, skill);
            }
            default -> {
                log.error("Unhandled skill {}", skill.skillId);
            }
        }
    }

    protected static int getBuffedDuration(User user, int duration) {
        final int skillId = SkillConstants.getBuffMasterySkill(user.getJob());
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return duration;
        }
        final int percentage = SkillProvider.getSkillInfoById(skillId).map((si) -> si.getValue(SkillStat.x, slv)).orElse(0);
        return duration * (100 + percentage) / 100;
    }


    // PROCESS UPDATE --------------------------------------------------------------------------------------------------

    public static void processUpdate(Locked<User> locked, Instant now) {
        final User user = locked.get();
        if (user.getHp() <= 0) {
            return;
        }
        handleRecovery(user, now);
        handleDragonBlood(user, now);
        handleInfinity(user, now);
        handleAura(user, now);
        handleMissileTank(user, now);
    }

    private static void handleRecovery(User user, Instant now) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.Regen)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.Regen);
        final int skillId = option.rOption;
        if (now.isAfter(user.getSkillManager().getSkillSchedule(skillId))) {
            final int hpRecovery = option.nOption;
            user.addHp(hpRecovery);
            user.write(UserLocal.effect(Effect.incDecHpEffect(hpRecovery)));
            user.getField().broadcastPacket(UserRemote.effect(user, Effect.incDecHpEffect(hpRecovery)), user);
            user.getSkillManager().setSkillSchedule(skillId, now.plus(5, ChronoUnit.SECONDS));
        }
    }

    private static void handleDragonBlood(User user, Instant now) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.DragonBlood)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.DragonBlood);
        final int skillId = option.rOption;
        if (now.isAfter(user.getSkillManager().getSkillSchedule(skillId))) {
            final int hpConsume = option.nOption;
            if (user.getHp() < hpConsume * 4) {
                // Skill is canceled when you don't have enough HP to be consumed in the next 4 seconds
                user.resetTemporaryStat(skillId);
                return;
            }
            user.addHp(-hpConsume);
            user.getSkillManager().setSkillSchedule(skillId, now.plus(1, ChronoUnit.SECONDS));
        }
    }

    private static void handleInfinity(User user, Instant now) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.Infinity)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.Infinity);
        final int skillId = option.rOption;
        if (now.isAfter(user.getSkillManager().getSkillSchedule(skillId))) {
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                log.error("Could not resolve skill info for infinity skill ID : {}", skillId);
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            final int slv = user.getSkillLevel(skillId);
            final int percentage = si.getValue(SkillStat.y, slv);
            // Recover hp and mp
            user.addHp(user.getMaxHp() * percentage / 100);
            user.addMp(user.getMaxMp() * percentage / 100);
            // Increase magic att %
            final int damage = si.getValue(SkillStat.damage, slv);
            user.setTemporaryStat(CharacterTemporaryStat.Infinity, option.update(option.nOption + damage));
            user.getSkillManager().setSkillSchedule(skillId, now.plus(4, ChronoUnit.SECONDS));
        }
    }

    private static void handleAura(User user, Instant now) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.Aura)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.Aura);
        final int skillId = BattleMage.getAdvancedAuraSkill(user, option.rOption);
        final int slv = user.getSkillLevel(skillId);
        if (now.isAfter(user.getSkillManager().getSkillSchedule(option.rOption))) {
            final CharacterTemporaryStat cts = SkillConstants.getStatByAuraSkill(skillId);
            if (cts == null) {
                log.error("Could not resolve CTS for aura skill ID : {}", skillId);
                return;
            }
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                log.error("Could not resolve skill info for aura skill ID : {}", skillId);
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            final Rect rect = user.getRelativeRect(si.getRect(slv));
            final int x = (cts == CharacterTemporaryStat.DarkAura ? si.getValue(SkillStat.x, slv) : slv);
            // Apply aura buff to self
            if (!user.getSecondaryStat().hasOption(cts)) {
                user.setTemporaryStat(cts, TemporaryStatOption.of(x, skillId, 0));
            }
            // Apply aura buff to party members
            user.getField().getUserPool().forEachPartyMember(user, (member) -> {
                try (var lockedMember = member.acquire()) {
                    if (rect.isInsideRect(member.getX(), member.getY())) {
                        if (!member.getSecondaryStat().hasOption(cts)) {
                            member.setTemporaryStat(cts, TemporaryStatOption.of(x, skillId, 0));
                        }
                    } else {
                        if (member.getSecondaryStat().hasOption(cts)) {
                            member.resetTemporaryStat(Set.of(cts));
                        }
                    }
                }
            });
            // Set next schedule
            user.getSkillManager().setSkillSchedule(option.rOption, now.plus(1, ChronoUnit.SECONDS));
        }
    }

    private static void handleMissileTank(User user, Instant now) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.Mechanic)) {
            return;
        }
        final int skillId = user.getSecondaryStat().getOption(CharacterTemporaryStat.Mechanic).rOption;
        if (skillId != Mechanic.MECH_MISSILE_TANK) {
            return;
        }
        if (now.isAfter(user.getSkillManager().getSkillSchedule(skillId))) {
            user.addMp(-user.getSkillStatValue(skillId, SkillStat.u));
            // Set next schedule
            user.getSkillManager().setSkillSchedule(skillId, now.plus(5, ChronoUnit.SECONDS));
        }
    }
}
