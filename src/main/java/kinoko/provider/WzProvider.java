package kinoko.provider;

import kinoko.provider.wz.serialize.WzProperty;
import kinoko.provider.wz.serialize.WzVector;
import kinoko.util.Rect;

public interface WzProvider {
    static int getInteger(Object object) {
        if (object instanceof Short value) {
            return value;
        } else if (object instanceof Integer value) {
            return value;
        } else if (object instanceof Float value) {
            return value.intValue();
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        throw new ProviderError("Unexpected or missing value while extracting Integer");
    }

    static int getInteger(Object object, int defaultValue) {
        if (object instanceof Short value) {
            return value;
        } else if (object instanceof Integer value) {
            return value;
        } else if (object instanceof Float value) {
            return value.intValue();
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    static double getDouble(Object object) {
        if (object instanceof Float value) {
            return value;
        } else if (object instanceof Double value) {
            return value;
        }
        throw new ProviderError("Unexpected or missing value while extracting Double");
    }

    static double getDouble(Object object, double defaultValue) {
        if (object instanceof Float value) {
            return value;
        } else if (object instanceof Double value) {
            return value;
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

    static Rect getRect(WzProperty prop) {
        final WzVector lt = prop.get("lt");
        final WzVector rb = prop.get("rb");
        return Rect.of(
                lt.getX(),
                lt.getY(),
                rb.getX(),
                rb.getY()
        );
    }
}
