package kinoko.world.field;

import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

public interface ControlledObject extends FieldObject {
    User getController();

    void setController(User controller);

    OutPacket changeControllerPacket(boolean forController);
}
