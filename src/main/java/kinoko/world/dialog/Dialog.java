package kinoko.world.dialog;

import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

public interface Dialog {
    void onPacket(Locked<User> user, InPacket inPacket);
}
