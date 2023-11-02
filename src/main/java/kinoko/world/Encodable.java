package kinoko.world;

import kinoko.server.OutPacket;

public interface Encodable {
    void encode(OutPacket outPacket);
}
