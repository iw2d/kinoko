package kinoko.world.job.explorer;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.InPacket;
import kinoko.world.job.JobHandler;
import kinoko.world.life.mob.MobStatOption;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public final class Warrior {
    // WARRIOR
    public static final int HP_BOOST = 1000006;
    public static final int IRON_BODY = 1001003;
    public static final int POWER_STRIKE = 1001004;
    public static final int SLASH_BLAST = 1001005;
    // FIGHTER
    public static final int WEAPON_MASTERY_HERO = 1100000;
    public static final int FINAL_ATTACK_HERO = 1100002;
    public static final int ENHANCED_BASICS_HERO = 1100009;
    public static final int WEAPON_BOOSTER_HERO = 1101004;
    public static final int RAGE = 1101006;
    public static final int POWER_GUARD_HERO = 1101007;
    public static final int GROUND_SMASH_HERO = 1101008;
    // CRUSADER
    public static final int IMPROVING_MP_RECOVERY = 1110000;
    public static final int CHANCE_ATTACK = 1110009;
    public static final int COMBO_ATTACK = 1111002;
    public static final int PANIC = 1111003;
    public static final int COMA = 1111005;
    public static final int MAGIC_CRASH_HERO = 1111007;
    public static final int SHOUT = 1111008;
    public static final int BRANDISH = 1111010;
    // HERO
    public static final int ADVANCED_COMBO_ATTACK = 1120003;
    public static final int ACHILLES_HERO = 1120004;
    public static final int COMBAT_MASTERY = 1120012;
    public static final int MAPLE_WARRIOR_HERO = 1121000;
    public static final int MONSTER_MAGNET_HERO = 1121001;
    public static final int POWER_STANCE_HERO = 1121002;
    public static final int RUSH_HERO = 1121006;
    public static final int INTREPID_SLASH = 1121008;
    public static final int ENRAGE = 1121010;
    public static final int HEROS_WILL_HERO = 1121011;
    // PAGE
    public static final int WEAPON_MASTERY_PALADIN = 1200000;
    public static final int FINAL_ATTACK_PALADIN = 1200002;
    public static final int ENHANCED_BASICS_PALADIN = 1200009;
    public static final int WEAPON_BOOSTER_PALADIN = 1201004;
    public static final int THREATEN = 1201006;
    public static final int POWER_GUARD_PALADIN = 1201007;
    public static final int GROUND_SMASH_PALADIN = 1201008;
    // WHITE_KNIGHT
    public static final int SHIELD_MASTERY = 1210001;
    public static final int CHARGED_BLOW = 1211002;
    public static final int FIRE_CHARGE = 1211004;
    public static final int ICE_CHARGE = 1211006;
    public static final int LIGHTNING_CHARGE = 1211008;
    public static final int MAGIC_CRASH_PALADIN = 1211009;
    public static final int HP_RECOVERY = 1211010;
    public static final int COMBAT_ORDERS = 1211011;
    // PALADIN
    public static final int ACHILLES_PALADIN = 1220005;
    public static final int GUARDIAN = 1220006;
    public static final int ADVANCED_CHARGE = 1220010;
    public static final int DIVINE_SHIELD = 1220013;
    public static final int MAPLE_WARRIOR_PALADIN = 1221000;
    public static final int POWER_STANCE_PALADIN = 1221002;
    public static final int DIVINE_CHARGE = 1221004;
    public static final int RUSH_PALADIN = 1221007;
    public static final int BLAST = 1221009;
    public static final int HEAVENS_HAMMER = 1221011;
    public static final int HEROS_WILL_PALADIN = 1221012;
    // SPEARMAN
    public static final int WEAPON_MASTERY_DRK = 1300000;
    public static final int FINAL_ATTACK_DRK = 1300002;
    public static final int ENHANCED_BASICS_DRK = 1300009;
    public static final int WEAPON_BOOSTER_DRK = 1301004;
    public static final int IRON_WILL = 1301006;
    public static final int HYPER_BODY = 1301007;
    public static final int GROUND_SMASH_DRK = 1301008;
    // DRAGON_KNIGHT
    public static final int ELEMENTAL_RESISTANCE = 1310000;
    public static final int DRAGON_WISDOM = 1310009;
    public static final int DRAGON_BUSTER = 1311001;
    public static final int DRAGON_FURY = 1311003;
    public static final int SACRIFICE = 1311005;
    public static final int DRAGON_ROAR = 1311006;
    public static final int MAGIC_CRASH_DRK = 1311007;
    public static final int DRAGON_BLOOD = 1311008;
    // DARK_KNIGHT
    public static final int ACHILLES_DRK = 1320005;
    public static final int BERSERK = 1320006;
    public static final int AURA_OF_THE_BEHOLDER = 1320008;
    public static final int HEX_OF_THE_BEHOLDER = 1320009;
    public static final int HEX_OF_THE_BEHOLDER_COUNTER = 1320011;
    public static final int MAPLE_WARRIOR_DRK = 1321000;
    public static final int MONSTER_MAGNET_DRK = 1321001;
    public static final int POWER_STANCE_DRK = 1321002;
    public static final int RUSH_DRK = 1321003;
    public static final int BEHOLDER = 1321007;
    public static final int HEROS_WILL_DRK = 1321010;
    private static final Logger log = LogManager.getLogger(JobHandler.class);

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final MobStatOption m1 = new MobStatOption();
        switch (skillId) {
            case PANIC:
            case COMA:
            case SHOUT:
                // TODO mobstat
        }
    }

    public static void handleSkill(User user, Skill skill, InPacket inPacket) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final TemporaryStatOption o1 = new TemporaryStatOption();
        final TemporaryStatOption o2 = new TemporaryStatOption();
        final MobStatOption m1 = new MobStatOption();
        switch (skillId) {
            // COMMON
            case IRON_BODY:
                o1.nOption = si.getValue(SkillStat.pdd, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.PDD, o1);
                return;
            case POWER_GUARD_HERO:
            case POWER_GUARD_PALADIN:
                o1.nOption = si.getValue(SkillStat.x, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.PowerGuard, o1);
                return;
            case MAGIC_CRASH_HERO:
            case MAGIC_CRASH_PALADIN:
            case MAGIC_CRASH_DRK:
                // TODO mobstat
                return;
            case POWER_STANCE_HERO:
            case POWER_STANCE_PALADIN:
            case POWER_STANCE_DRK:
                o1.nOption = si.getValue(SkillStat.prop, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.Stance, o1);
                return;

            // HERO
            case RAGE:
                o1.nOption = si.getValue(SkillStat.pad, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.PAD, o1);
                return;
            case COMBO_ATTACK:
                o1.nOption = 1;
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.ComboCounter, o1);
                return;
            case ENRAGE:
                o1.nOption = si.getValue(SkillStat.x, slv) * 100 + si.getValue(SkillStat.mobCount, slv); // damR = n / 100, nCount = n % 100
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.Enrage, o1);
                return;

            // PALADIN
            case THREATEN:
                // TODO mobstat
                return;
            case HP_RECOVERY:
                // TODO
                return;
            case COMBAT_ORDERS:
                o1.nOption = si.getValue(SkillStat.x, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.CombatOrders, o1);
                return;

            // DARK KNIGHT
            case IRON_WILL:
                o1.nOption = si.getValue(SkillStat.pdd, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                o2.nOption = si.getValue(SkillStat.mdd, slv);
                o2.rOption = skillId;
                o2.tOption = si.getDuration(slv);
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.PDD, o1,
                        CharacterTemporaryStat.MDD, o2
                ));
                return;
            case HYPER_BODY:
                o1.nOption = si.getValue(SkillStat.x, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                o2.nOption = si.getValue(SkillStat.y, slv);
                o2.rOption = skillId;
                o2.tOption = si.getDuration(slv);
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.MaxHP, o1,
                        CharacterTemporaryStat.MaxMP, o2
                ));
                return;
            case DRAGON_BLOOD:
                o1.nOption = si.getValue(SkillStat.pad, slv);
                o1.rOption = skillId;
                o1.tOption = si.getDuration(slv);
                user.setTemporaryStat(CharacterTemporaryStat.DragonBlood, o1);
                return;
            case BEHOLDER:
                // TODO summoned
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}
