package kinoko.server.script;

import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

public final class UserScriptManager extends ScriptManager {
    private final User user;

    UserScriptManager(User user) {
        this.user = user;
    }

    @Override
    void write(OutPacket outPacket) {
        user.write(outPacket);
    }
}
