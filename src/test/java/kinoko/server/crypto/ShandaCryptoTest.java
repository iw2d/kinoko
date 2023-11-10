package kinoko.server.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ShandaCryptoTest {
    @Test
    public void testEnDecrypt() {
        final byte[] data = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        ShandaCrypto.encrypt(data);
        ShandaCrypto.decrypt(data);
        Assertions.assertArrayEquals(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, data);
    }
}
