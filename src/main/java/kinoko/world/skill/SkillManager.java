package kinoko.world.skill;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.util.TimeUtil;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.time.Instant;
import java.util.*;

public final class SkillManager {
    private final Map<Integer, SkillRecord> skillRecords = new HashMap<>();
    private final Map<Integer, Instant> skillCooltimes = new HashMap<>();
    private final Map<Integer, Instant> skillSchedules = new HashMap<>();

    // SKILL RECORD METHODS --------------------------------------------------------------------------------------------

    public List<SkillRecord> getSkillRecords() {
        return skillRecords.values().stream().toList();
    }

    public Optional<SkillRecord> getSkill(int skillId) {
        return Optional.ofNullable(skillRecords.get(skillId));
    }

    public void addSkill(SkillRecord skillRecord) {
        skillRecords.put(skillRecord.getSkillId(), skillRecord);
    }

    public void removeSkill(int skillId) {
        skillRecords.remove(skillId);
    }


    // SKILL COOLTIME METHODS ------------------------------------------------------------------------------------------

    public Map<Integer, Instant> getSkillCooltimes() {
        return skillCooltimes;
    }

    public boolean hasSkillCooltime(int skillId) {
        final Instant nextAvailable = skillCooltimes.get(skillId);
        return nextAvailable != null && nextAvailable.isAfter(TimeUtil.getCurrentTime());
    }

    public void setSkillCooltime(int skillId, Instant nextAvailable) {
        skillCooltimes.put(skillId, nextAvailable);
    }

    public Set<Integer> expireSkillCooltime(Instant now) {
        final Set<Integer> resetCooltimes = new HashSet<>();
        final var iter = skillCooltimes.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Integer, Instant> entry = iter.next();
            final int skillId = entry.getKey();
            // Battleship durability is stored as cooltime
            if (skillId == SkillConstants.BATTLESHIP_DURABILITY) {
                continue;
            }
            // Check skill cooltime and remove
            final Instant nextAvailable = entry.getValue();
            if (now.isBefore(nextAvailable)) {
                continue;
            }
            iter.remove();
            resetCooltimes.add(skillId);
        }
        return resetCooltimes;
    }


    // SKILL SCHEDULE METHODS ------------------------------------------------------------------------------------------

    public Map<Integer, Instant> getSkillSchedules() {
        return skillSchedules;
    }

    public Instant getSkillSchedule(int skillId) {
        return skillSchedules.getOrDefault(skillId, Instant.MAX);
    }

    public boolean hasSkillSchedule(int skillId) {
        return skillSchedules.containsKey(skillId);
    }

    public void setSkillSchedule(int skillId, Instant nextSchedule) {
        skillSchedules.put(skillId, nextSchedule);
    }


    // STATIC METHODS --------------------------------------------------------------------------------------------------

    public static int getSkillLevel(SecondaryStat ss, SkillManager sm, int skillId) {
        final SkillRecord skillRecord = sm.skillRecords.get(skillId);
        if (skillRecord == null || skillRecord.getSkillLevel() == 0) {
            return 0;
        }
        if (SkillProvider.getSkillInfoById(skillId).map(SkillInfo::isCombatOrders).orElse(false)) {
            return skillRecord.getSkillLevel() + ss.getOption(CharacterTemporaryStat.CombatOrders).nOption;
        }
        return skillRecord.getSkillLevel();
    }
}
