package kinoko.world.skill;

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

    public int getSkillLevel(int skillId) {
        final SkillRecord skillRecord = skillRecords.get(skillId);
        if (skillRecord == null) {
            return 0;
        }
        return skillRecord.getSkillLevel();
    }
}
