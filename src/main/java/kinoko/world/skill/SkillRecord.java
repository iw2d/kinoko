package kinoko.world.skill;

import kinoko.meta.SkillId;

public final class SkillRecord {
    private final SkillId skillId;
    private int skillLevel;
    private int masterLevel;

    public SkillRecord(SkillId skillId) {
        this.skillId = skillId;
    }

    public SkillId getSkillId() {
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
