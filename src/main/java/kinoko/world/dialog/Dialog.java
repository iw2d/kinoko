package kinoko.world.dialog;

import kinoko.server.packet.InPacket;
import kinoko.world.user.User;

public interface Dialog {
    void onPacket(User user, InPacket inPacket);
}
