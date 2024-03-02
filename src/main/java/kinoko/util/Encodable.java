package kinoko.util;

import kinoko.server.packet.OutPacket;

public interface Encodable {
    void encode(OutPacket outPacket);
}
