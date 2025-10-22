package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.GameConstants;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;

/**
 * Sets the user's level directly.
 * JrGM-level command.
 */
public final class LevelCommand {

    @Command("level")
    @Arguments("new level")
    public static void level(User user, String[] args) {
        try {
            int level = Integer.parseInt(args[1]);
            if (level < 1 || level > GameConstants.LEVEL_MAX) {
                user.write(MessagePacket.system("Could not change level to : %d", level));
                return;
            }

            CharacterStat cs = user.getCharacterStat();
            cs.setLevel((short) level);
            user.validateStat();
            user.write(WvsContext.statChanged(Stat.LEVEL, (byte) cs.getLevel(), true));
            user.getConnectedServer().notifyUserUpdate(user);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !level <new level>"));
        }
    }
}
