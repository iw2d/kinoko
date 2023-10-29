package kinoko.common.wz.property;

import kinoko.common.wz.WzString;

public class WzUolProperty extends WzProperty {
    private final WzString uol;

    public WzUolProperty(WzString uol) {
        this.uol = uol;
    }

    public WzString getUol() {
        return uol;
    }
}
