package kinoko.server.command;

import kinoko.packet.world.MessagePacket;
import kinoko.server.ServerConfig;
import kinoko.util.ClassScanner;
import kinoko.world.user.User;
import kinoko.world.user.stat.AdminLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class CommandProcessor {
    private static final Logger log = LogManager.getLogger(CommandProcessor.class);
    private static final Map<String, Method> commandMap = new HashMap<>();
    private static final Map<Method, AdminLevel> methodLevelMap = new HashMap<>();

    /**
     * Returns an unmodifiable map of all registered command aliases to their corresponding methods.
     * This allows iterating over all commands without modifying the internal command registry.
     *
     * @return an unmodifiable view of the command map
     */
    public static Map<String, Method> getCommandMap() {
        return Collections.unmodifiableMap(commandMap);
    }

    /**
     * Returns the required admin level for the given command method.
     * If the method is not registered or has no level explicitly set, it defaults to PLAYER.
     *
     * @param method the command method to query
     * @return the admin level required to execute the command
     */
    public static AdminLevel getRequiredLevel(Method method) {
        return methodLevelMap.getOrDefault(method, AdminLevel.PLAYER);
    }


    public static void initialize() {
        // List of packages to scan for command classes
        String[] commandPackages = new String[]{
                "kinoko.server.command.admin",
                "kinoko.server.command.manager",
                "kinoko.server.command.supergm",
                "kinoko.server.command.gm",
                "kinoko.server.command.jrgm",
                "kinoko.server.command.tester",
                "kinoko.server.command.player"
        };

        Map<String, AdminLevel> packageLevels = Map.of(
                "admin", AdminLevel.ADMIN,
                "manager", AdminLevel.MANAGER,
                "supergm", AdminLevel.SUPER_GM,
                "gm", AdminLevel.GM,
                "jrgm", AdminLevel.JR_GM,
                "tester", AdminLevel.TESTER,
                "player", AdminLevel.PLAYER
        );

        for (String pkg : commandPackages) {
            // Get all classes in the package
            Set<Class<?>> classes = ClassScanner.getClasses(pkg);
            for (Class<?> clazz : classes) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (!method.isAnnotationPresent(Command.class)) {
                        continue;
                    }

                    // Validate method signature
                    if (method.getParameterCount() != 2
                            || method.getParameterTypes()[0] != User.class
                            || method.getParameterTypes()[1] != String[].class) {
                        throw new RuntimeException(
                                String.format("Incorrect parameters for command method \"%s\"", method.getName()));
                    }

                    // Determine enforced level from package
                    String pkgName = clazz.getPackageName().toLowerCase();
                    AdminLevel enforcedLevel = packageLevels.entrySet().stream()
                            .filter(e -> pkgName.endsWith(e.getKey()))
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse(AdminLevel.PLAYER);

                    // store the command's enforced admin level.
                    methodLevelMap.put(method, enforcedLevel);


                    // Register aliases
                    final Command annotation = method.getAnnotation(Command.class);
                    for (String value : annotation.value()) {
                        final String alias = value.toLowerCase();
                        if (commandMap.containsKey(alias)) {
                            throw new RuntimeException(
                                    String.format("Multiple methods found for Command alias \"%s\"", alias));
                        }
                        commandMap.put(alias, method);
                    }
                }
            }
        }

        log.info("CommandProcessor initialized with {} commands.", commandMap.size());
    }


    public static Optional<Method> getCommand(String commandName) {
        return Optional.ofNullable(commandMap.get(commandName.toLowerCase()));
    }

    public static String getHelpString(Method method) {
        final Command command = method.getAnnotation(Command.class);
        final Arguments arguments = method.getAnnotation(Arguments.class);

        // Determine the required admin level for this command
        AdminLevel requiredLevel = methodLevelMap.getOrDefault(method, AdminLevel.PLAYER);

        // Choose prefix based on the required admin level
        String prefix = (requiredLevel.isAtLeast(AdminLevel.TESTER))
                ? ServerConfig.STAFF_COMMAND_PREFIX
                : ServerConfig.PLAYER_COMMAND_PREFIX;

        final String commandString = String.join("|", command.value());
        final List<String> argumentString = Arrays.stream(arguments != null ? arguments.value() : new String[]{})
                .map(value -> String.format("<%s>", value))
                .toList();

        return String.format("%s%s %s", prefix, commandString, String.join(" ", argumentString));
    }

    public static void tryProcessCommand(User user, String text) {
        AdminLevel userLevel = user.getAdminLevel();

        // allowed command prefixes
        List<String> allowedPrefixes = new ArrayList<>();
        allowedPrefixes.add(ServerConfig.PLAYER_COMMAND_PREFIX); // everyone can use player commands
        if (userLevel.isAtLeast(AdminLevel.TESTER)) {
            allowedPrefixes.add(ServerConfig.STAFF_COMMAND_PREFIX); // Testers and higher can use staff commands
        }

        String usedPrefix = allowedPrefixes.stream()
                .filter(text::startsWith)
                .findFirst()
                .orElse(null);


        // no registered prefix found.
        if (usedPrefix == null) {
            return;
        }


        final String[] arguments = text.substring(usedPrefix.length()).trim().split(" ");
        final String commandName = arguments[0].toLowerCase();
        final Optional<Method> commandResult = getCommand(commandName);

        // no registered command found.
        if (commandResult.isEmpty()) {
            user.systemMessage("Unknown command : %s", text);
            return;
        }

        final Method method = commandResult.get();
        final Command commandAnnotation = method.getAnnotation(Command.class);

        // Check args
        if (method.isAnnotationPresent(Arguments.class)) {
            final Arguments annotation = method.getAnnotation(Arguments.class);
            if (arguments.length < annotation.value().length + 1) {
                user.systemMessage("Syntax : %s", getHelpString(method));
                return;
            }
        }

        // Check admin level
        AdminLevel requiredLevel = methodLevelMap.getOrDefault(method, AdminLevel.PLAYER);
        if (!user.getAdminLevel().isAtLeast(requiredLevel)) {
            if (ServerConfig.TESPIA) {
                user.systemMessage("You do not have permission to use this command.");  // We don't want to show this message to a normal player.
            }
            return;
        }


        // invoke command
        try {
            method.invoke(null, user, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Exception caught while processing command {}", text, e);
            user.systemMessage("Failed to process command : %s", text);
            e.printStackTrace();
        }
    }
}
