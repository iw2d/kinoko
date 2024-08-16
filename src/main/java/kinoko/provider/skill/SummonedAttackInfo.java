package kinoko.provider.skill;

import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

public final class SummonedAttackInfo {
    private final int mobCount;
    private final int attackCount;

    public SummonedAttackInfo(int mobCount, int attackCount) {
        this.mobCount = mobCount;
        this.attackCount = attackCount;
    }

    public int getMobCount() {
        return mobCount;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public static SummonedAttackInfo from(WzListProperty attackProp) {
        // CSummonedBase::LoadAttackInfo
        return new SummonedAttackInfo(
                WzProvider.getInteger(attackProp.get("mobCount"), 1),
                WzProvider.getInteger(attackProp.get("attackCount"), 1)
        );
    }
}
