package kinoko.server.command.gm;

import kinoko.packet.world.BroadcastPacket;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

import java.util.Arrays;

public final class NoticeRCommand {
    /**
     * Broadcasts a pop-up notice message to all players on the channel.
     * Usage: !noticer <message>
     */
    @Command("noticer")
    @Arguments("message")
    public static void notice(User user, String[] args) {
        // Join all args except the command itself
        final String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        user.getConnectedServer().broadcastServerAlert(message);

    }
}
