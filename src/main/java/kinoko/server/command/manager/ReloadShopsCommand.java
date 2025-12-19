package kinoko.server.command.manager;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.ShopProvider;
import kinoko.server.command.Command;
import kinoko.world.user.User;

public final class ReloadShopsCommand {

    /**
     * Reloads all shop data.
     * Manager command.
     *
     * @param user the user executing the command
     * @param args command arguments (none expected)
     */
    @Command("reloadshops")
    public static void reloadShops(User user, String[] args) {
        try {
            ShopProvider.initialize();
            user.systemMessage("Shops reloaded successfully.");
        } catch (Exception e) {
            user.systemMessage("Failed to reload shops: %s", e.getMessage());
        }
    }
}
