package kinoko.common.wz.property;

import java.nio.ByteBuffer;

public class WzCanvasProperty extends WzProperty {
    private final WzListProperty properties;
    private final int width;
    private final int height;
    private final int format;
    private final int format2;
    private final ByteBuffer data;

    public WzCanvasProperty(WzListProperty properties, int width, int height, int format, int format2, ByteBuffer data) {
        this.properties = properties;
        this.width = width;
        this.height = height;
        this.format = format;
        this.format2 = format2;
        this.data = data;
    }
}
