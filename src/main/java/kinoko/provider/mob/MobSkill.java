package kinoko.provider.mob;

public final class MobSkill {
    private final int skillId;
    private final int level;

    public MobSkill(int skillId, int level) {
        this.skillId = skillId;
        this.level = level;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getLevel() {
        return level;
    }
}
