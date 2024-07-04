package kinoko.server.script;

import kinoko.provider.map.PortalInfo;
import kinoko.server.ServerConfig;
import kinoko.world.GameConstants;
import kinoko.world.field.FieldObject;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public final class ScriptDispatcher {
    public static final String SCRIPT_LANGUAGE = "python";
    public static final String SCRIPT_EXTENSION = ".py";
    public static final String TEST_SCRIPT = "test";

    private static final Logger log = LogManager.getLogger(ScriptDispatcher.class);
    private static final ConcurrentHashMap<String, Source> sourceMap = new ConcurrentHashMap<>();
    private static ExecutorService executor;
    private static Engine engine;

    public static void initialize() throws IOException {
        // Initialize executor and engine
        executor = Executors.newVirtualThreadPerTaskExecutor();
        engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
        // Populate source map
        loadSourceMap();
        // Evaluate test script
        final Context context = createContext();
        context.eval(sourceMap.get(TEST_SCRIPT));
        context.close();
    }

    public static void loadSourceMap() throws IOException {
        final Map<String, Source> newSourceMap = new HashMap<>();
        try (final Stream<Path> stream = Files.walk(Path.of(ServerConfig.SCRIPT_DIRECTORY))) {
            for (Path path : stream.toList()) {
                if (!Files.isRegularFile(path) || !path.toString().endsWith(SCRIPT_EXTENSION)) {
                    continue;
                }
                final String scriptName = path.getFileName().toString().replace(SCRIPT_EXTENSION, "");
                final Source scriptSource = Source.newBuilder(SCRIPT_LANGUAGE, path.toFile()).build();
                if (newSourceMap.containsKey(scriptName)) {
                    throw new IllegalStateException("Duplicate script name : " + path);
                }
                newSourceMap.put(scriptName, scriptSource);
            }
        }
        sourceMap.putAll(newSourceMap);
    }

    public static void shutdown() {
        executor.shutdown();
        engine.close(true);
    }

    public static void startNpcScript(User user, FieldObject source, String scriptName, int speakerId) {
        startScript(ScriptType.NPC, scriptName, user, source, speakerId);
    }

    public static void startItemScript(User user, String scriptName, int speakerId) {
        startScript(ScriptType.ITEM, scriptName, user, user, speakerId);
    }

    public static void startQuestScript(User user, int questId, boolean isStart, int speakerId) {
        final String scriptName = String.format("q%d%s", questId, isStart ? "s" : "e");
        startScript(ScriptType.QUEST, scriptName, user, user, speakerId);
    }

    public static void startPortalScript(User user, PortalInfo portalInfo) {
        final String scriptName = portalInfo.getScript();
        startScript(ScriptType.PORTAL, scriptName, user, user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startReactorScript(User user, Reactor reactor, String scriptName) {
        startScript(ScriptType.PORTAL, scriptName, user, reactor, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startFirstUserEnterScript(User user, String scriptName) {
        startScript(ScriptType.FIRST_USER_ENTER, scriptName, user, user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startUserEnterScript(User user, String scriptName) {
        startScript(ScriptType.USER_ENTER, scriptName, user, user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    private static void startScript(ScriptType scriptType, String scriptName, User user, FieldObject source, int speakerId) {
        // Resolve script source
        final Source scriptSource = sourceMap.get(scriptName);
        if (scriptSource == null) {
            log.error("Could not resolve {} script file : {}", scriptType, scriptName);
            if (scriptType == ScriptType.PORTAL) {
                user.dispose();
            }
            return;
        }
        // Initialize context
        final Context context = createContext();
        final ScriptManager scriptManager = new ScriptManager(context, user, source, speakerId);
        context.getBindings(SCRIPT_LANGUAGE).putMember("sm", scriptManager);
        // Evaluate script with virtual thread executor
        executor.submit(() -> {
            try {
                log.debug("Evaluating {} script file : {}", scriptType.name(), scriptName);
                user.lock();
                context.eval(scriptSource);
            } catch (PolyglotException e) {
                if (!e.isCancelled()) {
                    log.error("Error while evaluating {} script file : {}", scriptType.name(), scriptName, e);
                    e.printStackTrace();
                }
            } finally {
                context.close(true);
                user.setDialog(null);
                user.unlock();
            }
        });
    }

    private static Context createContext() {
        return Context.newBuilder(SCRIPT_LANGUAGE)
                .engine(engine)
                .allowHostAccess(HostAccess.ALL)
                .build();
    }
}
