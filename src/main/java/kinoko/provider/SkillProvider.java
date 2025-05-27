package kinoko.provider;

import kinoko.provider.mob.MobSkillType;
import kinoko.provider.skill.MorphInfo;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SummonInfo;
import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.server.ServerConfig;
import kinoko.world.job.Job;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class SkillProvider implements WzProvider {
    public static final Path SKILL_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Skill.wz");
    public static final Path MORPH_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Morph.wz");
    private static final Map<Job, List<SkillInfo>> jobSkills = new EnumMap<>(Job.class);
    private static final Map<Integer, SkillInfo> skillInfos = new HashMap<>();
    private static final Map<Integer, SkillInfo> mobSkills = new HashMap<>();
    private static final Map<Integer, SummonInfo> mobSummons = new HashMap<>(); // skill level -> summon info
    private static final Map<Integer, MorphInfo> morphInfos = new HashMap<>();

    public static void initialize() {
        // Skill.wz
        try (final WzPackage source = WzPackage.from(SKILL_WZ)) {
            loadSkillInfos(source);
            loadMobSkills(source);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Skill.wz", e);
        }
        // Morph.wz
        try (final WzPackage source = WzPackage.from(MORPH_WZ)) {
            loadMorphInfos(source);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Morph.wz", e);
        }
    }

    public static List<SkillInfo> getSkillsForJob(Job job) {
        return jobSkills.getOrDefault(job, List.of());
    }

    public static Optional<SkillInfo> getSkillInfoById(int skillId) {
        return Optional.ofNullable(skillInfos.get(skillId));
    }

    public static Optional<SkillInfo> getMobSkillInfoById(int skillId) {
        return Optional.ofNullable(mobSkills.get(skillId));
    }

    public static Optional<SummonInfo> getMobSummonInfoByLevel(int skillLevel) {
        return Optional.ofNullable(mobSummons.get(skillLevel));
    }

    public static Optional<MorphInfo> getMorphInfoById(int morphId) {
        return Optional.ofNullable(morphInfos.get(morphId));
    }

    private static void loadSkillInfos(WzPackage source) throws ProviderError {
        for (var imageEntry : source.getDirectory().getImages().entrySet()) {

            final String imageName = imageEntry.getKey().replace(".img", "");
            if (!imageName.matches("[0-9]+")) {
                continue;
            }
            final short jobId = Short.parseShort(imageName);
            final Job job = Job.getById(jobId);
            if (!(imageEntry.getValue().getItem("skill") instanceof WzProperty skillList)) {
                throw new ProviderError("Failed to resolve skills for job ID : %d", jobId);
            }
            for (var skillEntry : skillList.getItems().entrySet()) {
                final int skillId = Integer.parseInt(skillEntry.getKey());
                if (!(skillEntry.getValue() instanceof WzProperty skillProp)) {
                    throw new ProviderError("Failed to resolve item property");
                }
                final SkillInfo skillInfo = SkillInfo.from(skillId, skillProp);

                if (!jobSkills.containsKey(job)) {
                    jobSkills.put(job, new ArrayList<>());
                }
                jobSkills.get(job).add(skillInfo);
                skillInfos.put(skillId, skillInfo);
            }
        }
    }

    private static void loadMobSkills(WzPackage source) throws ProviderError {
        if (!(source.getItem("MobSkill.img") instanceof WzImage mobSkill)) {
            throw new ProviderError("Failed to resolve MobSkill.img");
        }
        for (var entry : mobSkill.getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzProperty skillProp)) {
                throw new ProviderError("Failed to resolve mob skill property");
            }
            final int skillId = Integer.parseInt(entry.getKey());
            final SkillInfo skillInfo = SkillInfo.from(skillId, skillProp);
            mobSkills.put(skillId, skillInfo);
            if (skillId != MobSkillType.SUMMON.getId()) {
                continue;
            }
            // Resolve mob summons
            if (!(skillProp.get("level") instanceof WzProperty levelProps)) {
                throw new ProviderError("Failed to resolve mob summons");
            }
            for (int slv = 1; slv <= skillInfo.getMaxLevel(); slv++) {
                if (!(levelProps.get(String.valueOf(slv)) instanceof WzProperty summonProp)) {
                    throw new ProviderError("Failed to resolve mob summons");
                }
                final List<Integer> summons = new ArrayList<>();
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if (summonProp.get(String.valueOf(i)) == null) {
                        break;
                    }
                    summons.add(WzProvider.getInteger(summonProp.get(String.valueOf(i))));
                }
                mobSummons.put(slv, new SummonInfo(Collections.unmodifiableList(summons)));
            }
        }
    }

    private static void loadMorphInfos(WzPackage source) throws ProviderError {
        for (var imageEntry : source.getDirectory().getImages().entrySet()) {
            final String imageName = imageEntry.getKey().replace(".img", "");
            if (!imageName.matches("[0-9]+")) {
                continue;
            }
            final int morphId = Integer.parseInt(imageName);
            if (!(imageEntry.getValue().getItem("info") instanceof WzProperty infoProp)) {
                throw new ProviderError("Failed to resolve morph info property");
            }
            morphInfos.put(morphId, MorphInfo.from(morphId, infoProp));
        }
    }
}
