package kinoko.server.command.manager;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.User;
import kinoko.world.user.stat.AdminLevel;

import java.util.Optional;

public class SetGmLevelCommand {
    /**
     * Sets the admin level of a target user.
     * Usage: !setadmin <username> <level 0-6>
     * Level 0 = Admin, Level 6 = Player.
     * Only users currently connected can be targeted.
     * Notifies both the issuer and the target of the change.
     *
     * @param user The user issuing the command.
     * @param args Command arguments: target username and desired admin level.
     */
    @Command({"setgmlevel", "setadminlevel", "setadmin"})
    @Arguments({ "Character Name", "Admin Level" })
    public static void setGMLevel(User user, String[] args) {
        if (args.length < 3) {
            user.systemMessage("Usage: !setadmin <username> <level 0-6>");
            return;
        }

        final String targetName = args[1];
        final String levelStr = args[2];

        int level;
        try {
            level = Integer.parseInt(levelStr);
        } catch (NumberFormatException e) {
            user.systemMessage("Invalid level: %s", levelStr);
            return;
        }

        if (level < 0 || level > 6) {
            user.systemMessage("Admin level must be between 0 (Admin) and 6 (Player).");
            return;
        }

        Optional<User> target = user.getConnectedServer().getUserByCharacterName(targetName);
        if (target.isEmpty()) {
            user.systemMessage("User not found: %s", targetName);
            return;
        }

        User targetUser = target.get();

        AdminLevel adminLevel = AdminLevel.fromValue((short) level);
        targetUser.getCharacterStat().setAdminLevel(adminLevel);

        user.systemMessage("Set admin level of %s to %s (%d)", targetName, adminLevel.name(), level);
        targetUser.systemMessage("Your admin level has been set to %s by %s", adminLevel.name(), user.getCharacterName());
    }
}
