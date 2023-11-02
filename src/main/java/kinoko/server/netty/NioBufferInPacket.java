package kinoko.server.netty;

import kinoko.server.InPacket;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class NioBufferInPacket implements InPacket {
    private static final Logger log = LogManager.getLogger(InPacket.class);
    private final ByteBuffer buffer;

    public NioBufferInPacket(byte[] data) {
        this.buffer = ByteBuffer.wrap(data);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public byte decodeByte() {
        return buffer.get();
    }

    @Override
    public short decodeShort() {
        return buffer.getShort();
    }

    @Override
    public int decodeInt() {
        return buffer.getInt();
    }

    @Override
    public long decodeLong() {
        return buffer.getLong();
    }

    @Override
    public byte[] decodeArray(int length) {
        final byte[] array = new byte[length];
        buffer.get(array);
        return array;
    }

    @Override
    public String decodeString(int length) {
        return new String(decodeArray(length));
    }

    @Override
    public byte[] getData() {
        return buffer.array();
    }

    @Override
    public String decodeString() {
        final short length = decodeShort();
        return decodeString(length);
    }

    @Override
    public String toString() {
        final byte[] data = new byte[buffer.limit() - 2];
        buffer.get(2, data);
        return Util.readableByteArray(data);
    }
}
