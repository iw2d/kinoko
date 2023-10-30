package kinoko.common.provider;

import kinoko.common.wz.WzCrypto;
import kinoko.common.wz.WzPackage;
import kinoko.common.wz.WzString;
import kinoko.common.wz.property.WzListProperty;

import java.util.Map;

public abstract class Provider<T> {
    private final WzCrypto crypto;

    public Provider(WzCrypto crypto) {
        this.crypto = crypto;
    }

    protected final WzCrypto getCrypto() {
        return crypto;
    }

    protected final WzString encString(String string) {
        return WzString.fromString(string, crypto);
    }

    protected final String decString(WzString string) {
        return string.toString(crypto);
    }

    protected final int decInteger(WzString string) {
        return Integer.parseInt(decString(string));
    }

    @SuppressWarnings("unchecked")
    protected final <F> F getItem(WzListProperty property, String key) throws ProviderError {
        Object value = property.getItems().get(encString(key));
        if (value == null) {
            throw new ProviderError("Could not get list property item with key : %s", key);
        }
        return (F) value;
    }

    @SuppressWarnings("unchecked")
    protected final <F> F getItem(WzListProperty property, String key, F defaultValue) {
        Object value = property.getItems().get(encString(key));
        if (value == null) {
            return defaultValue;
        }
        return (F) value;
    }

    public abstract Map<Integer, T> resolve(WzPackage source) throws ProviderError;
}
