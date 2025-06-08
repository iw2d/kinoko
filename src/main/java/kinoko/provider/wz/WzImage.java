package kinoko.provider.wz;

import kinoko.provider.wz.serialize.WzProperty;

import java.nio.ByteBuffer;
import java.util.SequencedMap;

public final class WzImage implements WzReadable {
    private final WzReadable parent;
    private final int offset;
    private WzProperty property;

    public WzImage(WzReadable parent, int offset) {
        this.parent = parent;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public WzProperty getProperty() {
        if (property == null) {
            property = new WzProperty(this, offset);
        }
        return property;
    }

    public Object getItem(String path) {
        return getProperty().getItem(path);
    }

    public SequencedMap<String, Object> getItems() {
        return getProperty().getItems();
    }

    @Override
    public ByteBuffer getBuffer(int offset) {
        return parent.getBuffer(offset);
    }
}
