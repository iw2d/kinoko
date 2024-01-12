package kinoko.server.script;

import kinoko.server.ServerConfig;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;

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
    private static final Map<Integer, ScriptManager> userScriptManagers = new ConcurrentHashMap<>();

    private static ExecutorService executor;
    private static Engine engine;

    public static void initialize() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
    }

    public static void startNpcScript(User user, int templateId, String scriptName) {
        final File scriptFile = Path.of(NPC_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION).toFile();
        if (!scriptFile.isFile()) {
            log.error("NPC script file not found : {}", scriptName);
            return;
        }
        executor.submit(() -> {
            final ScriptManager scriptManager = new UserScriptManager(user);
            scriptManager.setSpeakerId(templateId);
            userScriptManagers.put(user.getCharacterId(), scriptManager);
            final Context context = Context.newBuilder(SCRIPT_LANGUAGE)
                    .engine(engine)
                    .allowHostAccess(HostAccess.ALL)
                    .build();
            context.getBindings(SCRIPT_LANGUAGE).putMember("sm", scriptManager);
            log.debug("Evaluating script file : {}", scriptFile.getName());
            try {
                context.eval(
                        Source.newBuilder(SCRIPT_LANGUAGE, scriptFile)
                                .cached(true)
                                .build()
                );
            } catch (IOException e) {
                log.error("Error while loading script file : {}", scriptFile.getName());
            }
        });
    }

    public static Optional<ScriptManager> getUserScriptManager(User user) {
        if (!userScriptManagers.containsKey(user.getCharacterId())) {
            return Optional.empty();
        }
        return Optional.of(userScriptManagers.get(user.getCharacterId()));
    }
}
