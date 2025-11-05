package kinoko.server.command.gm;

import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.User;

import java.util.Arrays;

public final class NoticeBCommand {
    /**
     * Sends a blue message to all players on the server.
     * Usage: !noticeb <message>
     */
    @Command("noticeb")
    @Arguments("message")
    public static void notice(User user, String[] args) {
        // Join all args except the command itself
        final String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        user.getConnectedServer().broadcastServerNoticeWithoutPrefix(message);
    }
}
