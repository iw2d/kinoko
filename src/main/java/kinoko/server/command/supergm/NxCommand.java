package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.user.User;

/**
 * SuperGM command to set a user's NX prepaid balance.
 */
public final class NxCommand {

    @Command("nx")
    @Arguments("amount")
    public static void nx(User user, String[] args) {
        try {
            int nx = Integer.parseInt(args[1]);
            user.getAccount().setNxPrepaid(nx);
            user.systemMessage("Set NX prepaid to %d", nx);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !nx <amount>");
        }
    }
}
