package kinoko.world.field.life.mob;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

public final class MobStatOption implements Encodable {
    public int nOption;
    public int rOption;
    public int tOption;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeShort(nOption);
        outPacket.encodeInt(rOption);
        outPacket.encodeShort(tOption);
    }
}
