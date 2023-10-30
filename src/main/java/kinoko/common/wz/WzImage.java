package kinoko.common.wz;

import kinoko.common.wz.property.WzListProperty;

public final class WzImage {
    private final int offset;
    private WzListProperty property;

    public WzImage(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public WzListProperty getProperty() {
        return property;
    }

    public void setProperty(WzListProperty property) {
        this.property = property;
    }
}
