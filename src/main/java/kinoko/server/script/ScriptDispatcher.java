package kinoko.server.script;

import kinoko.server.ServerConfig;
import kinoko.util.Tuple;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ScriptDispatcher {
    public static final Path NPC_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "npc");
    public static final String SCRIPT_EXTENSION = ".py";
    public static final String SCRIPT_LANGUAGE = "python";

    private static final Logger log = LogManager.getLogger(ScriptManager.class);
    private static final Map<Integer, Tuple<ScriptManager, Context>> scriptManagers = new ConcurrentHashMap<>();

    private static ExecutorService executor;
    private static Engine engine;

    public static void initialize() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
    }

    public static void startNpcScript(User user, int templateId, String scriptName) {
        if (scriptManagers.containsKey(user.getCharacterId())) {
            log.error("Script already being evaluated.");
            return;
        }
        final File scriptFile = Path.of(NPC_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION).toFile();
        if (!scriptFile.isFile()) {
            log.error("Npc script file not found : {}", scriptName);
            return;
        }
        // Create ScriptManager instance and polyglot Context
        final ScriptManager scriptManager = new ScriptManager(user);
        scriptManager.setSpeakerId(templateId);
        final Context context = Context.newBuilder(SCRIPT_LANGUAGE)
                .engine(engine)
                .allowHostAccess(HostAccess.ALL)
                .build();
        context.getBindings(SCRIPT_LANGUAGE).putMember("user", user);
        context.getBindings(SCRIPT_LANGUAGE).putMember("sm", scriptManager);
        scriptManagers.put(user.getCharacterId(), new Tuple<>(scriptManager, context));
        // Evaluate script with virtual thread executor
        executor.submit(() -> {
            try {
                log.debug("Evaluating script file : {}", scriptFile.getName());
                context.eval(
                        Source.newBuilder(SCRIPT_LANGUAGE, scriptFile)
                                .cached(true)
                                .build()
                );
                removeScriptManager(user);
            } catch (PolyglotException e) {
                if (!e.isCancelled()) {
                    log.error("Error while evaluating script file : {}", scriptFile.getName(), e);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                log.error("Error while loading script file : {}", scriptFile.getName());
            }
        });
    }

    public static Optional<ScriptManager> getScriptManager(User user) {
        final Tuple<ScriptManager, Context> tuple = scriptManagers.get(user.getCharacterId());
        if (tuple == null) {
            return Optional.empty();
        }
        return Optional.of(tuple.getLeft());
    }

    public static void removeScriptManager(User user) {
        final Tuple<ScriptManager, Context> tuple = scriptManagers.remove(user.getCharacterId());
        if (tuple != null) {
            tuple.getRight().close(true);
        }
    }
}
