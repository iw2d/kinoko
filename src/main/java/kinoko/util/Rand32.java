package kinoko.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Rand32 {
    private final Lock lock = new ReentrantLock();
    private int s1, s2, s3;

    public int getS1() {
        return s1;
    }

    public int getS2() {
        return s2;
    }

    public int getS3() {
        return s3;
    }

    public void setSeed(int s1, int s2, int s3) {
        lock.lock();
        try {
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
        } finally {
            lock.unlock();
        }
    }

    public int random() {
        lock.lock();
        try {
            this.s1 = (s1 << 12) ^ (s1 >>> 19) ^ ((short) (s1 >>> 6) ^ (short) (s1 << 12)) & 0x1FFF;
            this.s2 = (16 * s2) ^ (s2 >>> 25) ^ ((byte) (16 * s2) ^ (byte) (s2 >>> 23)) & 0x7F;
            this.s3 = (s3 >>> 11) ^ (s3 << 17) ^ ((s3 >>> 8) ^ (s3 << 17)) & 0x1FFFFF;
            return this.s1 ^ this.s2 ^ this.s3;
        } finally {
            lock.unlock();
        }
    }
}
