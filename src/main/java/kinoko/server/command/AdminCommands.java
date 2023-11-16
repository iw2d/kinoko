package kinoko.server.command;

import kinoko.packet.world.StatFlag;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.world.user.User;

public final class AdminCommands {
    @Command("dispose")
    public static void dispose(User user, String[] args) {
        user.write(WvsContext.statChanged(StatFlag.NONE, user.getCharacterData()));
        user.write(WvsContext.message(Message.system("You have been disposed.")));
    }
}
