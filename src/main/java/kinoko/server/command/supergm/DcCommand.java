package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.User;

import java.util.Optional;

public final class DcCommand {
    /**
     * Disconnects a player from the server.
     * Usage: !dc <player name>
     */
    @Command({"dc", "disconnect"})
    @Arguments("player name")
    public static void dc(User user, String[] args) {
        final String targetName = args[1];

        // Find the target user on this channel
        final Optional<User> targetUserResult = user.getConnectedServer().getUserByCharacterName(targetName);

        if (targetUserResult.isEmpty()) {
            user.systemMessage("Could not find player '%s' on this channel.", targetName);
            return;
        }

        final User targetUser = targetUserResult.get();

        // Prevent self-disconnect
        if (targetUser.getCharacterId() == user.getCharacterId()) {
            user.systemMessage("You cannot disconnect yourself! Use @dispose or logout normally.");
            return;
        }

        user.systemMessage("Disconnecting player: %s", targetName);

        // Properly logout the user before closing connection
        targetUser.logout(true);
        targetUser.getClient().close();
    }
}
