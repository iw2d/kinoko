package kinoko.world.skill;

import kinoko.provider.skill.SkillInfo;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public final class SkillManager {
    private final Map<Integer, SkillRecord> skillRecords = new HashMap<>();
    private final Map<Integer, Instant> skillCooltimes = new HashMap<>();

    public Map<Integer, SkillRecord> getSkillRecords() {
        return skillRecords;
    }

    public Map<Integer, Instant> getSkillCooltimes() {
        return skillCooltimes;
    }

    public void addSkill(SkillRecord skillRecord) {
        skillRecords.put(skillRecord.getSkillId(), skillRecord);
    }

    public void addSkill(SkillInfo skillInfo) {
        final SkillRecord skillRecord = new SkillRecord(skillInfo.getId());
        skillRecord.setSkillLevel(0);
        skillRecord.setMasterLevel(skillInfo.getMaxLevel());
        addSkill(skillRecord);
    }
}
