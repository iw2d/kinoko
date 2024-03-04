package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public class Option implements Encodable {
    public static final Option EMPTY = new Option();
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
