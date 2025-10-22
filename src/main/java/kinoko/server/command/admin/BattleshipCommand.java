package kinoko.server.command.admin;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.user.User;

/**
 * Shows the Battleship HP for the executing user.
 * Admin-level command.
 */
public final class BattleshipCommand {

    @Command({ "battleship", "bship" })
    public static void battleship(User user, String[] args) {
        user.write(MessagePacket.system("Battleship HP : %d", Pirate.getBattleshipDurability(user)));
    }
}
