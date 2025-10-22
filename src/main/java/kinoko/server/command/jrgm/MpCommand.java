package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.user.User;

/**
 * JrGM command to set MP.
 */
public final class MpCommand {

    @Command("mp")
    @Arguments("new mp")
    public static void mp(User user, String[] args) {
        try {
            int newMp = Integer.parseInt(args[1]);
            user.setMp(newMp);
            user.write(MessagePacket.system("MP set to %d", newMp));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !mp <new MP>"));
        }
    }
}
