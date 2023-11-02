package kinoko.server.crypto;

public final class ShandaCrypto {
    private static byte rotateLeft(byte x, int y) {
        final int tmp = (x & 0xFF) << (y % 8);
        return (byte) ((tmp & 0xFF) | (tmp >> 8));
    }

    private static byte rotateRight(byte x, int y) {
        final int tmp = ((x & 0xFF) << 8) >>> (y % 8);
        return (byte) ((tmp & 0xFF) | (tmp >>> 8));
    }

    public static void encrypt(byte[] data) {
        for (int i = 0; i < 3; i++) {
            int a = data.length;
            byte b = 0;
            for (int j = 0; j < data.length; j++) {
                b ^= a + rotateLeft(data[j], 3);
                data[j] = (byte) (0x47 - rotateRight(b, a));
                a -= 1;
            }
            a = data.length;
            b = 0;
            for (int j = data.length - 1; j >= 0; j--) {
                b ^= a + rotateLeft(data[j], 4);
                data[j] = rotateRight((byte) (b ^ 0x13), 3);
                a -= 1;
            }
        }
    }

    public static void decrypt(byte[] data) {
        for (int i = 0; i < 3; i++) {
            int a = data.length;
            byte b = 0;
            for (int j = data.length - 1; j >= 0; j--) {
                final byte c = (byte) (rotateLeft(data[j], 3) ^ 0x13);
                data[j] = rotateRight((byte) ((b ^ c) - a), 4);
                b = c;
                a -= 1;
            }
            a = data.length;
            b = 0;
            for (int j = 0; j < data.length; j++) {
                final byte c = rotateLeft((byte) ~(data[j] - 0x48), a);
                data[j] = rotateRight((byte) ((b ^ c) - a), 3);
                b = c;
                a -= 1;
            }
        }
    }
}
