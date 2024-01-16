package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Rect;

public interface SkillInfo {
    int getId();

    int getMaxLevel();

    boolean isPsd();

    boolean isInvisible();

    int getValue(SkillStat stat, int slv);

    Rect getRect();

    static SkillInfo from(int skillId, WzListProperty skillProp) throws ProviderError {
        if (skillProp.get("level") instanceof WzListProperty) {
            return StaticSkillInfo.from(skillId, skillProp);
        } else {
            return ComputedSkillInfo.from(skillId, skillProp);
        }
    }
}
