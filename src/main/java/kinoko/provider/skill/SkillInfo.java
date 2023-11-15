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

    static int getInteger(Object object) {
        if (object instanceof Integer value) {
            return value;
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    static String getString(Object object) {
        if (object instanceof Integer value) {
            return String.valueOf(value);
        } else if (object instanceof String value) {
            return value;
        }
        return "";
    }
}
