package kinoko.server.command.gm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.User;

import java.util.Optional;

public final class KillCommand {
    /**
     * Kills a specific player.
     * Usage: !kill <player name>
     */
    @Command("kill")
    @Arguments("player name")
    public static void kill(User user, String[] args) {
        final String targetName = args[1];

        // Find the target user on this channel
        final Optional<User> targetUserResult = user.getConnectedServer().getUserByCharacterName(targetName);

        if (targetUserResult.isEmpty()) {
            user.systemMessage("Could not find player '%s' on this channel.", targetName);
            return;
        }

        final User targetUser = targetUserResult.get();

        if (!user.getAdminLevel().isAtLeast(targetUser.getAdminLevel())){
            user.systemMessage("You cannot kill a GM that is a higher level than you!");
            return;
        }

        targetUser.kill();
        user.systemMessage("Killed player: %s", targetName);
    }
}
