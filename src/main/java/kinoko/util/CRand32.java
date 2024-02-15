package kinoko.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class CRand32 {
    private final Lock lock = new ReentrantLock();
    public long s1, s2, s3;

    public void setSeed(int s1, int s2, int s3) {
        lock.lock();
        try {
            this.s1 = Integer.toUnsignedLong(s1);
            this.s2 = Integer.toUnsignedLong(s2);
            this.s3 = Integer.toUnsignedLong(s3);
        } finally {
            lock.unlock();
        }
    }

    public long random() {
        lock.lock();
        try {
            this.s1 = (s1 << 12) ^ (s1 >> 19) ^ ((s1 >> 6) ^ (s1 << 12)) & 0x1FFF;
            this.s2 = (16 * s2) ^ (s2 >> 25) ^ ((16 * s2) ^ (s2 >> 23)) & 0x7F;
            this.s3 = (s3 >> 11) ^ (s3 << 17) ^ ((s3 >> 8) ^ (s3 << 17)) & 0x1FFFFF;
            return (this.s1 ^ this.s2 ^ this.s3) & 0xFFFFFFFFL;
        } finally {
            lock.unlock();
        }
    }
}
