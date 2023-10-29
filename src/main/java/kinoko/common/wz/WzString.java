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
    private final ByteBuffer data;

    public WzString() {
        this(WzStringType.EMPTY, null);
    }

    public WzString(WzStringType type, ByteBuffer data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Decrypts the String using the {@link WzCrypto} instance.
     *
     * @return Decrypted {@link String}.
     */
    public String toString(WzCrypto crypto) {
        switch (this.type) {
            case ASCII -> {
                byte[] data = new byte[this.data.limit()];
                this.data.get(data);
                crypto.cryptAscii(data);
                return new String(data, StandardCharsets.US_ASCII);
            }
            case UNICODE -> {
                byte[] data = new byte[this.data.limit()];
                this.data.get(data);
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
        return this.type == other.type && Objects.equals(this.data, other.data);
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + Objects.hashCode(this.data);
    }

}
