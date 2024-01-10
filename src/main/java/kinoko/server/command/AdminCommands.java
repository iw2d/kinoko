package kinoko.server.command;

import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.world.user.User;

import java.util.Set;

public final class AdminCommands {
    @Command("dispose")
    public static void dispose(User user, String[] args) {
        user.dispose();
        user.write(WvsContext.message(Message.system("You have been disposed.")));
    }
}
