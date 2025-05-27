package kinoko.provider.wz.serialize;

import kinoko.provider.wz.WzImage;

public final class WzVector extends WzSerialize {
    private final int x;
    private final int y;

    public WzVector(WzImage parent, int offset, int x, int y) {
        super(parent, offset);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
