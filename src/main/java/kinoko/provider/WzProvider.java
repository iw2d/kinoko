package kinoko.provider;

import kinoko.provider.wz.property.WzListProperty;
import kinoko.provider.wz.property.WzVectorProperty;
import kinoko.util.Rect;

public interface WzProvider {
    static int getInteger(Object object) {
        if (object instanceof Integer value) {
            return value;
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        throw new ProviderError("Unexpected or missing value while extracting Integer");
    }

    static int getInteger(Object object, int defaultValue) {
        if (object instanceof Integer value) {
            return value;
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    static String getString(Object object) {
        if (object instanceof Integer value) {
            return String.valueOf(value);
        } else if (object instanceof String value) {
            return value;
        }
        throw new ProviderError("Unexpected or missing value while extracting String");
    }

    static String getString(Object object, String defaultValue) {
        if (object instanceof Integer value) {
            return String.valueOf(value);
        } else if (object instanceof String value) {
            return value;
        }
        return defaultValue;
    }

    static Rect getRect(WzListProperty prop) {
        final WzVectorProperty lt = prop.get("lt");
        final WzVectorProperty rb = prop.get("rb");
        return new Rect(
                lt.getX(),
                lt.getY(),
                rb.getX(),
                rb.getY()
        );
    }
}
