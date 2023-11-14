package kinoko.server.command;

import kinoko.packet.world.StatFlag;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.server.client.Client;

public final class AdminCommands {
    @Command("dispose")
    public static void dispose(Client c, String[] args) {
        c.write(WvsContext.statChanged(StatFlag.NONE, c.getUser().getCharacterData()));
        c.write(WvsContext.message(Message.system("You have been disposed.")));
    }
}
