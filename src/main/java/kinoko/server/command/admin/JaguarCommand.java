package kinoko.server.command.admin;

import kinoko.packet.world.WvsContext;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.user.User;

/**
 * Sets the Wild Hunter mount (riding type) for the user.
 * Admin-level command.
 */
public final class JaguarCommand {

    @Command("jaguar")
    @Arguments("index")
    public static void jaguar(User user, String[] args) {
        try {
            int index = Integer.parseInt(args[1]);
            user.getWildHunterInfo().setRidingType(index);
            user.write(WvsContext.wildHunterInfo(user.getWildHunterInfo()));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(kinoko.packet.world.MessagePacket.system("Usage: !jaguar <index>"));
        }
    }
}
