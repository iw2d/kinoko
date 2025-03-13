package kinoko.server.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import kinoko.server.header.OutHeader;
import kinoko.util.Util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class NettyOutPacket implements OutPacket {

    private final ByteBuf buffer;

    public NettyOutPacket() {
        this.buffer = Unpooled.buffer(16);
    }

    @Override
    public void encodeByte(byte value) {
        buffer.writeByte(value);
    }

    @Override
    public void encodeShort(short value) {
        buffer.writeShortLE(value);
    }

    @Override
    public void encodeInt(int value) {
        buffer.writeIntLE(value);
    }

    @Override
    public void encodeLong(long value) {
        buffer.writeLongLE(value);
    }

    @Override
    public void encodeDouble(double value) {
        buffer.writeDoubleLE(value);
    }

    @Override
    public void encodeArray(byte[] value) {
        buffer.writeBytes(value);
    }

    @Override
    public void encodeString(String value, int length) {
        if (value == null) {
            value = "";
        }
        if (value.length() > length) {
            buffer.writeBytes(value.getBytes(StandardCharsets.US_ASCII), 0, length);
        } else {
            buffer.writeBytes(value.getBytes(StandardCharsets.US_ASCII));
            buffer.writeZero(length - value.length());
        }
    }

    @Override
    public void encodeString(String value) {
        if (value == null) {
            value = "";
        }
        buffer.writeShortLE(value.length());
        buffer.writeBytes(value.getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    public int getSize() {
        return buffer.writerIndex();
    }

    @Override
    public OutHeader getHeader() {
        if (buffer.writerIndex() >= 2) {
            final short op = buffer.getShortLE(0);
            return OutHeader.getByValue(op);
        }
        return OutHeader.NO;
    }

    @Override
    public byte[] getData() {
        final byte[] data = new byte[getSize()];
        buffer.getBytes(0, data);
        return data;
    }

    @Override
    public String toString() {
        final OutHeader header = getHeader();
        return String.format("%s(%s) | %s", header, Util.opToString(header.getValue()),
                Util.readableByteArray(Arrays.copyOfRange(getData(), 2, getSize())));
    }
}
