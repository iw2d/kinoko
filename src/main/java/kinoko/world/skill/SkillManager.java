package kinoko.world.skill;

import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public final class SkillManager {
    private final Map<Integer, SkillRecord> skillRecords = new ConcurrentHashMap<>();
    private final Map<Integer, Instant> skillCooltimes = new ConcurrentHashMap<>();
}
