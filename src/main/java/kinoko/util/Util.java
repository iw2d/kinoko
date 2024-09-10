package kinoko.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HexFormat;
import java.util.Optional;
import java.util.Random;
import java.util.function.ToDoubleFunction;

public final class Util {
    private static final HexFormat hexFormat = HexFormat.ofDelimiter(" ").withUpperCase();
    private static final Random random = new SecureRandom();

    public static String getEnv(String name, String defaultValue) {
        final String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }

    public static int getEnv(String name, int defaultValue) {
        final String value = System.getenv(name);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public static boolean getEnv(String name, boolean defaultValue) {
        final String value = System.getenv(name);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static byte[] getHost(String name) {
        try {
            return InetAddress.getByName(name).getAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readableByteArray(byte[] array) {
        return hexFormat.formatHex(array);
    }

    public static byte[] stringToByteArray(String string) {
        return hexFormat.parseHex(string);
    }

    public static String opToString(int op) {
        return String.format("%d/0x%X", op, op);
    }

    public static Random getRandom() {
        return random;
    }

    public static int getRandom(int toInclusive) {
        return random.nextInt(toInclusive + 1);
    }

    public static int getRandom(int fromInclusive, int toInclusive) {
        return random.nextInt(fromInclusive, toInclusive + 1);
    }

    public static double getRandom(double origin, double bound) {
        return random.nextDouble(origin, bound);
    }

    public static boolean succeedProp(int chance) {
        return random.nextInt(0, 100) < chance;
    }

    public static boolean succeedDouble(double chance) {
        return random.nextDouble() < chance;
    }

    public static <T> Optional<T> getRandomFromCollection(Collection<T> collection) {
        return getRandomFromCollection(collection, (ignored) -> 1.0);
    }

    public static <T> Optional<T> getRandomFromCollection(Collection<T> collection, ToDoubleFunction<T> weightFunction) {
        if (collection.isEmpty()) {
            return Optional.empty();
        }
        final double totalWeight = collection.stream().mapToDouble(weightFunction).sum();
        double r = random.nextDouble() * totalWeight;
        for (T item : collection) {
            r -= weightFunction.applyAsDouble(item);
            if (r <= 0.0) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        final int dx = x1 - x2;
        final int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static boolean isInteger(String string) {
        return string != null && string.matches("^-?\\d+$");
    }
}
