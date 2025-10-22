package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.user.User;

/**
 * SuperGM command to clear a user's locker inventory.
 */
public final class ClearLockerCommand {

    @Command("clearlocker")
    public static void clearLocker(User user, String[] args) {
        user.getAccount().getLocker().getCashItems().clear();
        user.write(MessagePacket.system("Locker inventory cleared!"));
    }
}
