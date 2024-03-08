package kinoko.provider.skill;

import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

public final class SummonedAttackInfo {
    private final int skillId;
    private final int mobCount;
    private final int attackCount;

    public SummonedAttackInfo(int skillId, int mobCount, int attackCount) {
        this.skillId = skillId;
        this.mobCount = mobCount;
        this.attackCount = attackCount;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getMobCount() {
        return mobCount;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public static SummonedAttackInfo from(int skillId, WzListProperty attackProp) {
        // CSummonedBase::LoadAttackInfo
        return new SummonedAttackInfo(
                skillId,
                WzProvider.getInteger(attackProp.get("mobCount"), 1),
                WzProvider.getInteger(attackProp.get("attackCount"), 1)
        );
    }
}
