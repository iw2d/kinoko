package kinoko.util;

import java.util.HexFormat;

public final class Util {
    private static final HexFormat HEX_FORMAT = HexFormat.ofDelimiter(" ").withUpperCase();

    public static String readableByteArray(byte[] array) {
        return HEX_FORMAT.formatHex(array);
    }

    public static String opToString(short op) {
        return String.format("%d/0x%X", op, op);
    }
}
