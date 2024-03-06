package kinoko.util;

import kinoko.server.packet.OutPacket;

import java.util.Arrays;
import java.util.Set;

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

    public void clear() {
        Arrays.fill(this.flags, 0);
    }

    @Override
    public void encode(OutPacket outPacket) {
        for (int i = flags.length - 1; i >= 0; i--) {
            outPacket.encodeInt(flags[i]);
        }
    }

    public static <T extends BitIndex> BitFlag<T> from(Set<T> flagSet, int size) {
        final BitFlag<T> bitFlag = new BitFlag<>(size);
        for (T flag : flagSet) {
            bitFlag.setFlag(flag);
        }
        return bitFlag;
    }
}
