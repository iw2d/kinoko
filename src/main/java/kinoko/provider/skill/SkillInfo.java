package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Rect;
import kinoko.world.skill.SkillRecord;

public interface SkillInfo {
    int getId();

    int getMaxLevel();

    boolean isPsd();

    boolean isInvisible();

    int getValue(SkillStat stat, int slv);

    Rect getRect();

    default SkillRecord createRecord() {
        final SkillRecord skillRecord = new SkillRecord(getId());
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
