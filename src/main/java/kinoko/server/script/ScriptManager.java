package kinoko.server.script;

import kinoko.world.user.User;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;

public final class ScriptManager {
    public static final String CONTEXT_VARIABLE = "ctx";
    public static final String SCRIPT_LANGUAGE = "python";
    private static Engine graalEngine;

    public static void initialize() {
        graalEngine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
    }

    private static ScriptContext createContext(User user) {
        final Context graalContext = Context.newBuilder(SCRIPT_LANGUAGE)
                .engine(graalEngine)
                .allowHostAccess(HostAccess.EXPLICIT)
                .build();
        final ScriptContext scriptContext = new ScriptContext(user);
        graalContext.getBindings(SCRIPT_LANGUAGE).putMember(CONTEXT_VARIABLE, scriptContext);
        return scriptContext;
    }

    public static void startQuestScript(User user, int questId, int templateId) {
    }
}
