package kinoko.common.wz;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Encrypted Strings inside WZ files which are decrypted lazily using {@link WzCrypto}. Comparisons are done using the
 * type of the string and the underlying byte array data. In addition, since the String encryption is symmetric, we can
 * search more efficiently by simply encrypting the search query and comparing the encrypted Strings.
 */
public class WzString {
    private final WzStringType type;
    private final ByteBuffer buffer;

    public WzString() {
        this(WzStringType.EMPTY, null);
    }

    public WzString(WzStringType type, ByteBuffer buffer) {
        this.type = type;
        this.buffer = buffer;
    }

    public static WzString fromString(String string, WzCrypto crypto) {
        return fromString(WzStringType.ASCII, string, crypto);
    }

    public static WzString fromString(WzStringType type, String string, WzCrypto crypto) {
        switch (type) {
            case ASCII -> {
                byte[] data = string.getBytes(StandardCharsets.US_ASCII);
                crypto.cryptAscii(data);
                return new WzString(type, ByteBuffer.wrap(data));
            }
            case UNICODE -> {
                byte[] data = string.getBytes(StandardCharsets.UTF_16LE);
                crypto.cryptUnicode(data);
                return new WzString(type, ByteBuffer.wrap(data));
            }
        }
        return new WzString();
    }

    /**
     * Decrypts the String using the {@link WzCrypto} instance.
     *
     * @return Decrypted {@link String}.
     */
    public String toString(WzCrypto crypto) {
        switch (type) {
            case ASCII -> {
                byte[] data = new byte[buffer.limit()];
                buffer.get(data);
                crypto.cryptAscii(data);
                return new String(data, StandardCharsets.US_ASCII);
            }
            case UNICODE -> {
                byte[] data = new byte[buffer.limit()];
                buffer.get(data);
                crypto.cryptUnicode(data);
                return new String(data, StandardCharsets.UTF_16LE);
            }
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof WzString other))
            return false;
        return this.type == other.type && Objects.equals(this.buffer, other.buffer);
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + Objects.hashCode(this.buffer);
    }

}
