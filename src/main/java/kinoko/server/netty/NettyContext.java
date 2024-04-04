package kinoko.server.netty;

import io.netty.util.AttributeKey;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class NettyContext {
    public static final AttributeKey<NettyContext> CONTEXT_KEY = AttributeKey.valueOf("X");
    private final Lock encoderLock = new ReentrantLock();
    private int storedLength = -1;

    public int getStoredLength() {
        return storedLength;
    }

    public void setStoredLength(int storedLength) {
        this.storedLength = storedLength;
    }

    public void acquireEncoderState() {
        encoderLock.lock();
    }

    public void releaseEncoderState() {
        encoderLock.unlock();
    }
}
