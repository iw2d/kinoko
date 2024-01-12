package kinoko.util;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HexFormat;
import java.util.Optional;
import java.util.Random;

public final class Util {
    private static final HexFormat hexFormat = HexFormat.ofDelimiter(" ").withUpperCase();
    private static final Random random = new SecureRandom();

    public static String readableByteArray(byte[] array) {
        return hexFormat.formatHex(array);
    }

    public static String opToString(short op) {
        return String.format("%d/0x%X", op, op);
    }

    public static int getRandom(int bound) {
        return random.nextInt(bound);
    }

    public static int getRandom(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

    public static <T> Optional<T> getRandomFromCollection(Collection<T> collection) {
        if (collection.isEmpty()) {
            return Optional.empty();
        }
        return collection.stream()
                .skip(getRandom(collection.size()))
                .findFirst();
    }

    public static boolean isInteger(String string) {
        return string != null && string.matches("^-?\\d+$");
    }
}
