package kinoko.common.wz;

import kinoko.common.wz.property.WzProperty;

public final class WzImage {
    private final int offset;
    private WzProperty property;

    public WzImage(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public WzProperty getProperty() {
        return property;
    }

    public void setProperty(WzProperty property) {
        this.property = property;
    }
}
