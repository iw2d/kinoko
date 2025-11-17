package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.GameConstants;
import kinoko.world.user.User;

/**
 * Levels up the user to a specific level.
 * JrGM-level command.
 */
public final class LevelUpCommand {

    @Command("levelup")
    @Arguments("new level")
    public static void levelUp(User user, String[] args) {
        try {
            int level = Integer.parseInt(args[1]);
            if (level <= user.getLevel() || level > GameConstants.LEVEL_MAX) {
                user.systemMessage("Could not level up to : %d", level);
                return;
            }
            while (user.getLevel() < level) {
                user.addExp(GameConstants.getNextLevelExp(user.getLevel()) - user.getCharacterStat().getExp());
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !levelup <new level>");
        }
    }
}
