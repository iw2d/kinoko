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
    private final Cipher cipher;
    private byte[] cachedKey;

    public WzCrypto(Cipher cipher) {
        this.cipher = cipher;

        if(cipher != null) {
            final int CACHE_SIZE = 4096 * 4;
            this.cachedKey = new byte[CACHE_SIZE];
            for (int i = 0; i < CACHE_SIZE; i += 16) {
                try {
                    cipher.update(cachedKey, i, 16, cachedKey, i);
                } catch (ShortBufferException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void cryptAscii(byte[] data) {
        // Crypt
        crypt(data);

        // Apply mask
        byte mask = (byte) 0xAA;

        for (int i = 0; i < data.length; i++) {
            data[i] ^= mask;
            mask++;
        }
    }

    public void cryptUnicode(byte[] data) {
        crypt(data);

        short mask = (short) 0xAAAA;
        for (int i = 0; i < data.length; i += 2) {
            data[i] ^= (byte) ((mask & 0xFF));
            data[i + 1] ^= (byte) ((mask >> 8));
            mask++;
        }
    }


    public void crypt(byte[] data) {
        if(cipher == null)
            return;

        // Encrypt(xor) with cache first
        for (int i = 0; i < data.length; i++) {
            data[i] ^= cachedKey[i];
        }

        // If data is remaining after cache, encrypt with cipher
        if (data.length > cachedKey.length) {
            final int remaining = data.length - cachedKey.length;
            //grab last 16 bytes of the key
            var key = new byte[16];
            System.arraycopy(cachedKey, cachedKey.length - 16, key, 0, 16);
            for (int i = 0; i < remaining; i++) {
                if(i % 16 == 0) {
                    key = nextKey(key);
                }
                data[i + cachedKey.length] ^= key[i];
            }
        }
    }


    private byte[] nextKey(byte[] currentKey) {
        byte[] key = new byte[16];
        try {
            cipher.update(currentKey, 0, 16, key, 0);
        } catch (ShortBufferException e) {
            throw new RuntimeException(e);
        }
        return key;
    }
    public static WzCrypto fromIv(byte[] iv) {
        // Empty IV
        if (Arrays.equals(iv, WzConstants.WZ_EMPTY_IV)) {
            return new WzCrypto(null);
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
            return new WzCrypto(cipher);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
