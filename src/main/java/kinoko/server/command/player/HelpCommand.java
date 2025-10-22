package kinoko.server.command.player;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.user.User;
import kinoko.server.command.CommandProcessor;
import kinoko.world.user.stat.AdminLevel;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class HelpCommand {

    /**
     * Displays a list of all commands the user can access, or syntax for a specific command.
     *
     * If called with no arguments (e.g., !help), it lists all commands available to the user.
     * If called with a command name (e.g., !help meso), it shows the syntax for that specific command.
     *
     * @param user the user executing the command
     * @param args command arguments
     */
    @Command("help")
    public static void help(User user, String[] args) {
        AdminLevel userLevel = user.getAdminLevel();

        // If just "!help" or "@help" -> list all accessible commands
        if (args.length == 1) {
            user.write(MessagePacket.system("Available Commands:"));

            // Use a set to avoid duplicate methods caused by multiple aliases
            Set<Method> uniqueMethods = new HashSet<>(CommandProcessor.getCommandMap().values());

            for (Method method : uniqueMethods) {
                AdminLevel requiredLevel = CommandProcessor.getRequiredLevel(method);
                if (!userLevel.isAtLeast(requiredLevel)) continue;

                user.write(MessagePacket.system("%s", CommandProcessor.getHelpString(method)));
            }
        }
        // "!help <command>" -> show syntax only
        else {
            String commandName = args[1].toLowerCase();
            Optional<Method> commandResult = CommandProcessor.getCommand(commandName);

            if (commandResult.isEmpty()) {
                user.write(MessagePacket.system("Unknown command: %s", commandName));
                return;
            }

            Method method = commandResult.get();
            AdminLevel requiredLevel = CommandProcessor.getRequiredLevel(method);
            if (!userLevel.isAtLeast(requiredLevel)) {
                user.write(MessagePacket.system("Unknown command: %s", commandName));
                return;
            }

            user.write(MessagePacket.system("Syntax: %s", CommandProcessor.getHelpString(method)));
        }
    }
}
