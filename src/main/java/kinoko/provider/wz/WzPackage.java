package kinoko.provider.wz;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.SequencedMap;

public final class WzPackage implements WzReadable, Closeable {
    private final RandomAccessFile file;
    private final FileChannel channel;
    private final ByteBuffer buffer;
    private final int start;
    private final int hash;
    private WzDirectory directory;

    public WzPackage(RandomAccessFile file, FileChannel channel, ByteBuffer buffer, int start, int hash) {
        this.file = file;
        this.channel = channel;
        this.buffer = buffer;
        this.start = start;
        this.hash = hash;
    }

    public int getStart() {
        return start;
    }

    public int getHash() {
        return hash;
    }

    public WzDirectory getDirectory() {
        if (directory == null) {
            directory = new WzDirectory(this, start + 2); // skip version header
        }
        return directory;
    }

    public Object getItem(String path) {
        return getDirectory().getItem(path);
    }

    public SequencedMap<String, Object> getItems() {
        return getDirectory().getItems();
    }

    public ByteBuffer getBuffer(int offset) {
        final ByteBuffer result = buffer.duplicate();
        result.order(ByteOrder.LITTLE_ENDIAN);
        result.position(offset);
        return result;
    }

    public void close() throws IOException {
        channel.close();
        file.close();
    }

    public static WzPackage from(String path) throws IOException {
        return from(new File(path));
    }

    public static WzPackage from(Path path) throws IOException {
        return from(path.toFile());
    }

    public static WzPackage from(File file) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final FileChannel fileChannel = randomAccessFile.getChannel();
        final ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Check PKG1 header
        if (buffer.getInt() != 0x31474B50) {
            throw new WzReaderError("PKG1 header missing");
        }
        final long size = buffer.getLong();
        final int start = buffer.getInt();

        // Check version hash
        buffer.position(start);
        final int versionHeader = Short.toUnsignedInt(buffer.getShort());
        final int versionHash = computeVersionHash(WzConstants.GAME_VERSION);
        final int computedHeader = 0xFF
                ^ ((versionHash >> 24) & 0xFF)
                ^ ((versionHash >> 16) & 0xFF)
                ^ ((versionHash >> 8) & 0xFF)
                ^ (versionHash & 0xFF);
        if (versionHeader != computedHeader) {
            throw new WzReaderError("Incorrect version");
        }

        return new WzPackage(randomAccessFile, fileChannel, buffer, start, versionHash);
    }

    private static int computeVersionHash(int version) {
        int versionHash = 0;
        for (final byte c : String.valueOf(version).getBytes()) {
            versionHash = (versionHash * 32) + c + 1;
        }
        return versionHash;
    }
}
