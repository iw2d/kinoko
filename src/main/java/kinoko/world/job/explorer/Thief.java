package kinoko.world.job.explorer;

import kinoko.packet.field.MobPacket;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.ServerConfig;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.field.affectedarea.AffectedAreaType;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAssistType;
import kinoko.world.field.summoned.SummonedMoveAbility;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.CalcDamage;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class Thief extends SkillProcessor {
    // ROGUE
    public static final int NIMBLE_BODY = 4000000;
    public static final int KEEN_EYES = 4000001;
    public static final int DISORDER = 4001002;
    public static final int DARK_SIGHT = 4001003;
    public static final int DOUBLE_STAB = 4001334;
    public static final int LUCKY_SEVEN = 4001344;
    // ASSASSIN
    public static final int CLAW_MASTERY = 4100000;
    public static final int CRITICAL_THROW = 4100001;
    public static final int SHADOW_RESISTANCE_NL = 4100006;
    public static final int CLAW_BOOSTER = 4101003;
    public static final int HASTE_NL = 4101004;
    public static final int DRAIN = 4101005;
    // HERMIT
    public static final int ALCHEMIST = 4110000;
    public static final int MESO_UP = 4111001;
    public static final int SHADOW_PARTNER_NL = 4111002;
    public static final int SHADOW_WEB = 4111003;
    public static final int SHADOW_MESO = 4111004;
    public static final int AVENGER = 4111005;
    public static final int FLASH_JUMP_NL = 4111006;
    public static final int DARK_FLARE_NL = 4111007;
    // NIGHT_LORD
    public static final int SHADOW_SHIFTER_NL = 4120002;
    public static final int VENOMOUS_STAR = 4120005;
    public static final int EXPERT_THROWING_STAR_HANDLING = 4120010;
    public static final int MAPLE_WARRIOR_NL = 4121000;
    public static final int TAUNT_NL = 4121003;
    public static final int NINJA_AMBUSH_NL = 4121004;
    public static final int SHADOW_STARS = 4121006;
    public static final int TRIPLE_THROW = 4121007;
    public static final int NINJA_STORM = 4121008;
    public static final int HEROS_WILL_NL = 4121009;
    // BANDIT
    public static final int DAGGER_MASTERY = 4200000;
    public static final int SHADOW_RESISTANCE_SHAD = 4200006;
    public static final int DAGGER_BOOSTER = 4201002;
    public static final int HASTE_SHAD = 4201003;
    public static final int STEAL = 4201004;
    public static final int SAVAGE_BLOW = 4201005;
    // CHIEF_BANDIT
    public static final int SHIELD_MASTERY = 4210000;
    public static final int CHAKRA = 4211001;
    public static final int ASSAULTER = 4211002;
    public static final int PICKPOCKET = 4211003;
    public static final int BAND_OF_THIEVES = 4211004;
    public static final int MESO_GUARD = 4211005;
    public static final int MESO_EXPLOSION = 4211006;
    public static final int DARK_FLARE_SHAD = 4211007;
    public static final int SHADOW_PARTNER_SHAD = 4211008;
    public static final int FLASH_JUMP_SHAD = 4211009;
    // SHADOWER
    public static final int SHADOW_SHIFTER_SHAD = 4220002;
    public static final int VENOMOUS_STAB = 4220005;
    public static final int MESO_MASTERY = 4220009;
    public static final int MAPLE_WARRIOR_SHAD = 4221000;
    public static final int ASSASSINATE = 4221001;
    public static final int TAUNT_SHAD = 4221003;
    public static final int NINJA_AMBUSH_SHAD = 4221004;
    public static final int SMOKESCREEN = 4221006;
    public static final int BOOMERANG_STEP = 4221007;
    public static final int HEROS_WILL_SHAD = 4221008;
    // BLADE_RECRUIT
    public static final int KATARA_MASTERY = 4300000;
    public static final int TRIPLE_STAB = 4301001;
    public static final int KATARA_BOOSTER = 4301002;
    // BLADE_ACOLYTE
    public static final int SHADOW_RESISTANCE_DB = 4310004;
    public static final int SELF_HASTE = 4311001;
    public static final int FATAL_BLOW = 4311002;
    public static final int SLASH_STORM = 4311003;
    // BLADE_SPECIALIST
    public static final int TORNADO_SPIN = 4321000;
    public static final int TORNADO_SPIN_ATTACK = 4321001;
    public static final int FLASHBANG = 4321002;
    public static final int FLASH_JUMP_DB = 4321003;
    // BLADE_LORD
    public static final int ADVANCED_DARK_SIGHT = 4330001;
    public static final int BLOODY_STORM = 4331000;
    public static final int MIRROR_IMAGE = 4331002;
    public static final int OWL_SPIRIT = 4331003;
    public static final int UPPER_STAB = 4331004;
    public static final int FLYING_ASSAULTER = 4331005;
    // BLADE_MASTER
    public static final int VENOM_DB = 4340001;
    public static final int MAPLE_WARRIOR_DB = 4341000;
    public static final int FINAL_CUT = 4341002;
    public static final int MONSTER_BOMB = 4341003;
    public static final int SUDDEN_RAID = 4341004;
    public static final int CHAINS_OF_HELL = 4341005;
    public static final int MIRRORED_TARGET = 4341006;
    public static final int THORNS = 4341007;
    public static final int HEROS_WILL_DB = 4341008;

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case DISORDER:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss()) {
                        mob.setTemporaryStat(Map.of(
                                MobTemporaryStat.PAD, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                                MobTemporaryStat.PDR, MobStatOption.of(si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                        ));
                    }
                });
                break;
            case SHADOW_MESO:
                attack.forEachMob(field, (mob) -> {
                    mob.resetTemporaryStat(Set.of(MobTemporaryStat.PGuardUp, MobTemporaryStat.MGuardUp));
                });
                break;
            case TAUNT_NL:
            case TAUNT_SHAD:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss()) {
                        mob.setTemporaryStat(Map.of(
                                MobTemporaryStat.Showdown, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                                MobTemporaryStat.PDR, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                                MobTemporaryStat.MDR, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv))
                        ));
                    }
                });
                break;
            case ASSAULTER:
            case BOOMERANG_STEP:
            case FLYING_ASSAULTER:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
            case STEAL:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)));
                        mob.steal(user);
                    }
                });
                break;
            case MESO_EXPLOSION:
                for (int dropId : attack.drops) {
                    final Optional<Drop> dropResult = field.getDropPool().getById(dropId);
                    if (dropResult.isEmpty() || !dropResult.get().isMoney()) {
                        log.error("Received invalid drop ID {} for meso explosion skill", dropId);
                        continue;
                    }
                    field.getDropPool().removeDrop(dropResult.get(), DropLeaveType.EXPLODE, 0, 0, attack.dropExplodeDelay);
                }
                break;
            case FLASHBANG:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Blind, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                    }
                });
                break;
            case UPPER_STAB:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss() && !mob.getMobStat().hasOption(MobTemporaryStat.RiseByToss)) {
                        mob.setTemporaryStat(MobTemporaryStat.RiseByToss, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, 1000));
                    }
                });
                break;
            case SUDDEN_RAID:
                attack.forEachMob(field, (mob) -> {
                    mob.setBurnedInfo(BurnedInfo.from(user, si, slv, mob));
                });
                break;
            case FINAL_CUT:
                final int finalCut = si.getValue(SkillStat.y, slv) * attack.keyDown / SkillConstants.getMaxGaugeTime(skillId);
                user.setTemporaryStat(CharacterTemporaryStat.FinalCut, TemporaryStatOption.of(finalCut, skillId, si.getDuration(slv)));
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
            case DARK_SIGHT:
                if (slv == si.getMaxLevel()) {
                    user.setTemporaryStat(CharacterTemporaryStat.DarkSight, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                } else {
                    user.setTemporaryStat(Map.of(
                            CharacterTemporaryStat.DarkSight, TemporaryStatOption.of(1, skillId, si.getDuration(slv)),
                            CharacterTemporaryStat.Slow, TemporaryStatOption.of(100 - si.getValue(SkillStat.y, slv), skillId, si.getDuration(slv))
                    ));
                }
                return;
            case HASTE_NL:
            case HASTE_SHAD:
            case SELF_HASTE:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Jump, TemporaryStatOption.of(si.getValue(SkillStat.jump, slv), skillId, si.getDuration(slv))
                ));
                return;
            case SHADOW_PARTNER_NL:
            case SHADOW_PARTNER_SHAD:
            case MIRROR_IMAGE:
                user.setTemporaryStat(CharacterTemporaryStat.ShadowPartner, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case DARK_FLARE_NL:
            case DARK_FLARE_SHAD:
                final Summoned darkFlare = Summoned.from(si, slv, SummonedMoveAbility.STOP, SummonedAssistType.ATTACK_COUNTER);
                darkFlare.setPosition(field, skill.positionX, skill.positionY);
                darkFlare.setRect(SkillConstants.DARK_FLARE_RANGE.translate(skill.positionX, skill.positionY));
                user.addSummoned(darkFlare);
                return;
            case NINJA_AMBUSH_NL:
            case NINJA_AMBUSH_SHAD:
                final int damage = (int) Math.clamp(CalcDamage.calcDamageMax(user) * si.getValue(SkillStat.damage, slv) / 100, 1.0, GameConstants.DAMAGE_MAX);
                skill.forEachAffectedMob(field, (mob) -> {
                    mob.setBurnedInfo(new BurnedInfo(user.getCharacterId(), skillId, damage, 1000, si.getValue(SkillStat.time, slv)));
                });
                return;

            // NL
            case MESO_UP:
                user.setTemporaryStat(CharacterTemporaryStat.MesoUp, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case SHADOW_WEB:
                skill.forEachAffectedMob(field, (mob) -> {
                    if (!mob.isBoss() && Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                        mob.setTemporaryStat(MobTemporaryStat.Web, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
            case SHADOW_STARS:
                user.setTemporaryStat(CharacterTemporaryStat.SpiritJavelin, TemporaryStatOption.of(skill.spiritJavelinItemId - 2069999, skillId, si.getDuration(slv)));
                return;

            // SHAD
            case CHAKRA:
                final double mod = Util.getRandom(3.3, 6.6); // [(luk * 3.3 + dex) * 0.2] ~ [(luk * 6.6 + dex) * 0.2]
                user.addHp((int) ((user.getBasicStat().getLuk() * mod + user.getBasicStat().getDex()) * 0.2 * si.getValue(SkillStat.y, slv) / 100.0));
                return;
            case PICKPOCKET:
                user.setTemporaryStat(CharacterTemporaryStat.PickPocket, TemporaryStatOption.of(slv, skillId, si.getDuration(slv)));
                return;
            case MESO_GUARD:
                user.setTemporaryStat(CharacterTemporaryStat.MesoGuard, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case SMOKESCREEN:
                final AffectedArea smoke = AffectedArea.from(AffectedAreaType.Smoke, user, si, slv, 0, skill.positionX, skill.positionY);
                user.getField().getAffectedAreaPool().addAffectedArea(smoke);
                return;

            // DB
            case TORNADO_SPIN:
                user.setTemporaryStat(CharacterTemporaryStat.Dash_Speed, TemporaryStatOption.ofTwoState(CharacterTemporaryStat.Dash_Speed, si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case MIRRORED_TARGET:
                final Summoned summoned = new Summoned(skillId, slv, SummonedMoveAbility.STOP, SummonedAssistType.NONE, user.getCharacterData().getAvatarLook(), Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS));
                summoned.setPosition(field, skill.positionX, skill.positionY);
                summoned.setHp(si.getValue(SkillStat.x, slv));
                user.addSummoned(summoned);
                user.resetTemporaryStat(Set.of(CharacterTemporaryStat.ShadowPartner));
                return;
            case THORNS:
                final int thorns = (si.getValue(SkillStat.x, slv) << 8) + si.getValue(SkillStat.criticaldamageMax, slv); // (cr << 8) + cd
                user.setTemporaryStat(CharacterTemporaryStat.ThornsEffect, TemporaryStatOption.of(thorns, skillId, si.getDuration(slv)));
                return;
            case MONSTER_BOMB:
                skill.forEachAffectedMob(field, (mob) -> {
                    field.broadcastPacket(MobPacket.mobAffected(mob, skillId, 0));
                    mob.setTemporaryStat(MobTemporaryStat.TimeBomb, MobStatOption.of(1, skillId, si.getDuration(slv) + ServerConfig.FIELD_TICK_INTERVAL)); // for MobTimeBombEnd check
                });
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}