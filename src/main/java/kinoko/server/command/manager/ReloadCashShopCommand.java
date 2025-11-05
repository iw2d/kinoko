package kinoko.server.command.manager;

import kinoko.packet.world.MessagePacket;
import kinoko.server.cashshop.CashShop;
import kinoko.server.command.Command;
import kinoko.world.user.User;

public final class ReloadCashShopCommand {

    /**
     * Reloads the cash shop.
     * Manager command.
     *
     * @param user the user executing the command
     * @param args command arguments (none expected)
     */
    @Command({ "reloadcashshop", "reloadcs" })
    public static void reloadCashShop(User user, String[] args) {
        try {
            CashShop.initialize();
            user.systemMessage("Cash shop reloaded successfully.");
        } catch (Exception e) {
            user.systemMessage("Failed to reload cash shop: %s", e.getMessage());
        }
    }
}