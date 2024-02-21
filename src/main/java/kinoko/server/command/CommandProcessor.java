package kinoko.server.command;

import kinoko.server.ServerConfig;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
                Command annotation = method.getAnnotation(Command.class);
                for (String alias : annotation.value()) {
                    if (commandMap.containsKey(alias)) {
                        throw new RuntimeException(String.format("Multiple methods found for Command alias \"%s\"", alias));
                    }
                    commandMap.put(alias, method);
                }
            }
        }
    }

    public static boolean tryProcessCommand(User user, String text) {
        final String[] args = text.replaceFirst(ServerConfig.COMMAND_PREFIX, "").split(" ");
        if (!commandMap.containsKey(args[0].toLowerCase())) {
            return false;
        }
        try {
            commandMap.get(args[0]).invoke(null, user, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Exception caught while processing command {}", text, e);
            e.printStackTrace();
        }
        return true;
    }
}
