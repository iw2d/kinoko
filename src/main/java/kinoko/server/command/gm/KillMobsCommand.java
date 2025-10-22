package kinoko.server.command.gm;

import kinoko.server.command.Command;
import kinoko.world.field.mob.MobLeaveType;
import kinoko.world.user.User;

/**
 * Instantly kills all mobs in the user's field.
 * GM-level command.
 */
public final class KillMobsCommand {

    @Command("killmobs")
    public static void killMobs(User user, String[] args) {
        user.getField().getMobPool().forEach(mob -> {
            if (mob.getHp() > 0) {
                mob.damage(user, mob.getMaxHp(), 0, MobLeaveType.ETC);
            }
        });
    }
}
