package kinoko.script.common;

import kinoko.provider.map.PortalInfo;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.FieldObject;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ScriptDispatcher {
    private static final Logger log = LogManager.getLogger(ScriptDispatcher.class);
    private static final Map<String, Method> scriptMap = new HashMap<>();
    private static ExecutorService executor;

    public static void initialize() {
        final Reflections reflections = new Reflections("kinoko.script", Scanners.SubTypes);
        for (Class<? extends ScriptHandler> clazz : reflections.getSubTypesOf(ScriptHandler.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Script.class)) {
                    continue;
                }
                if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != ScriptManager.class) {
                    throw new RuntimeException(String.format("Incorrect parameters for script method \"%s\"", method.getName()));
                }
                final Script annotation = method.getAnnotation(Script.class);
                final String scriptName = annotation.value();
                if (scriptMap.containsKey(scriptName)) {
                    throw new RuntimeException(String.format("Multiple methods found for script name \"%s\"", scriptName));
                }
                scriptMap.put(scriptName, method);
            }
        }
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public static void shutdown() {
        executor.shutdown();
    }

    public static void startNpcScript(User user, FieldObject source, String scriptName, int speakerId) {
        startScript(ScriptType.NPC, scriptName, user, source.getField(), source, speakerId);
    }

    public static void startItemScript(User user, String scriptName, int speakerId) {
        startScript(ScriptType.ITEM, scriptName, user, user.getField(), user, speakerId);
    }

    public static void startQuestScript(User user, int questId, boolean isStart, int speakerId) {
        final String scriptName = String.format("q%d%s", questId, isStart ? "s" : "e");
        startScript(ScriptType.QUEST, scriptName, user, user.getField(), user, speakerId);
    }

    public static void startPortalScript(User user, PortalInfo portalInfo) {
        final String scriptName = portalInfo.getScript();
        startScript(ScriptType.PORTAL, scriptName, user, user.getField(), user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startReactorScript(User user, Reactor reactor, String scriptName) {
        startScript(ScriptType.REACTOR, scriptName, user, reactor.getField(), reactor, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startFirstUserEnterScript(User user, String scriptName) {
        startScript(ScriptType.FIRST_USER_ENTER, scriptName, user, user.getField(), user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startUserEnterScript(User user, String scriptName) {
        startScript(ScriptType.USER_ENTER, scriptName, user, user.getField(), user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    private static void startScript(ScriptType scriptType, String scriptName, User user, Field field, FieldObject source, int speakerId) {
        // Resolve script handler
        final Method handler = scriptMap.get(scriptName);
        if (handler == null) {
            log.error("Could not resolve {} script with name : {}", scriptType, scriptName);
            if (scriptType == ScriptType.ITEM || scriptType == ScriptType.PORTAL) {
                user.dispose();
            }
            return;
        }
        // Execute script handler
        final ScriptManagerImpl scriptManager = new ScriptManagerImpl(user, field, source, speakerId);
        executor.submit(() -> {
            try {
                log.debug("Executing {} script : {}", scriptType.name(), scriptName);
                user.lock();
                handler.invoke(null, scriptManager);
            } catch (Exception e) {
                if (!(e.getCause() instanceof ScriptTermination)) {
                    log.error("Script execution failed with exception : {}", e.getCause(), e);
                    e.printStackTrace();
                }
            } finally {
                user.unlock();
                // Dispose after item scripts, and portal scripts if not warped
                if (scriptType == ScriptType.ITEM || (scriptType == ScriptType.PORTAL && user.getFieldId() == field.getFieldId())) {
                    user.dispose();
                }
            }
        });
    }
}

