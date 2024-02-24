package kinoko.world.skill;

import kinoko.packet.field.MobPacket;
import kinoko.packet.user.UserRemote;
import kinoko.world.field.Field;
import kinoko.world.field.life.mob.Mob;
import kinoko.world.user.User;

import java.util.Arrays;
import java.util.Optional;

public final class SkillProcessor {
    public static void processAttack(User user, Attack attack) {
        // Set skill level
        if (attack.skillId != 0) {
            attack.skillId = user.getSkillManager().getSkillLevel(attack.skillId);
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

    public static void processSkill(User user, Skill skill) {

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
}
