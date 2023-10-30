package kinoko.common.wz.property;

import java.util.Map;

public class WzListProperty extends WzProperty {
    private final Map<String, Object> items;

    public WzListProperty(Map<String, Object> items) {
        this.items = items;
    }

    public Map<String, Object> getItems() {
        return items;
    }
}
