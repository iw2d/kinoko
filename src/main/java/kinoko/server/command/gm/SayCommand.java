package kinoko.server.command.gm;

import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.User;

import java.util.Arrays;

public final class SayCommand {
    /**
     * Sends a blue message to all players on the server with the GM's Character Name as the prefix.
     * Usage: !say <message>
     */
    @Command("say")
    @Arguments("message")
    public static void say(User user, String[] args) {
        // Join all args except the command itself
        final String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        final String formattedMessage = "[" + user.getCharacterName()  +"] " + message;
        user.getConnectedServer().broadcastServerNoticeWithoutPrefix(formattedMessage);
    }
}
