package kinoko.world.skill;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return nextAvailable != null && nextAvailable.isAfter(Instant.now());
    }

    public void setSkillCooltime(int skillId, Instant nextAvailable) {
        skillCooltimes.put(skillId, nextAvailable);
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
