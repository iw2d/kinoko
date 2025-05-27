package kinoko.provider.map;

import kinoko.provider.wz.serialize.WzProperty;

public final class LifeInfo {
    private final LifeType lifeType;
    private final int templateId;
    private final int x;
    private final int y;
    private final int rx0;
    private final int rx1;
    private final int cy;
    private final int fh;
    private final boolean flip;
    private final boolean hide;
    private final int mobTime;

    public LifeInfo(LifeType lifeType, int templateId, int x, int y, int rx0, int rx1, int cy, int fh, boolean flip, boolean hide, int mobTime) {
        this.lifeType = lifeType;
        this.templateId = templateId;
        this.x = x;
        this.y = y;
        this.rx0 = rx0;
        this.rx1 = rx1;
        this.cy = cy;
        this.fh = fh;
        this.flip = flip;
        this.hide = hide;
        this.mobTime = mobTime;
    }

    public LifeType getLifeType() {
        return lifeType;
    }

    public int getTemplateId() {
        return templateId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRx0() {
        return rx0;
    }

    public int getRx1() {
        return rx1;
    }

    public int getCy() {
        return cy;
    }

    public int getFh() {
        return fh;
    }

    public boolean isFlip() {
        return flip;
    }

    public boolean isHide() {
        return hide;
    }

    public int getMobTime() {
        return mobTime;
    }

    @Override
    public String toString() {
        return "LifeInfo[" +
                "type=" + lifeType + ", " +
                "id=" + templateId + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "rx0=" + rx0 + ", " +
                "rx1=" + rx1 + ", " +
                "cy=" + cy + ", " +
                "fh=" + fh + ", " +
                "f=" + flip + ", " +
                "hide=" + hide + ", " +
                "mobTime=" + mobTime + ']';
    }

    public static LifeInfo from(LifeType lifeType, WzProperty lifeProp) {
        return new LifeInfo(
                lifeType,
                Integer.parseInt(lifeProp.get("id")),
                lifeProp.get("x"),
                lifeProp.get("y"),
                lifeProp.get("rx0"),
                lifeProp.get("rx1"),
                lifeProp.get("cy"),
                lifeProp.get("fh"),
                lifeProp.getOrDefault("f", 0) != 0,
                lifeProp.getOrDefault("hide", 0) != 0,
                lifeProp.getOrDefault("mobTime", 0)
        );
    }

}
