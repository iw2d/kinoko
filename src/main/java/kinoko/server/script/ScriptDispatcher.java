package kinoko.server.script;

import kinoko.provider.map.PortalInfo;
import kinoko.server.ServerConfig;
import kinoko.util.Tuple;
import kinoko.world.field.reactor.Reactor;
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
import java.util.function.Consumer;

public final class ScriptDispatcher {
    public static final Path NPC_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "npc");
    public static final Path QUEST_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "quest");
    public static final Path PORTAL_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "portal");
    public static final Path REACTOR_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "reactor");
    public static final Path FIELD_SCRIPTS = Path.of(ServerConfig.SCRIPT_DIRECTORY, "field");
    public static final String SCRIPT_EXTENSION = ".py";
    public static final String SCRIPT_LANGUAGE = "python";

    private static final Logger log = LogManager.getLogger(ScriptDispatcher.class);
    private static final Map<ScriptType, Map<Integer, Tuple<ScriptManager, Context>>> scriptManagers = Map.of(
            ScriptType.NPC, new ConcurrentHashMap<>(),
            ScriptType.PORTAL, new ConcurrentHashMap<>(),
            ScriptType.REACTOR, new ConcurrentHashMap<>(),
            ScriptType.FIRST_USER_ENTER, new ConcurrentHashMap<>(),
            ScriptType.USER_ENTER, new ConcurrentHashMap<>()
    );

    private static ExecutorService executor;
    private static Engine engine;

    public static void initialize() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
    }

    public static Optional<NpcScriptManager> getNpcScriptManager(User user) {
        return Optional.ofNullable((NpcScriptManager) scriptManagers.get(ScriptType.NPC).get(user.getCharacterId()).getLeft());
    }

    public static void removeScriptManager(User user) {
        for (ScriptType scriptType : ScriptType.values()) {
            removeScriptManager(scriptType, user);
        }
    }

    public static void removeScriptManager(ScriptType scriptType, User user) {
        final Tuple<ScriptManager, Context> tuple = scriptManagers.get(scriptType).remove(user.getCharacterId());
        if (tuple != null) {
            tuple.getRight().close(true);
        }
    }

    public static void startNpcScript(User user, int speakerId, String scriptName) {
        if (scriptManagers.get(ScriptType.NPC).containsKey(user.getCharacterId())) {
            log.error("Cannot start npc script {}, another npc script already being evaluated.", scriptName);
            return;
        }
        final NpcScriptManager scriptManager = new NpcScriptManager(user, speakerId);
        startScript(ScriptType.NPC, scriptManager, Path.of(NPC_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION).toFile(), (context) -> {
            context.getBindings(SCRIPT_LANGUAGE).putMember("npcId", speakerId);
        });
    }

    public static void startQuestScript(User user, int speakerId, int questId, boolean isStart) {
        final String scriptName = String.format("q%d%s", questId, isStart ? "s" : "e");
        if (scriptManagers.get(ScriptType.NPC).containsKey(user.getCharacterId())) {
            log.error("Cannot start quest script {}, another npc script already being evaluated.", scriptName);
            return;
        }
        final NpcScriptManager scriptManager = new NpcScriptManager(user, speakerId);
        startScript(ScriptType.NPC, scriptManager, Path.of(QUEST_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION).toFile(), (context) -> {
            context.getBindings(SCRIPT_LANGUAGE).putMember("npcId", speakerId);
            context.getBindings(SCRIPT_LANGUAGE).putMember("questId", questId);
        });
    }

    public static void startPortalScript(User user, PortalInfo portalInfo) {
        final String scriptName = portalInfo.getScript();
        if (scriptManagers.get(ScriptType.PORTAL).containsKey(user.getCharacterId())) {
            log.error("Cannot start portal script {}, another script already being evaluated.", scriptName);
            return;
        }
        final PortalScriptManager scriptManager = new PortalScriptManager(user, portalInfo);
        startScript(ScriptType.PORTAL, scriptManager, Path.of(PORTAL_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION).toFile(), (context) -> {
            context.getBindings(SCRIPT_LANGUAGE).putMember("fieldId", user.getField().getFieldId());
            context.getBindings(SCRIPT_LANGUAGE).putMember("portal", portalInfo);
        });
    }

    public static void startReactorScript(User user, Reactor reactor) {
        final String scriptName = reactor.getAction();
        if (scriptManagers.get(ScriptType.REACTOR).containsKey(user.getCharacterId())) {
            log.error("Cannot start reactor script {}, another script already being evaluated.", scriptName);
            return;
        }
        final ReactorScriptManager scriptManager = new ReactorScriptManager(user, reactor);
        startScript(ScriptType.REACTOR, scriptManager, Path.of(REACTOR_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION).toFile(), (context) -> {
            context.getBindings(SCRIPT_LANGUAGE).putMember("fieldId", user.getField().getFieldId());
            context.getBindings(SCRIPT_LANGUAGE).putMember("reactor", reactor);
        });
    }

    public static void startFirstUserEnterScript(User user, String scriptName) {
        if (scriptManagers.get(ScriptType.FIRST_USER_ENTER).containsKey(user.getCharacterId())) {
            log.error("Cannot start onFirstUserEnter script {}, another script already being evaluated.", scriptName);
            return;
        }
        final FieldScriptManager scriptManager = new FieldScriptManager(user, user.getField(), true);
        startScript(ScriptType.FIRST_USER_ENTER, scriptManager, Path.of(FIELD_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION).toFile(), (context) -> {
            context.getBindings(SCRIPT_LANGUAGE).putMember("fieldId", user.getField().getFieldId());
        });
    }

    public static void startUserEnterScript(User user, String scriptName) {
        if (scriptManagers.get(ScriptType.USER_ENTER).containsKey(user.getCharacterId())) {
            log.error("Cannot start userEnterScript script {}, another script already being evaluated.", scriptName);
            return;
        }
        final FieldScriptManager scriptManager = new FieldScriptManager(user, user.getField(), false);
        startScript(ScriptType.USER_ENTER, scriptManager, Path.of(FIELD_SCRIPTS.toString(), scriptName + SCRIPT_EXTENSION).toFile(), (context) -> {
            context.getBindings(SCRIPT_LANGUAGE).putMember("fieldId", user.getField().getFieldId());
        });
    }

    private static void startScript(ScriptType scriptType, ScriptManager scriptManager, File scriptFile, Consumer<Context> consumer) {
        final Context context = Context.newBuilder(SCRIPT_LANGUAGE)
                .engine(engine)
                .allowHostAccess(HostAccess.ALL)
                .build();
        context.getBindings(SCRIPT_LANGUAGE).putMember("sm", scriptManager);
        consumer.accept(context); // add bindings
        // Evaluate script with virtual thread executor
        final User user = scriptManager.getUser();
        scriptManagers.get(scriptType).put(user.getCharacterId(), new Tuple<>(scriptManager, context));
        executor.submit(() -> {
            try {
                log.debug("Evaluating {} script file : {}", scriptType.name(), scriptFile.getPath());
                user.lock();
                context.eval(
                        Source.newBuilder(SCRIPT_LANGUAGE, scriptFile)
                                .cached(true)
                                .build()
                );
            } catch (PolyglotException e) {
                if (!e.isCancelled()) {
                    log.error("Error while evaluating {} script file : {}", scriptType.name(), scriptFile.getPath(), e);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                log.error("Error while loading {} script file : {}", scriptType.name(), scriptFile.getPath(), e);
            } finally {
                scriptManager.disposeManager();
                user.unlock();
            }
        });
    }
}
