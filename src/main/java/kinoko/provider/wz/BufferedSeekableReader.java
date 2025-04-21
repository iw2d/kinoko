// src/main/java/kinoko/provider/wz/BufferedSeekableReader.java
package kinoko.provider.wz;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;

public class BufferedSeekableReader implements Closeable {
    private final SeekableByteChannel input;
    private final ByteBuffer buffer;
    private long position;    // logical read position

    public BufferedSeekableReader(FileChannel file) {
        this((SeekableByteChannel) file, 1024);
    }

    public BufferedSeekableReader(SeekableByteChannel input) {
        this(input, 1024);
    }

    public BufferedSeekableReader(SeekableByteChannel input, int bufferSize) {
        this.input = input;
        this.buffer = ByteBuffer.allocate(bufferSize);
        // start with an “empty” buffer
        this.buffer.limit(0);
        this.position = 0;
    }

    /**
     * Ensures there are at least {@code bytesNeeded} unread bytes in the buffer,
     * refilling it (compacting the old unread data) if necessary.
     */
    private void refillBuffer(int bytesNeeded) throws IOException {
        if (buffer.remaining() >= bytesNeeded) {
            return;
        }
        // compact unread bytes to front
        buffer.compact();
        int bytesRead = input.read(buffer);
        if (bytesRead == -1) {
            throw new EOFException("Unexpected end of stream");
        }
        buffer.flip();
        if (buffer.remaining() < bytesNeeded) {
            throw new EOFException("Could not read enough bytes, needed "
                    + bytesNeeded + " but only "
                    + buffer.remaining() + " available");
        }
    }

    public byte readByte() throws IOException {
        refillBuffer(1);
        byte b = buffer.get();
        position += 1;
        return b;
    }

    public short readShortLE() throws IOException {
        refillBuffer(2);
        // set little‑endian just for this get
        short v = buffer.order(ByteOrder.LITTLE_ENDIAN).getShort();
        position += 2;
        return v;
    }

    public int readIntLE() throws IOException {
        refillBuffer(4);
        int v = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
        position += 4;
        return v;
    }

    public long readLongLE() throws IOException {
        refillBuffer(8);
        long v = buffer.order(ByteOrder.LITTLE_ENDIAN).getLong();
        position += 8;
        return v;
    }

    public byte[] readNBytes(int n) throws IOException {
        byte[] result = new byte[n];
        int offset = 0;
        while (offset < n) {
            refillBuffer(1);
            int toRead = Math.min(buffer.remaining(), n - offset);
            buffer.get(result, offset, toRead);
            offset += toRead;
            position += toRead;
        }
        return result;
    }

    /** Returns the logical read position (number of bytes read so far, or position after a seek). */
    public long position() {
        return position;
    }

    /**
     * Seeks to newPosition.  Only forward or backward by resetting
     * buffer and repositioning the channel.
     */
    public void seek(long newPosition) throws IOException {
        if (newPosition == position) {
            return;
        }
        // throw away any buffered data
        buffer.clear();
        buffer.limit(0);
        input.position(newPosition);
        position = newPosition;
    }

    /** Shortcut for seek(position + count) */
    public void skip(int count) throws IOException {
        seek(position + count);
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
