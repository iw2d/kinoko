package kinoko.world.skill;

public final class SkillRecord {
    private final int skillId;
    private int skillLevel;
    private int masterLevel;

    public SkillRecord(int skillId) {
        this.skillId = skillId;
    }

    public SkillRecord(int skillId, int skillLevel, int masterLevel) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.masterLevel = masterLevel;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public void setMasterLevel(int masterLevel) {
        this.masterLevel = masterLevel;
    }
}
