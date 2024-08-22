package kinoko.provider.wz.property;

import java.util.Collections;
import java.util.SequencedMap;

public final class WzListProperty extends WzProperty {
    private final SequencedMap<String, Object> items;

    public WzListProperty(SequencedMap<String, Object> items) {
        this.items = items;
    }

    public SequencedMap<String, Object> getItems() {
        return items;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) items.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        if (!items.containsKey(key)) {
            return defaultValue;
        }
        return (T) items.get(key);
    }

    public static WzListProperty from(SequencedMap<String, Object> items) {
        return new WzListProperty(
                Collections.unmodifiableSequencedMap(items)
        );
    }
}
