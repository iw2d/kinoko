package kinoko.server.command.gm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.field.Field;
import kinoko.world.user.User;
import kinoko.world.user.stat.AdminLevel;

public final class KillMapCommand {
    /**
     * Kills all players in the current map.
     * Usage: !killmap
     */
    @Command("killmap")
    public static void killMap(User user, String[] args) {
        final Field field = user.getField();
        final int[] killCount = {0}; // needed here for lambda mutation

        field.getUserPool().forEachExcept(user, targetUser -> {
            if (targetUser.getAdminLevel().isAtLeast(AdminLevel.JR_GM)) {
                return; // skip GMs and above
            }
            targetUser.kill();
            killCount[0]++;
        });

        user.systemMessage("Killed %d player(s) in the current map.", killCount[0]);
    }
}
