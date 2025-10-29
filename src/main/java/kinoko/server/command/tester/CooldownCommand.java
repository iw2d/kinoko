package kinoko.server.command.tester;

import kinoko.packet.user.UserLocal;
import kinoko.server.command.Command;
import kinoko.world.user.User;

/**
 * Clears all skill cooldowns for the executing user.
 * Tester-level command.
 */
public final class CooldownCommand {

    @Command({"cd", "cooldown", "cooldowns"})
    public static void cd(User user, String[] args) {
        var iter = user.getSkillManager().getSkillCooltimes().keySet().iterator();
        while (iter.hasNext()) {
            int skillId = iter.next();
            user.write(UserLocal.skillCooltimeSet(skillId, 0)); // remove visual cooldown
            iter.remove(); // remove from internal map
        }
    }
}
