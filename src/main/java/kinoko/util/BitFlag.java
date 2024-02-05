package kinoko.util;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

public final class BitFlag<T extends BitIndex> implements Encodable {
    private final int[] flags;

    public BitFlag(int size) {
        assert (size % 32 == 0);
        this.flags = new int[size / 32];
    }

    public boolean hasFlag(T bitIndex) {
        return (flags[bitIndex.getArrayIndex()] & bitIndex.getBitPosition()) != 0;
    }

    public void setFlag(T bitIndex) {
        this.flags[bitIndex.getArrayIndex()] |= bitIndex.getBitPosition();
    }

    @Override
    public void encode(OutPacket outPacket) {
        for (int flag : flags) {
            outPacket.encodeInt(flag);
        }
    }
}
