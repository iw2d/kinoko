package kinoko.util;

import java.time.Instant;

public final class FileTime {
    public static final FileTime MAX_TIME = FileTime.from(150841440000000000L);
    public static final FileTime ZERO_TIME = FileTime.from(94354848000000000L);

    private final int lowDateTime;
    private final int highDateTime;

    public FileTime(int lowDateTime, int highDateTime) {
        this.lowDateTime = lowDateTime;
        this.highDateTime = highDateTime;
    }

    public int getLowDateTime() {
        return lowDateTime;
    }

    public int getHighDateTime() {
        return highDateTime;
    }

    public static FileTime now() {
        return from(Instant.now());
    }

    public static FileTime from(Instant timestamp) {
        return from(timestamp.toEpochMilli());
    }

    public static FileTime from(long value) {
        return new FileTime((int) value, (int) (value >>> 32));
    }
}
