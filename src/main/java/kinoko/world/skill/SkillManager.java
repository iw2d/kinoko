package kinoko.world.skill;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class SkillManager {
    private final Map<Integer, SkillRecord> skillRecords = new HashMap<>();
    private final Map<Integer, Instant> skillCooltimes = new HashMap<>();

    public Set<SkillRecord> getSkillRecords() {
        return skillRecords.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Map<Integer, Instant> getSkillCooltimes() {
        return skillCooltimes;
    }

    public Optional<SkillRecord> getSkill(int skillId) {
        return Optional.ofNullable(skillRecords.get(skillId));
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

    public boolean hasSkillCooltime(int skillId) {
        final Instant nextAvailable = skillCooltimes.get(skillId);
        return nextAvailable != null && nextAvailable.isAfter(Instant.now());
    }

    public void setSkillCooltime(int skillId, Instant nextAvailable) {
        skillCooltimes.put(skillId, nextAvailable);
    }
}
