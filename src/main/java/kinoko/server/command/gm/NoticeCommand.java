package kinoko.server.command.gm;

import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.User;

import java.util.Arrays;

public final class NoticeCommand {
    /**
     * Sends a blue notice message to all players on the server.
     * Usage: !notice <message>
     */
    @Command("notice")
    @Arguments("message")
    public static void notice(User user, String[] args) {
        // Join all args except the command itself
        final String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        final String formattedMessage = "[Notice] " + message;
        user.getConnectedServer().broadcastServerNoticeWithoutPrefix(formattedMessage);
    }
}
