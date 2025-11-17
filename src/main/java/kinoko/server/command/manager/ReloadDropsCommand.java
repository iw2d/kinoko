package kinoko.server.command.manager;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.user.User;
import kinoko.provider.RewardProvider;

public final class ReloadDropsCommand {

    /**
     * Reloads all drop data.
     * Manager command.
     *
     * @param user the user executing the command
     * @param args command arguments (none expected)
     */
    @Command("reloaddrops")
    public static void reloadDrops(User user, String[] args) {
        try {
            RewardProvider.initialize();
            user.systemMessage("Drops reloaded successfully.");
        } catch (Exception e) {
            user.systemMessage("Failed to reload drops: %s", e.getMessage());
        }
    }
}
