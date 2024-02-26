package kinoko.server.script;

import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;

public final class ReactorScriptManager extends ScriptManager {
    private final Reactor reactor;

    public ReactorScriptManager(User user, Reactor reactor) {
        super(user);
        this.reactor = reactor;
    }

    @Override
    public void disposeManager() {
        ScriptDispatcher.removeScriptManager(ScriptType.REACTOR, user);
    }

    public Reactor getReactor() {
        return reactor;
    }

    public void dropRewards() {
        reactor.dropRewards(user);
    }
}
