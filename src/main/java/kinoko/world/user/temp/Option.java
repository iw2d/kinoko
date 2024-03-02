package kinoko.world.user.temp;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public class Option implements Encodable {
    public int nOption;
    public int rOption;
    public int tOption;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeShort(nOption);
        outPacket.encodeInt(rOption);
        outPacket.encodeInt(tOption);
    }
}
