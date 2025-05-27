package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.provider.wz.serialize.WzVector;
import kinoko.util.Rect;

public final class SummonedAttackInfo {
    private final boolean sp;
    private final int spX;
    private final int spY;
    private final int range;
    private final int mobCount;
    private final Rect rect;

    public SummonedAttackInfo(boolean sp, int spX, int spY, int range, int mobCount, Rect rect) {
        this.sp = sp;
        this.spX = spX;
        this.spY = spY;
        this.range = range;
        this.mobCount = mobCount;
        this.rect = rect;
    }

    public boolean isSp() {
        return sp;
    }

    public int getSpX() {
        return spX;
    }

    public int getSpY() {
        return spY;
    }

    public int getRange() {
        return range;
    }

    public int getMobCount() {
        return mobCount;
    }

    public Rect getRect() {
        return rect;
    }

    @Override
    public String toString() {
        return "SummonedAttackInfo{" +
                "sp=" + sp +
                ", spX=" + spX +
                ", spY=" + spY +
                ", range=" + range +
                ", mobCount=" + mobCount +
                ", rect=" + rect +
                '}';
    }

    public static SummonedAttackInfo from(WzProperty attackProp) {
        // CSummonedBase::LoadAttackInfo
        if (!(attackProp.get("range") instanceof WzProperty rangeProp)) {
            throw new ProviderError("Could not resolve summoned attack range");
        }
        boolean sp = false;
        int spX = 0;
        int spY = 0;
        int range = 0;
        if (rangeProp.get("sp") instanceof WzVector spProp) {
            sp = true;
            spX = spProp.getX();
            spY = spProp.getY();
            range = WzProvider.getInteger(rangeProp.get("r"));
        }
        return new SummonedAttackInfo(
                sp,
                spX,
                spY,
                range,
                WzProvider.getInteger(attackProp.get("mobCount"), 1),
                rangeProp.get("lt") != null ? WzProvider.getRect(rangeProp) : null
        );
    }
}
