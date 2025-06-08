package kinoko.provider.wz.serialize;

import kinoko.provider.wz.WzImage;

public abstract class WzSerialize {
    protected final WzImage parent;
    protected final int offset;

    protected WzSerialize(WzImage parent, int offset) {
        this.parent = parent;
        this.offset = offset;
    }
}
