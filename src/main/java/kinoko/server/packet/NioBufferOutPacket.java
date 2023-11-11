package kinoko.server.packet;

import kinoko.server.header.OutHeader;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public final class NioBufferOutPacket implements OutPacket {
    private static final Logger log = LogManager.getLogger(OutPacket.class);
    private final ByteBuffer[] buffers;
    private int bufferIndex;

    public NioBufferOutPacket() {
        this.buffers = new ByteBuffer[16];
        this.bufferIndex = 0;
        // Initial size
        buffers[0] = newBuffer(16);
    }

    private void ensureSize(int size) {
        final ByteBuffer current = getBuffer();
        if (current.limit() - current.position() >= size) {
            return;
        }
        // Create new ByteBuffer
        bufferIndex++;
        buffers[bufferIndex] = newBuffer(Math.max(current.capacity() * 2, size));
    }

    private ByteBuffer getBuffer() {
        return buffers[bufferIndex];
    }

    @Override
    public void encodeByte(byte value) {
        ensureSize(1);
        getBuffer().put(value);
    }

    @Override
    public void encodeShort(short value) {
        ensureSize(2);
        getBuffer().putShort(value);
    }

    @Override
    public void encodeInt(int value) {
        ensureSize(4);
        getBuffer().putInt(value);
    }

    @Override
    public void encodeLong(long value) {
        ensureSize(8);
        getBuffer().putLong(value);
    }

    @Override
    public void encodeArray(byte[] value) {
        ensureSize(value.length);
        getBuffer().put(value);
    }

    @Override
    public void encodeString(String value, int length) {
        ensureSize(length);
        if (value.length() > length) {
            log.error("Encoding a string that is too long, string will be truncated");
            getBuffer().put(value.substring(0, length).getBytes());
        } else {
            getBuffer().put(value.getBytes());
            getBuffer().put(new byte[length - value.length()]);
        }
    }

    @Override
    public void encodeString(String value) {
        if (value.length() > Short.MAX_VALUE) {
            log.error("Encoding a string that is too long, string will be truncated");
        }
        final int length = Math.min(value.length(), Short.MAX_VALUE);
        ensureSize(2 + length);
        getBuffer().putShort((short) value.length());
        getBuffer().put(value.getBytes());
    }

    @Override
    public int getSize() {
        int size = 0;
        for (int i = 0; i < bufferIndex + 1; i++) {
            final ByteBuffer buffer = buffers[i];
            size += buffer.position();
        }
        return size;
    }

    @Override
    public byte[] getData() {
        final byte[] data = new byte[getSize()];
        int position = 0;
        for (int i = 0; i < bufferIndex + 1; i++) {
            final ByteBuffer buffer = buffers[i];
            buffer.get(0, data, position, buffer.position());
            position += buffer.position();
        }
        return data;
    }

    @Override
    public String toString() {
        final short op = buffers[0].getShort(0);
        return String.format("%s(%s) | %s", OutHeader.getByValue(op), Util.opToString(op),
                Util.readableByteArray(Arrays.copyOfRange(getData(), 2, getSize())));
    }

    private static ByteBuffer newBuffer(int capacity) {
        final ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }
}
