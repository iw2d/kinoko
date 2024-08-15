package kinoko.provider.map;

import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

public final class LadderRope {
    private final int sn;
    private final int l;
    private final int uf;
    private final int x;
    private final int y1;
    private final int y2;
    private final int page;

    public LadderRope(int sn, int l, int uf, int x, int y1, int y2, int page) {
        this.sn = sn;
        this.l = l;
        this.uf = uf;
        this.x = x;
        this.y1 = y1;
        this.y2 = y2;
        this.page = page;
    }

    public int getSn() {
        return sn;
    }

    public int getL() {
        return l;
    }

    public int getUf() {
        return uf;
    }

    public int getX() {
        return x;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public int getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "LadderRope{" +
                "sn=" + sn +
                ", l=" + l +
                ", uf=" + uf +
                ", x=" + x +
                ", y1=" + y1 +
                ", y2=" + y2 +
                ", page=" + page +
                '}';
    }

    public static LadderRope from(int sn, WzListProperty ladderRopeProp) {
        return new LadderRope(
                sn,
                WzProvider.getInteger(ladderRopeProp.get("l")), // bLadder
                WzProvider.getInteger(ladderRopeProp.get("uf")), // bUpperFoothold
                WzProvider.getInteger(ladderRopeProp.get("x")),
                WzProvider.getInteger(ladderRopeProp.get("y1")),
                WzProvider.getInteger(ladderRopeProp.get("y2")),
                WzProvider.getInteger(ladderRopeProp.get("page"))
        );
    }
}
