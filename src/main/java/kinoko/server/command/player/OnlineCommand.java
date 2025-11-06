package kinoko.server.command.player;

import kinoko.server.Server;
import kinoko.server.command.Command;
import kinoko.world.user.User;

public final class OnlineCommand {
    /**
     * Shows the number of online players.
     * Usage: @online
     */
    @Command("online")
    public static void online(User user, String[] args) {
        final int onlineCount = Server.getCentralServerNode().getRemoteUserCount();
        user.systemMessage("There are currently %d player(s) online.", onlineCount);
    }
}
