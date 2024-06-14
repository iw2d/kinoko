package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Rect;
import kinoko.world.skill.SkillRecord;

import java.util.List;

public interface SkillInfo {
    int getSkillId();

    int getMaxLevel();

    boolean isInvisible();

    boolean isCombatOrders();

    boolean isPsd();

    List<Integer> getPsdSkills();

    int getValue(SkillStat stat, int slv);

    Rect getRect();

    ElementAttribute getElemAttr();

    default int getDuration(int slv) {
        return getValue(SkillStat.time, slv) * 1000;
    }

    default int getBulletCon(int slv) {
        return Math.max(getValue(SkillStat.bulletConsume, slv), getValue(SkillStat.bulletCount, slv));
    }

    default SkillRecord createRecord() {
        final SkillRecord skillRecord = new SkillRecord(getSkillId());
        skillRecord.setSkillLevel(0);
        skillRecord.setMasterLevel(getMaxLevel());
        return skillRecord;
    }

    static SkillInfo from(int skillId, WzListProperty skillProp) throws ProviderError {
        if (skillProp.get("level") instanceof WzListProperty) {
            return StaticSkillInfo.from(skillId, skillProp);
        } else {
            return ComputedSkillInfo.from(skillId, skillProp);
        }
    }
}
