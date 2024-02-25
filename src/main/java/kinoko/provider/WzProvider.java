package kinoko.provider;

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
}
