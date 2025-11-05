package kinoko.server.command.gm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.field.Field;
import kinoko.world.user.User;

public final class HealMapCommand {
    /**
     * Heals all players in the current map to full HP and MP.
     * Usage: !healmap
     */
    @Command("healmap")
    public static void healMap(User user, String[] args) {
        final Field field = user.getField();
        int healedCount = 0;

        for (User targetUser : field.getUserPool().values()) {
            targetUser.heal();
            healedCount++;
        }

        user.systemMessage("Healed %d player(s) in the current map.", healedCount);
    }
}
