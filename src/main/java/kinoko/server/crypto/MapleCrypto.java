package kinoko.server.crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class MapleCrypto {
    public static final byte[] AES_USER_KEY = new byte[]{
            0x13, 0x00, 0x00, 0x00,
            0x08, 0x00, 0x00, 0x00,
            0x06, 0x00, 0x00, 0x00,
            (byte) 0xB4, 0x00, 0x00, 0x00,
            0x1B, 0x00, 0x00, 0x00,
            0x0F, 0x00, 0x00, 0x00,
            0x33, 0x00, 0x00, 0x00,
            0x52, 0x00, 0x00, 0x00
    };
    public static final int BLOCK_SIZE = 16;
    private static final Cipher cipher;

    static {
        SecretKey key = new SecretKeySpec(AES_USER_KEY, "AES");
        try {
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize() {
        // Run static initialization block
    }

    public static void crypt(byte[] data, byte[] iv) {
        int a = data.length;
        int b = 0x5B0;
        int c = 0;
        while (a > 0) {
            final byte[] block = expandIv(iv);
            if (a < b) {
                b = a;
            }
            for (int i = c; i < (c + b); i++) {
                if ((i - c) % BLOCK_SIZE == 0) {
                    try {
                        final byte[] cipher = MapleCrypto.cipher.doFinal(block);
                        System.arraycopy(cipher, 0, block, 0, BLOCK_SIZE);
                    } catch (IllegalBlockSizeException | BadPaddingException e) {
                        throw new RuntimeException(e);
                    }
                }
                data[i] ^= block[(i - c) % BLOCK_SIZE];
            }
            c += b;
            a -= b;
            b = 0x5B4;
        }
    }

    private static byte[] expandIv(byte[] iv) {
        final byte[] expandedIv = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i += iv.length) {
            System.arraycopy(iv, 0, expandedIv, i, iv.length);
        }
        return expandedIv;
    }
}
