package kinoko.server.command.admin;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.ItemProvider;
import kinoko.server.command.Command;
import kinoko.world.user.User;

public class TestCommand {
    /**
     * Admin command to test server queries and dispose the player after applying an effect.
     */
    @Command("test")
    public static void test(User user, String[] args) {
        user.getConnectedServer().submitUserQueryRequestAll(queryResult -> {
            user.systemMessage("Users in world: %d", queryResult.size());
            user.systemMessage("Users in field : %d", user.getField().getUserPool().getCount());
            user.systemMessage("Party ID : %d (%d)", user.getPartyId(), user.getCharacterData().getPartyId());

            // Apply item effect (throws if item not found)
            user.setConsumeItemEffect(ItemProvider.getItemInfo(2022181).orElseThrow());

            // Dispose player
            user.dispose();
        });
    }
}
