package kinoko.world.skill;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SkillManager {
    private final Map<Integer, SkillRecord> skillRecords = new ConcurrentHashMap<>();
    private final Map<Integer, Instant> skillCooltimes = new ConcurrentHashMap<>();

    public Map<Integer, SkillRecord> getSkillRecords() {
        return skillRecords;
    }

    public Map<Integer, Instant> getSkillCooltimes() {
        return skillCooltimes;
    }
}
