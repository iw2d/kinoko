package kinoko.server.script;

import kinoko.packet.script.ScriptPacket;
import kinoko.world.user.User;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

public final class ScriptContext {
    private final Context context;
    private final User user;

    public ScriptContext(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @HostAccess.Export
    public void sendSay() {
        user.write(ScriptPacket.scriptMessage(null));
    }
}
