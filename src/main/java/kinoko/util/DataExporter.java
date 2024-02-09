package kinoko.util;

import kinoko.provider.SkillProvider;
import kinoko.provider.StringProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class DataExporter {
    public static void main(String[] args) {
        exportJobSkills();
    }

    private static void exportJobSkills() {
        final Map<String, Map<String, Predicate<Integer>>> jobClassifier = Map.of(
                "explorer", Map.of(
                        "Beginner", (jobId) -> jobId == 0,
                        "Warrior", (jobId) -> jobId / 100 == 1,
                        "Magician", (jobId) -> jobId / 100 == 2,
                        "Bowman", (jobId) -> jobId / 100 == 3,
                        "Thief", (jobId) -> jobId / 100 == 4,
                        "Pirate", (jobId) -> jobId / 100 == 5
                ),
                "cygnus", Map.of(
                        "Noblesse", (jobId) -> jobId == 1000,
                        "DawnWarrior", (jobId) -> jobId / 100 == 11,
                        "BlazeWizard", (jobId) -> jobId / 100 == 12,
                        "WindArcher", (jobId) -> jobId / 100 == 13,
                        "NightWalker", (jobId) -> jobId / 100 == 14,
                        "ThunderBreaker", (jobId) -> jobId / 100 == 15
                ),
                "legend", Map.of(
                        "Aran", JobConstants::isAranJob,
                        "Evan", JobConstants::isEvanJob
                ),
                "resistance", Map.of(
                        "Citizen", (jobId) -> jobId == 3000,
                        "BattleMage", (jobId) -> jobId / 100 == 32,
                        "WildHunter", (jobId) -> jobId / 100 == 33,
                        "Mechanic", (jobId) -> jobId / 100 == 35
                )
        );
        final Map<String, Map<String, Set<Integer>>> jobSkills = new HashMap<>();

        SkillProvider.initialize();
        StringProvider.initialize();

        // Populate jobSkills
        for (Job job : Job.values()) {
            for (SkillInfo si : SkillProvider.getSkillsForJob(job)) {
                final int skillId = si.getId();
                final int jobId = skillId / 10000;
                for (var x : jobClassifier.entrySet()) {
                    for (var y : x.getValue().entrySet()) {
                        if (!y.getValue().test(jobId)) {
                            continue;
                        }
                        final String jobType = x.getKey();
                        final String jobName = y.getKey();
                        if (!jobSkills.containsKey(jobType)) {
                            jobSkills.put(jobType, new HashMap<>());
                        }
                        if (!jobSkills.get(jobType).containsKey(jobName)) {
                            jobSkills.get(jobType).put(jobName, new HashSet<>());
                        }
                        jobSkills.get(jobType).get(jobName).add(skillId);
                    }
                }
            }
        }

        // Resolve skill names
        for (var x : jobSkills.entrySet()) {
            for (var y : x.getValue().entrySet()) {
                final String jobType = x.getKey();
                final String jobName = y.getKey();
                System.out.printf("public final class %s {%n", jobName);
                y.getValue().stream().sorted().forEach((skillId) -> {
                    final String skillName = StringProvider.getSkillName(skillId);
                    final String variableName;
                    if (skillName != null) {
                        variableName = skillName.replace(' ', '_').replace("'", "").toUpperCase();
                    } else {
                        variableName = "UNK_" + skillId;
                    }
                    System.out.printf("    public static final int %s = %d;%n", variableName, skillId);
                });
                System.out.println("}");
                System.out.println();
            }
        }
    }
}
