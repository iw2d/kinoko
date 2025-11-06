package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.user.User;

/**
 * JrGM command to set HP.
 */
public final class HpCommand {

    @Command("hp")
    @Arguments("new hp")
    public static void hp(User user, String[] args) {
        try {
            int newHp = Integer.parseInt(args[1]);
            user.setHp(newHp);
            user.systemMessage("HP set to %d", newHp);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !hp <new HP>");
        }
    }
}
