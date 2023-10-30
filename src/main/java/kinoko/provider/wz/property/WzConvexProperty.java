package kinoko.provider.wz.property;

import java.util.List;

public class WzConvexProperty extends WzProperty {
    private final List<WzProperty> properties;

    public WzConvexProperty(List<WzProperty> properties) {
        this.properties = properties;
    }

    public List<WzProperty> getProperties() {
        return properties;
    }
}
