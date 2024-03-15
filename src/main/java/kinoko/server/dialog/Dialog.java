package kinoko.server.dialog;

import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

public interface Dialog {
    void onPacket(Locked<User> locked, InPacket inPacket);
}
