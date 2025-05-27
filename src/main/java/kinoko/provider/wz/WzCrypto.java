package kinoko.provider.wz;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class WzCrypto {
    public static final int BATCH_SIZE = 1024;
    private static Cipher cipher = getCipher(WzConstants.WZ_GMS_IV);
    private static byte[] cipherMask = new byte[]{};

    public static void cryptAscii(byte[] data) {
        ensureSize(data.length);
        byte mask = (byte) 0xAA;
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (data[i] ^ cipherMask[i] ^ mask);
            mask++;
        }
    }

    public static void cryptUnicode(byte[] data) {
        ensureSize(data.length);
        short mask = (short) 0xAAAA;
        for (int i = 0; i < data.length; i += 2) {
            data[i] = (byte) (data[i] ^ cipherMask[i] ^ (mask & 0xFF));
            data[i + 1] = (byte) (data[i + 1] ^ cipherMask[i + 1] ^ (mask >> 8));
            mask++;
        }
    }

    private static synchronized void ensureSize(int size) {
        final int curSize = cipherMask.length;
        if (curSize >= size) {
            return;
        }
        final int newSize = ((size / BATCH_SIZE) + 1) * BATCH_SIZE;
        final byte[] newMask = new byte[newSize];

        if (cipher != null) {
            System.arraycopy(cipherMask, 0, newMask, 0, curSize);
            try {
                final byte[] block = new byte[16];
                for (int i = curSize; i < newSize; i += 16) {
                    cipher.update(block, 0, 16, newMask, i);
                }
            } catch (ShortBufferException e) {
                throw new RuntimeException(e);
            }
        }
        cipherMask = newMask;
    }

    public static Cipher getCipher(byte[] iv) {
        // Empty IV
        if (Arrays.equals(iv, WzConstants.WZ_EMPTY_IV)) {
            return null;
        }

        // Initialize key
        final byte[] trimmedKey = new byte[32];
        for (int i = 0; i < 128; i += 16) {
            trimmedKey[i / 4] = WzConstants.AES_USER_KEY[i];
        }
        SecretKey key = new SecretKeySpec(trimmedKey, "AES");

        // Initialize IV
        final byte[] expandedIv = new byte[16];
        for (int i = 0; i < expandedIv.length; i += iv.length) {
            System.arraycopy(iv, 0, expandedIv, i, iv.length);
        }
        IvParameterSpec ivParam = new IvParameterSpec(expandedIv);

        // Create cipher and return WzCrypto object
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParam);
            return cipher;
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCipher(Cipher cipher) {
        WzCrypto.cipher = cipher;
        WzCrypto.cipherMask = new byte[]{};
    }
}
