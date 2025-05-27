package kinoko.provider.wz;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public final class WzArchive implements WzReadable, Closeable {
    private final RandomAccessFile file;
    private final FileChannel channel;
    private final ByteBuffer buffer;
    private WzImage image;

    public WzArchive(RandomAccessFile file, FileChannel channel, ByteBuffer buffer) {
        this.file = file;
        this.channel = channel;
        this.buffer = buffer;
    }

    public WzImage getImage() {
        if (image == null) {
            image = new WzImage(this, 0);
        }
        return image;
    }

    @Override
    public ByteBuffer getBuffer(int offset) {
        final ByteBuffer result = buffer.duplicate();
        result.order(ByteOrder.LITTLE_ENDIAN);
        result.position(offset);
        return result;
    }

    @Override
    public void close() throws IOException {
        channel.close();
        file.close();
    }

    public static WzArchive from(String path) throws IOException {
        return from(new File(path));
    }

    public static WzArchive from(Path path) throws IOException {
        return from(path.toFile());
    }

    public static WzArchive from(File file) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final FileChannel fileChannel = randomAccessFile.getChannel();
        final ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return new WzArchive(randomAccessFile, fileChannel, buffer);
    }
}
