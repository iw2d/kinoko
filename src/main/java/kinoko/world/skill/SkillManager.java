package kinoko.world.skill;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public final class SkillManager {
    private Map<Integer, SkillRecord> skillRecords;
    private Map<Integer, Instant> skillCooltimes;
}
