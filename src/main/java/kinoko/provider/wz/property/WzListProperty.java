package kinoko.provider.wz.property;

import java.util.Map;

public final class WzListProperty extends WzProperty {
    private final Map<String, Object> items;

    public WzListProperty(Map<String, Object> items) {
        this.items = items;
    }

    public final Map<String, Object> getItems() {
        return items;
    }

    @SuppressWarnings("unchecked")
    public final <T> T get(String key) {
        return (T) items.get(key);
    }

    @SuppressWarnings("unchecked")
    public final <T> T getOrDefault(String key, T defaultValue) {
        if (!items.containsKey(key)) {
            return defaultValue;
        }
        return (T) items.get(key);
    }
}
