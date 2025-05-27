package kinoko.provider.wz.serialize;

import kinoko.provider.wz.WzImage;

public final class WzUol extends WzSerialize {
    private final String uol;

    public WzUol(WzImage parent, int offset, String uol) {
        super(parent, offset);
        this.uol = uol;
    }

    public String getUol() {
        return uol;
    }
}
