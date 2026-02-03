package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.user.User;

/**
 * SuperGM command to enable or disable mob spawns in the current field.
 */
public final class ToggleMobCommand {

    @Command("togglemob")
    @Arguments("true/false")
    public static void disableMob(User user, String[] args) {
        try {
            if (args[1].equalsIgnoreCase("true")) {
                user.getField().setMobSpawn(true);
                user.systemMessage("Enabled mob spawns");
            } else if (args[1].equalsIgnoreCase("false")) {
                user.getField().setMobSpawn(false);
                user.systemMessage("Disabled mob spawns");
            } else {
                user.systemMessage("Usage: !togglemob <true/false>");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !togglemob <true/false>");
        }
    }
}
