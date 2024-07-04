package kinoko.server.script;

import kinoko.provider.map.PortalInfo;
import kinoko.server.ServerConfig;
import kinoko.world.field.FieldObject;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ScriptDispatcher {
    public static final Path NPC_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "npc");
    public static final Path ITEM_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "item");
    public static final Path QUEST_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "quest");
    public static final Path PORTAL_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "portal");
    public static final Path REACTOR_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "reactor");
    public static final Path FIELD_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "field");
    public static final Path TEST_SCRIPT = Path.of(ServerConfig.SCRIPT_DIRECTORY, "test.py");
    public static final String SCRIPT_EXTENSION = ".py";
    public static final String SCRIPT_LANGUAGE = "python";

    private static final Logger log = LogManager.getLogger(ScriptDispatcher.class);
    private static ExecutorService executor;
    private static Engine engine;

    public static void initialize() throws IOException {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
        // Evaluate test script (also handle the delay on first script execution)
        final Context context = createContext();
        context.eval(
                Source.newBuilder(SCRIPT_LANGUAGE, TEST_SCRIPT.toFile())
                        .build()
        );
        context.close();
    }

    public static void shutdown() {
        executor.shutdown();
        engine.close(true);
    }

    public static void startNpcScript(User user, FieldObject source, String scriptName, int speakerId) {
        startScript(ScriptType.NPC, Path.of(NPC_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION), user, source, speakerId);
    }

    public static void startItemScript(User user, String scriptName, int speakerId) {
        startScript(ScriptType.ITEM, Path.of(ITEM_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION), user, user, speakerId);
    }

    public static void startQuestScript(User user, int questId, boolean isStart, int speakerId) {
        final String scriptName = String.format("q%d%s", questId, isStart ? "s" : "e");
        startScript(ScriptType.QUEST, Path.of(QUEST_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION), user, user, speakerId);
    }

    public static void startPortalScript(User user, PortalInfo portalInfo, int speakerId) {
        final String scriptName = portalInfo.getScript();
        startScript(ScriptType.PORTAL, Path.of(PORTAL_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION), user, user, speakerId);
    }

    public static void startReactorScript(User user, Reactor reactor, String scriptName, int speakerId) {
        startScript(ScriptType.PORTAL, Path.of(REACTOR_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION), user, reactor, speakerId);
    }

    public static void startFirstUserEnterScript(User user, String scriptName, int speakerId) {
        startScript(ScriptType.FIRST_USER_ENTER, Path.of(FIELD_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION), user, user, speakerId);
    }

    public static void startUserEnterScript(User user, String scriptName, int speakerId) {
        startScript(ScriptType.USER_ENTER, Path.of(FIELD_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION), user, user, speakerId);
    }

    private static void startScript(ScriptType scriptType, Path scriptPath, User user, FieldObject source, int speakerId) {
        // Initialize context
        final Context context = createContext();
        final ScriptManager scriptManager = new ScriptManager(context, user, source, speakerId);
        context.getBindings(SCRIPT_LANGUAGE).putMember("sm", scriptManager);
        // Evaluate script with virtual thread executor
        executor.submit(() -> {
            try {
                log.debug("Evaluating {} script file : {}, {}", scriptType.name(), scriptPath, context);
                user.lock();
                context.eval(
                        Source.newBuilder(SCRIPT_LANGUAGE, scriptPath.toFile())
                                .cached(true)
                                .build()
                );
            } catch (PolyglotException e) {
                if (!e.isCancelled()) {
                    log.error("Error while evaluating {} script file : {}", scriptType.name(), scriptPath, e);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                log.error("Error while loading {} script file : {}", scriptType.name(), scriptPath, e);
                if (scriptType == ScriptType.PORTAL) {
                    user.dispose();
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
