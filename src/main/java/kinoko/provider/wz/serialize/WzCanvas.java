package kinoko.provider.wz.serialize;

import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.WzReader;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;

public final class WzCanvas extends WzSerialize {
    private WzProperty property;

    public WzCanvas(WzImage parent, int offset) {
        super(parent, offset);
    }

    public WzProperty getProperty() {
        if (property == null) {
            property = readProperty();
        }
        return property;
    }

    private WzProperty readProperty() {
        final ByteBuffer buffer = parent.getBuffer(offset);
        WzReader.readStringBlock(parent, buffer);
        buffer.position(buffer.position() + 1);
        final boolean hasProperty = buffer.get() == 1;
        if (hasProperty) {
            return new WzProperty(parent, offset);
        } else {
            return new WzProperty(parent, offset, new LinkedHashMap<>());
        }
    }
}
