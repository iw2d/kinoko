package kinoko.common.wz.property;

import kinoko.common.wz.WzString;

import java.util.Map;

public class WzListProperty extends WzProperty {
    private final Map<WzString, Object> items;

    public WzListProperty(Map<WzString, Object> items) {
        this.items = items;
    }

    public Map<WzString, Object> getItems() {
        return items;
    }
}
