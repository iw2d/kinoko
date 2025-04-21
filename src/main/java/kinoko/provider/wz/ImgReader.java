package kinoko.provider.wz;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class ImgReader  implements AutoCloseable {
    private final RandomAccessFile file;
    private final FileChannel channel;
    private final WzReaderConfig config;
    private final WzCrypto crypto;
    private final Map<Integer, String> stringTable;

    public ImgReader(RandomAccessFile file, FileChannel channel, WzReaderConfig config, WzCrypto crypto) {
        this.file = file;
        this.channel = channel;
        this.config = config;
        this.crypto = crypto;
        this.stringTable = new HashMap<>();
    }

    @Override
    public void close() throws IOException {
        file.close();
        channel.close();
    }
}
