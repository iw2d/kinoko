package kinoko.common.wz.property;

import kinoko.common.wz.WzString;

import java.util.Map;

public class WzListProperty extends WzProperty {
    private final Map<WzString, WzListItem<?>> items;

    public WzListProperty(Map<WzString, WzListItem<?>> items) {
        this.items = items;
    }

    public Map<WzString, WzListItem<?>> getItems() {
        return items;
    }
}
