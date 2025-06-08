package kinoko.provider.wz;

import java.nio.ByteBuffer;

public interface WzReadable {
    ByteBuffer getBuffer(int offset);
}
