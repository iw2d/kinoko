package kinoko.world.skill;

import kinoko.provider.skill.SkillInfo;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SkillManager {
    private final Map<Integer, SkillRecord> skillRecords = new ConcurrentHashMap<>();
    private final Map<Integer, Instant> skillCooltimes = new ConcurrentHashMap<>();

    public Map<Integer, SkillRecord> getSkillRecords() {
        //return skillRecords;
        final SkillRecord threeSnails = new SkillRecord(1000);
        threeSnails.setSkillLevel(1);
        threeSnails.setMasterLevel(3);
        return Map.of(
                1000, threeSnails
        );
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
