package kinoko.provider.wz;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class WzReader implements AutoCloseable {
    private final BufferedSeekableReader reader;
    private final WzReaderConfig config;
    private final WzCrypto crypto;

    public WzReader(BufferedSeekableReader reader, WzReaderConfig config, WzCrypto crypto) {
        this.reader = reader;
        this.config = config;
        this.crypto = crypto;
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    public WzReaderConfig getConfig() {
        return this.config;
    }

    public WzCrypto getCrypto() {
        return this.crypto;
    }

    public short readShort() throws IOException {
        return this.reader.readShortLE();
    }

    public int readInt() throws IOException {
        return this.reader.readIntLE();
    }

    public long readLong() throws IOException {
        return this.reader.readLongLE();
    }

    public byte readByte() throws IOException {
        return this.reader.readByte();
    }


    public long readWzLong() throws IOException {
        // Read first byte
        var b = readByte();
        if(b == Byte.MIN_VALUE) {
            return readLong();
        }

        return b;
    }

    public int readWzInt() throws IOException {
        // Read first byte
        var b = readByte();
        if(b == Byte.MIN_VALUE) {
            return readInt();
        } else {
            return b;
        }
    }

    private String readASCIIString(int len) throws IOException {
        var data = this.reader.readNBytes(len);
        crypto.cryptAscii(data);
        return new String(data, StandardCharsets.US_ASCII);
    }

    private String readWtf16String(int len) throws IOException {
        var data = this.reader.readNBytes(len * 2);
        crypto.cryptUnicode(data);
        return new String(data, StandardCharsets.UTF_16LE);
    }

    public String readString() throws IOException {
        var b = readByte();

        // ASCII
        if(b < 0) {
            var len = (b == Byte.MIN_VALUE ? readInt() : -b);
            return readASCIIString(len);
        }

        // WTF-16
        var len = (b == Byte.MAX_VALUE ? readInt() : b);
        return readWtf16String(len);
    }


    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public long position() {
        return this.reader.position();
    }

    public void seek(long pos) throws IOException {
        this.reader.seek(pos);
    }

    private int readOffset(WzPackage parent, ByteBuffer buffer) {
        final int start = parent.getStart();
        final int hash = parent.getHash();
        int result = buffer.position();
        result = ~(result - start);
        result = result * hash;
        result = result - WzConstants.WZ_OFFSET_CONSTANT;
        result = Integer.rotateLeft(result, result & 0x1F);
        result = result ^ buffer.getInt(); // encrypted offset
        result = result + (start * 2);
        return result;
    }

    public void skip(int dataSize) throws IOException {
        reader.skip(dataSize);
    }
}
