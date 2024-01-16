package kinoko.provider;

import kinoko.provider.skill.SkillInfo;
import kinoko.provider.wz.WzConstants;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.WzReader;
import kinoko.provider.wz.WzReaderConfig;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.world.job.Job;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class SkillProvider implements WzProvider {
    public static final Path SKILL_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Skill.wz");
    private static final Map<Job, Set<SkillInfo>> jobSkills = new EnumMap<>(Job.class);
    private static final Map<Integer, SkillInfo> skillInfos = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(SKILL_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadSkillInfos(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Skill.wz", e);
        }
    }

    public static Set<SkillInfo> getSkillsForJob(Job job) {
        return jobSkills.getOrDefault(job, Set.of());
    }

    public static Optional<SkillInfo> getSkillInfoById(int skillId) {
        if (!skillInfos.containsKey(skillId)) {
            return Optional.empty();
        }
        return Optional.of(skillInfos.get(skillId));
    }

    private static void loadSkillInfos(WzPackage source) throws ProviderError {
        for (var imageEntry : source.getDirectory().getImages().entrySet()) {
            final String imageName = imageEntry.getKey().replace(".img", "");
            if (!imageName.matches("[0-9]+")) {
                continue;
            }
            final short jobId = Short.parseShort(imageName);
            final Job job = Job.getById(jobId);
            if (!(imageEntry.getValue().getProperty().get("skill") instanceof WzListProperty skillList)) {
                throw new ProviderError("Failed to resolve skills for job ID : {}", jobId);
            }
            for (var skillEntry : skillList.getItems().entrySet()) {
                final int skillId = Integer.parseInt(skillEntry.getKey());
                if (!(skillEntry.getValue() instanceof WzListProperty skillProp)) {
                    throw new ProviderError("Failed to resolve item property");
                }
                final SkillInfo skillInfo = SkillInfo.from(skillId, skillProp);

                if (!jobSkills.containsKey(job)) {
                    jobSkills.put(job, new HashSet<>());
                }
                jobSkills.get(job).add(skillInfo);

                skillInfos.put(skillId, skillInfo);
            }
        }
    }
}
