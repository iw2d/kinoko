package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.Server;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.server.user.RemoteUser;
import kinoko.world.user.User;

import java.util.Optional;

public final class WarpToCommand {
    /**
     * Teleports to another player's location.
     * Usage: !warpto <player name>
     */
    @Command({"warpto", "reach"})
    @Arguments("player name")
    public static void warpToPlayer(User user, String[] args) {
        final String targetName = args[1];

        // Find the target user
        Optional<RemoteUser> targetRemoteUser = Server.getCentralServerNode().getRemoteUserByCharacterName(targetName);

        if (targetRemoteUser.isEmpty()) {
            user.write(MessagePacket.system("Could not find player '%s'.", targetName));
            return;
        }

        final RemoteUser targetUser = targetRemoteUser.get();
        user.warpTo(targetUser);
        user.systemMessage("Teleported to %s's location.", targetName);
    }
}
