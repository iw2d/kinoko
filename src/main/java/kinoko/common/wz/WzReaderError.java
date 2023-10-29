package kinoko.common.wz;

public class WzReaderError extends Error {
    public WzReaderError(String message) {
        super(message);
    }

    public WzReaderError(String format, Object... args) {
        super(String.format(format, args));
    }
}
