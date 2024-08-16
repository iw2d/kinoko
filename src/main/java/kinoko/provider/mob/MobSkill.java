package kinoko.provider.mob;

import java.util.Objects;

public final class MobSkill {
    private final MobSkillType skillType;
    private final int skillId;
    private final int skillLevel;

    public MobSkill(MobSkillType skillType, int skillId, int skillLevel) {
        this.skillType = skillType;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
    }

    public MobSkillType getSkillType() {
        return skillType;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobSkill mobSkill = (MobSkill) o;
        return skillId == mobSkill.skillId && skillLevel == mobSkill.skillLevel && skillType == mobSkill.skillType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillType, skillId, skillLevel);
    }
}
