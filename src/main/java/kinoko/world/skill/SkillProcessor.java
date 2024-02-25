package kinoko.world.skill;

import kinoko.packet.field.MobPacket;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.field.life.mob.Mob;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

public final class SkillProcessor {
    private static final Logger log = LogManager.getLogger(SkillProcessor.class);

    public static void processAttack(User user, Attack attack) {
        // Process skill
        if (attack.skillId != 0) {
            // Set skill level
            attack.slv = user.getSkillManager().getSkillLevel(attack.skillId);
            if (attack.slv == 0) {
                log.error("Tried to attack with skill {} not learned by user", attack.skillId);
                user.dispose();
                return;
            }
            // Resolve skill info
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(attack.skillId);
            if (skillInfoResult.isEmpty()) {
                log.error("Failed to resolve skill info for skill : {}", attack.skillId);
                user.dispose();
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            // Check and apply skill cost
            if (!applySkillCon(user, si, attack.slv)) {
                log.error("Tried to attack with skill {} without enough resources", attack.skillId);
                user.dispose();
                return;
            }
            // TODO skill handling
        }
        // Process attack damage
        final Field field = user.getField();
        for (AttackInfo ai : attack.getAttackInfo()) {
            final Optional<Mob> mobResult = field.getMobPool().getById(ai.mobId);
            if (mobResult.isEmpty()) {
                continue;
            }
            final int totalDamage = Arrays.stream(ai.damage).sum();
            // Acquire mob
            try (var locked = mobResult.get().acquire()) {
                final Mob mob = locked.get();
                mob.damage(user, totalDamage);
                // Show mob hp indicator
                final double percentage = (double) mob.getHp() / mob.getMaxHp();
                user.write(MobPacket.hpIndicator(mob, (int) (percentage * 100)));
                // Handle death
                if (mob.getHp() <= 0) {
                    mob.distributeExp();
                    mob.dropRewards(user);
                    field.getMobPool().removeMob(mob);
                }
            }
        }
        field.broadcastPacket(UserRemote.attack(user, attack), user);
    }

    public static void processSkill(User user, int skillId, int slv, InPacket inPacket) {
        // Resolve skill info
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Failed to resolve skill info for skill : {}", skillId);
            user.dispose();
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        // Check and apply skill cost
        if (!applySkillCon(user, si, slv)) {
            log.error("Tried to use skill {} without enough resources", skillId);
            user.dispose();
            return;
        }
        // TODO skill handling
        user.write(WvsContext.skillUseResult());
    }

    public static void processHit(User user, HitInfo hitInfo) {
        // TODO skill handling
        hitInfo.damage = Math.max(hitInfo.damage, 0);
        // Process hit damage
        if (hitInfo.damage > 0) {
            user.addHp(-hitInfo.damage);
        }
        user.getField().broadcastPacket(UserRemote.hit(user, hitInfo), user);
    }

    private static boolean applySkillCon(User user, SkillInfo si, int slv) {
        final int hpCon = si.getValue(SkillStat.hpCon, slv);
        final int mpCon = si.getValue(SkillStat.mpCon, slv);
        final int bulletCon = si.getValue(SkillStat.bulletCount, slv);
        // TODO
        return true;
    }
}
