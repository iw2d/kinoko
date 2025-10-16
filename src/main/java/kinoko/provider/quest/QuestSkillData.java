package kinoko.provider.quest;

import kinoko.meta.SkillId;
import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;

import java.util.*;

public final class QuestSkillData {
    private final SkillId skillId;
    private final int skillLevel;
    private final int masterLevel;
    private final boolean onlyMasterLevel;
    private final Set<Integer> jobs;

    public QuestSkillData(SkillId skillId, int skillLevel, int masterLevel, boolean onlyMasterLevel, Set<Integer> jobs) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.masterLevel = masterLevel;
        this.onlyMasterLevel = onlyMasterLevel;
        this.jobs = jobs;
    }

    public SkillId getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public boolean isOnlyMasterLevel() {
        return onlyMasterLevel;
    }

    public Set<Integer> getJobs() {
        return jobs;
    }

    public static List<QuestSkillData> resolveSkillData(WzProperty skillList) {
        final List<QuestSkillData> skills = new ArrayList<>();
        for (var skillEntry : skillList.getItems().entrySet()) {
            if (!(skillEntry.getValue() instanceof WzProperty skillProp)) {
                throw new ProviderError("Failed to resolve quest skill list");
            }
            if (!(skillProp.get("job") instanceof WzProperty jobList)) {
                throw new ProviderError("Failed to resolve quest skill job list");
            }
            final Set<Integer> jobs = new HashSet<>();
            for (var jobEntry : jobList.getItems().entrySet()) {
                jobs.add(WzProvider.getInteger(jobEntry.getValue()));
            }
            skills.add(new QuestSkillData(
                    SkillId.fromValue(WzProvider.getInteger(skillProp.get("id"))),
                    WzProvider.getInteger(skillProp.get("skillLevel")),
                    WzProvider.getInteger(skillProp.get("masterLevel")),
                    WzProvider.getInteger(skillProp.get("onlyMasterLevel"), 0) != 0,
                    Collections.unmodifiableSet(jobs)
            ));
        }
        return skills;
    }
}
