package kinoko.provider.mob;

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
}
