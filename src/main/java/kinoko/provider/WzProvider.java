package kinoko.provider;

public interface WzProvider {
    static int getInteger(Object object) {
        if (object instanceof Integer value) {
            return value;
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    static String getString(Object object) {
        if (object instanceof Integer value) {
            return String.valueOf(value);
        } else if (object instanceof String value) {
            return value;
        }
        return "";
    }
}
