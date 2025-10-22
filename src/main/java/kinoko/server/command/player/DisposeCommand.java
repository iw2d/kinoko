package kinoko.server.command.player;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.user.User;

public class DisposeCommand {
    /**
     * Player command to dispose themselves.
     */
    @Command("dispose")
    public static void dispose(User user, String[] args) {
        user.closeDialog();
        user.dispose();
        user.write(MessagePacket.system("You have been disposed."));
    }
}
