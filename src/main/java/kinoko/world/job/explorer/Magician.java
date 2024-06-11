package kinoko.world.job.explorer;

import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.world.field.Field;
import kinoko.world.field.TownPortal;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillDispatcher;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public final class Magician {
    // MAGICIAN
    public static final int MP_BOOST = 2000006;
    public static final int MAGIC_GUARD = 2001002;
    public static final int MAGIC_ARMOR = 2001003;
    public static final int ENERGY_BOLT = 2001004;
    public static final int MAGIC_CLAW = 2001005;
    // WIZARD_FP
    public static final int MP_EATER_FP = 2100000;
    public static final int SPELL_MASTERY_FP = 2100006;
    public static final int MEDITATION_FP = 2101001;
    public static final int TELEPORT_FP = 2101002;
    public static final int SLOW_FP = 2101003;
    public static final int FIRE_ARROW = 2101004;
    public static final int POISON_BREATH = 2101005;
    // MAGE_FP
    public static final int PARTIAL_RESISTANCE_FP = 2110000;
    public static final int ELEMENT_AMPLIFICATION_FP = 2110001;
    public static final int EXPLOSION = 2111002;
    public static final int POISON_MIST = 2111003;
    public static final int SEAL_FP = 2111004;
    public static final int SPELL_BOOSTER_FP = 2111005;
    public static final int ELEMENT_COMPOSITION_FP = 2111006;
    public static final int TELEPORT_MASTERY_FP = 2111007;
    public static final int ELEMENTAL_DECREASE_FP = 2111008;
    // ARCH_MAGE_FP
    public static final int BUFF_MASTERY_FP = 2120009;
    public static final int MAPLE_WARRIOR_FP = 2121000;
    public static final int BIG_BANG_FP = 2121001;
    public static final int MANA_REFLECTION_FP = 2121002;
    public static final int FIRE_DEMON = 2121003;
    public static final int INFINITY_FP = 2121004;
    public static final int IFRIT = 2121005;
    public static final int PARALYZE = 2121006;
    public static final int METEOR_SHOWER = 2121007;
    public static final int HEROS_WILL_FP = 2121008;
    // WIZARD_IL
    public static final int MP_EATER_IL = 2200000;
    public static final int SPELL_MASTERY_IL = 2200006;
    public static final int MEDITATION_IL = 2201001;
    public static final int TELEPORT_IL = 2201002;
    public static final int SLOW_IL = 2201003;
    public static final int COLD_BEAM = 2201004;
    public static final int THUNDER_BOLT = 2201005;
    // MAGE_IL
    public static final int PARTIAL_RESISTANCE_IL = 2210000;
    public static final int ELEMENT_AMPLIFICATION_IL = 2210001;
    public static final int ICE_STRIKE = 2211002;
    public static final int THUNDER_SPEAR = 2211003;
    public static final int SEAL_IL = 2211004;
    public static final int SPELL_BOOSTER_IL = 2211005;
    public static final int ELEMENT_COMPOSITION_IL = 2211006;
    public static final int TELEPORT_MASTERY_IL = 2211007;
    public static final int ELEMENTAL_DECREASE_IL = 2211008;
    // ARCH_MAGE_IL
    public static final int BUFF_MASTERY_IL = 2220009;
    public static final int MAPLE_WARRIOR_IL = 2221000;
    public static final int BIG_BANG_IL = 2221001;
    public static final int MANA_REFLECTION_IL = 2221002;
    public static final int ICE_DEMON = 2221003;
    public static final int INFINITY_IL = 2221004;
    public static final int ELQUINES = 2221005;
    public static final int CHAIN_LIGHTNING = 2221006;
    public static final int BLIZZARD = 2221007;
    public static final int HEROS_WILL_IL = 2221008;
    // CLERIC
    public static final int MP_EATER_BISH = 2300000;
    public static final int SPELL_MASTERY_BISH = 2300006;
    public static final int TELEPORT_BISH = 2301001;
    public static final int HEAL = 2301002;
    public static final int INVINCIBLE = 2301003;
    public static final int BLESS = 2301004;
    public static final int HOLY_ARROW = 2301005;
    // PRIEST
    public static final int ELEMENTAL_RESISTANCE_BISH = 2310000;
    public static final int HOLY_FOCUS = 2310008;
    public static final int DISPEL = 2311001;
    public static final int MYSTIC_DOOR = 2311002;
    public static final int HOLY_SYMBOL = 2311003;
    public static final int SHINING_RAY = 2311004;
    public static final int DOOM = 2311005;
    public static final int SUMMON_DRAGON = 2311006;
    public static final int TELEPORT_MASTERY_BISH = 2311007;
    // BISHOP
    public static final int BUFF_MASTERY_BISH = 2320010;
    public static final int MAPLE_WARRIOR_BISH = 2321000;
    public static final int BIG_BANG_BISH = 2321001;
    public static final int MANA_REFLECTION_BISH = 2321002;
    public static final int BAHAMUT = 2321003;
    public static final int INFINITY_BISH = 2321004;
    public static final int HOLY_SHIELD = 2321005;
    public static final int RESURRECTION = 2321006;
    public static final int ANGEL_RAY = 2321007;
    public static final int GENESIS = 2321008;
    public static final int HEROS_WILL_BISH = 2321009;

    private static final Logger log = LogManager.getLogger(SkillDispatcher.class);

    public static void handleAttack(User user, Attack attack) {
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int skillId = attack.skillId;
        final int slv = attack.slv;

        final Field field = user.getField();
        switch (skillId) {
            // FP
            case POISON_BREATH, ELEMENT_COMPOSITION_FP -> {
                final BurnedInfo burnedInfo = BurnedInfo.from(user, si, slv);
                attack.forEachMob(field, (mob) -> {
                    if (mob.isBoss()) {
                        return;
                    }
                    mob.setBurnedInfo(burnedInfo);
                });
            }
            case POISON_MIST -> {
                final AffectedArea affectedArea = AffectedArea.userSkill(user, si, slv, 0, attack.userX, attack.userY);
                user.getField().getAffectedAreaPool().addAffectedArea(affectedArea);
            }
        }
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            // BISHOP
            case MYSTIC_DOOR:
                final Optional<TownPortal> townPortalResult = field.getTownPortalPool().createFieldPortal(
                        user,
                        skillId,
                        skill.positionX,
                        skill.positionY,
                        Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS)
                );
                if (townPortalResult.isPresent()) {
                    final TownPortal townPortal = townPortalResult.get();
                    user.setTownPortal(townPortal);
                    user.write(WvsContext.townPortal(townPortal));
                    user.getConnectedServer().notifyUserUpdate(user);
                } else {
                    user.write(MessagePacket.system("You cannot use the Mystic Door skill here."));
                }
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}