package kinoko.util;

import java.time.Instant;

public final class FileTime {
    public static final FileTime DEFAULT_TIME = FileTime.from(150842304000000000L);
    public static final FileTime ZERO_TIME = FileTime.from(94354848000000000L);
    public static final long FT_OFFSET = 116444736000000000L;

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
        return FileTime.from(TimeUtil.getCurrentTime());
    }

    public static FileTime from(Instant timestamp) {
        return FileTime.from(timestamp.toEpochMilli() * 10000L + FT_OFFSET);
    }

    public static FileTime from(long value) {
        return new FileTime((int) value, (int) (value >>> 32));
    }
}
