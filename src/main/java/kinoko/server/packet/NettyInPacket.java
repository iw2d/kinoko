package kinoko.server.packet;

import io.netty.buffer.ByteBuf;
import kinoko.util.Util;

import java.nio.charset.StandardCharsets;

public final class NettyInPacket implements InPacket {
    private final ByteBuf buffer;

    public NettyInPacket(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public byte peekByte() {
        return buffer.getByte(buffer.readerIndex());
    }

    @Override
    public byte decodeByte() {
        return buffer.readByte();
    }

    @Override
    public short decodeShort() {
        return buffer.readShortLE();
    }

    @Override
    public int decodeInt() {
        return buffer.readIntLE();
    }

    @Override
    public long decodeLong() {
        return buffer.readLongLE();
    }

    @Override
    public byte[] decodeArray(int length) {
        final byte[] array = new byte[length];
        buffer.readBytes(array);
        return array;
    }

    @Override
    public String decodeString(int length) {
        return new String(decodeArray(length), StandardCharsets.US_ASCII);
    }

    @Override
    public String decodeString() {
        final short length = decodeShort();
        return new String(decodeArray(length), StandardCharsets.US_ASCII);
    }

    @Override
    public byte[] getData() {
        final byte[] data = new byte[buffer.capacity()];
        buffer.getBytes(0, data);
        return data;
    }

    @Override
    public int getRemaining() {
        return buffer.readableBytes();
    }

    @Override
    public String toString() {
        final byte[] data = new byte[buffer.capacity() - 2];
        buffer.getBytes(2, data);
        return Util.readableByteArray(data);
    }

    @Override
    public void release() {
        buffer.release();
    }
}
