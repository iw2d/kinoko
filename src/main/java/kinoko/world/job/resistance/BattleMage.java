package kinoko.world.job.resistance;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public final class BattleMage extends SkillProcessor {
    // BATTLE_MAGE_1
    public static final int TRIPLE_BLOW = 32001000;
    public static final int THE_FINISHER = 32001001;
    public static final int TELEPORT = 32001002;
    public static final int DARK_AURA = 32001003;
    public static final int THE_FINISHER_STANDALONE = 32001007;
    public static final int THE_FINISHER_TRIPLE_BLOW = 32001008;
    public static final int THE_FINISHER_QUAD_BLOW = 32001009;
    public static final int THE_FINISHER_QUINTUPLE_BLOW = 32001010;
    public static final int THE_FINISHER_FINISHING_BLOW = 32001011;
    // BATTLE_MAGE_2
    public static final int STAFF_MASTERY = 32100006;
    public static final int QUAD_BLOW = 32101000;
    public static final int DARK_CHAIN = 32101001;
    public static final int BLUE_AURA = 32101002;
    public static final int YELLOW_AURA = 32101003;
    public static final int BLOOD_DRAIN = 32101004;
    public static final int STAFF_BOOST = 32101005;
    // BATTLE_MAGE_3
    public static final int ADVANCED_BLUE_AURA = 32110000;
    public static final int BATTLE_MASTERY = 32110001;
    public static final int BODY_BOOST_DARK_AURA = 32110007;
    public static final int BODY_BOOST_YELLOW_AURA = 32110008;
    public static final int BODY_BOOST_BLUE_AURA = 32110009;
    public static final int QUINTUPLE_BLOW = 32111002;
    public static final int DARK_SHOCK = 32111003;
    public static final int CONVERSION = 32111004;
    public static final int BODY_BOOST = 32111005;
    public static final int SUMMON_REAPER_BUFF = 32111006;
    public static final int TELEPORT_MASTERY = 32111010;
    public static final int ADVANCED_DARK_CHAIN = 32111011;
    // BATTLE_MAGE_4
    public static final int ADVANCED_DARK_AURA = 32120000;
    public static final int ADVANCED_YELLOW_AURA = 32120001;
    public static final int ENERGIZE = 32120009;
    public static final int FINISHING_BLOW = 32121002;
    public static final int TWISTER_SPIN = 32121003;
    public static final int DARK_GENESIS = 32121004;
    public static final int STANCE = 32121005;
    public static final int PARTY_SHIELD = 32121006;
    public static final int MAPLE_WARRIOR_BAM = 32121007;
    public static final int HEROS_WILL_BAM = 32121008;

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            case DARK_CHAIN:
            case ADVANCED_DARK_CHAIN:
                attack.forEachMob(field, (mob) -> {
                    if (!mob.isBoss()) {
                        mob.setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, skillId, si.getDuration(slv)));
                    }
                });
                break;
            case DARK_GENESIS:
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
            case DARK_AURA:
            case YELLOW_AURA:
            case BLUE_AURA:
                user.setTemporaryStat(CharacterTemporaryStat.Aura, TemporaryStatOption.of(1, skillId, 0));
                user.getSkillManager().setSkillSchedule(skillId, Instant.now());
                return;
            case BLOOD_DRAIN:
                user.setTemporaryStat(CharacterTemporaryStat.ComboDrain, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case CONVERSION:
                user.setTemporaryStat(CharacterTemporaryStat.Conversion, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)));
                return;
            case BODY_BOOST:
                user.setTemporaryStat(CharacterTemporaryStat.SuperBody, TemporaryStatOption.of(slv, skillId, si.getDuration(slv)));
                return;
            case SUMMON_REAPER_BUFF:
                // Revive is the Korean name for the skill, also the summon effect is on killing the mob, contrary to the English skill description
                user.setTemporaryStat(CharacterTemporaryStat.Revive, TemporaryStatOption.of(slv, skillId, si.getDuration(slv)));
                return;
            case TWISTER_SPIN:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Cyclone, TemporaryStatOption.of(1, skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.NotDamaged, TemporaryStatOption.of(1, skillId, si.getDuration(slv))
                ));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }

    public static int getAdvancedAuraSkill(User user, int skillId) {
        final int advancedAuraSkill = switch (skillId) {
            case DARK_AURA -> ADVANCED_DARK_AURA;
            case BLUE_AURA -> ADVANCED_BLUE_AURA;
            case YELLOW_AURA -> ADVANCED_YELLOW_AURA;
            default -> skillId;
        };
        if (user.getSkillLevel(advancedAuraSkill) > 0) {
            return advancedAuraSkill;
        }
        return skillId;
    }

    public static void cancelPartyAura(User user, int skillId) {
        final CharacterTemporaryStat cts = SkillConstants.getStatByAuraSkill(skillId);
        if (cts == null) {
            log.error("Could not resolve CTS while trying to cancel aura skill ID : {}", skillId);
            return;
        }
        // Remove user's aura from party members
        user.getField().getUserPool().forEachPartyMember(user, (member) -> {
            if (member.getSecondaryStat().hasOption(cts)) {
                member.resetTemporaryStat(Set.of(cts));
            }
        });
    }
}
