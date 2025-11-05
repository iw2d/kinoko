package kinoko.server.command.jrgm;

import kinoko.server.command.Command;
import kinoko.world.user.User;

import java.util.Optional;

public final class HealCommand {
    /**
     * Heals one or more players to full HP and MP.
     * Usage: !heal [player1] [player2] [...]
     * If no names are provided, heals yourself.
     */
    @Command("heal")
    public static void heal(User user, String[] args) {
        // If no player names provided, heal yourself
        if (args.length == 1) {
            user.heal();
            user.systemMessage("Healed %s.", user.getCharacterName());
            return;
        }

        // Loop through all provided player names
        for (int i = 1; i < args.length; i++) {
            String targetName = args[i];

            Optional<User> targetUserResult = user.getConnectedServer().getUserByCharacterName(targetName);

            if (targetUserResult.isEmpty()) {
                user.systemMessage("Could not find player '%s' on this channel.", targetName);
                continue; // skip to next name
            }

            User targetUser = targetUserResult.get();
            targetUser.heal();

            user.systemMessage("Healed %s.", targetUser.getCharacterName());
        }
    }
}
