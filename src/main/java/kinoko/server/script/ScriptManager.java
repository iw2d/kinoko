package kinoko.server.script;

import kinoko.world.user.User;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;

public final class ScriptManager {
    public static final String SCRIPT_LANGUAGE = "python";
    private static Engine graalEngine;

    public static void initialize() {
        graalEngine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
    }

    public static ScriptContext createContext(User user) {
        final Context graalContext = Context.newBuilder(SCRIPT_LANGUAGE)
                .engine(graalEngine)
                .build();
        return new ScriptContext(graalContext, user);
    }
}
