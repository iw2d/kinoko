package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public interface ControlledObject extends FieldObject {
    OutPacket changeControllerPacket(boolean forController);
}
