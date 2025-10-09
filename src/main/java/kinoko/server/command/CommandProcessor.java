package kinoko.server.command;

import kinoko.packet.world.MessagePacket;
import kinoko.server.ServerConfig;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class CommandProcessor {
    private static final Logger log = LogManager.getLogger(CommandProcessor.class);
    private static final Map<String, Method> commandMap = new HashMap<>();

    public static void initialize() {
        for (Class<?> clazz : new Class[]{ AdminCommands.class }) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Command.class)) {
                    continue;
                }
                if (method.getParameterCount() != 2 || method.getParameterTypes()[0] != User.class || method.getParameterTypes()[1] != String[].class) {
                    throw new RuntimeException(String.format("Incorrect parameters for command method \"%s\"", method.getName()));
                }
                final Command annotation = method.getAnnotation(Command.class);
                for (String value : annotation.value()) {
                    final String alias = value.toLowerCase();
                    if (commandMap.containsKey(alias)) {
                        throw new RuntimeException(String.format("Multiple methods found for Command alias \"%s\"", alias));
                    }
                    commandMap.put(alias, method);
                }
            }
        }
    }

    public static Optional<Method> getCommand(String commandName) {
        return Optional.ofNullable(commandMap.get(commandName.toLowerCase()));
    }

    public static String getHelpString(Method method) {
        final Command command = method.getAnnotation(Command.class);
        final Arguments arguments = method.getAnnotation(Arguments.class);
        final String commandString = String.join("|", command.value());
        final List<String> argumentString = Arrays.stream(arguments != null ? arguments.value() : new String[]{}).map((value) -> String.format("<%s>", value)).toList();
        return String.format("%s%s %s", ServerConfig.PLAYER_COMMAND_PREFIX, commandString, String.join(" ", argumentString));
    }

    public static void tryProcessCommand(User user, String text) {
        final String[] arguments = text.replaceFirst(ServerConfig.PLAYER_COMMAND_PREFIX, "").split(" ");
        final String commandName = arguments[0].toLowerCase();
        final Optional<Method> commandResult = getCommand(commandName);
        if (commandResult.isEmpty()) {
            user.write(MessagePacket.system("Unknown command : %s", text));
            return;
        }
        final Method method = commandResult.get();
        if (method.isAnnotationPresent(Arguments.class)) {
            final Arguments annotation = method.getAnnotation(Arguments.class);
            if (arguments.length < annotation.value().length + 1) {
                user.write(MessagePacket.system("Syntax : %s", getHelpString(method)));
                return;
            }
        }
        try {
            method.invoke(null, user, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Exception caught while processing command {}", text, e);
            user.write(MessagePacket.system("Failed to process command : %s", text));
            e.printStackTrace();
        }
    }
}
