package kinoko.world.skill;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public final class SkillManager {
    private final Map<Integer, SkillRecord> skillRecords = new HashMap<>();
    private final Map<Integer, Instant> skillCooltimes = new HashMap<>();
    private boolean ignoreCooltime = false;

    public Set<SkillRecord> getSkillRecords() {
        return skillRecords.values().stream().collect(Collectors.toUnmodifiableSet());
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

    public int getSkillStatValue(int skillId, SkillStat stat) {
        final int slv = getSkillLevel(skillId);
        if (slv == 0) {
            return 0;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        return skillInfoResult.map(skillInfo -> skillInfo.getValue(stat, slv)).orElse(0);
    }

    public Map<Integer, Instant> getSkillCooltimes() {
        return skillCooltimes;
    }


    public boolean hasSkillCooltime(int skillId) {
        final Instant nextAvailable = skillCooltimes.get(skillId);
        return nextAvailable != null && nextAvailable.isAfter(Instant.now());
    }

    public void setSkillCooltime(int skillId, Instant nextAvailable) {
        if (isIgnoreCooltime()) {
            return;
        }
        skillCooltimes.put(skillId, nextAvailable);
    }

    public Set<Integer> expireSkillCooltime(Instant now) {
        final Set<Integer> resetCooltimes = new HashSet<>();
        final var iter = getSkillCooltimes().entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Integer, Instant> entry = iter.next();
            final int skillId = entry.getKey();
            final Instant nextAvailable = entry.getValue();
            // Check skill cooltime and remove
            if (now.isBefore(nextAvailable) && !isIgnoreCooltime()) {
                continue;
            }
            iter.remove();
            resetCooltimes.add(skillId);
        }
        return resetCooltimes;
    }

    public boolean isIgnoreCooltime() {
        return ignoreCooltime;
    }

    public void setIgnoreCooltime(boolean ignoreCooltime) {
        this.ignoreCooltime = ignoreCooltime;
    }
}
