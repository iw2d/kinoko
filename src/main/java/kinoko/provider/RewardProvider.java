package kinoko.provider;

import kinoko.provider.reward.Reward;
import kinoko.server.ServerConfig;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class RewardProvider implements DataProvider {
    public static final Path REWARD_DATA = Path.of(ServerConfig.DATA_DIRECTORY, "reward");
    private static final Map<Integer, List<Reward>> mobRewards = new HashMap<>(); // mobId -> rewards

    public static void initialize() {
        final Load yamlLoader = new Load(LoadSettings.builder().build());
        try (final Stream<Path> paths = Files.list(REWARD_DATA)) {
            for (Path path : paths.toList()) {
                final String fileName = path.getFileName().toString();
                if (!fileName.endsWith(".yaml")) {
                    continue;
                }
                final int mobId = Integer.parseInt(fileName.replace(".yaml", ""));
                try (final InputStream is = Files.newInputStream(path)) {
                    loadMobRewards(mobId, yamlLoader.loadFromInputStream(is));
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception caught while loading Reward Data", e);
        }
    }

    public static List<Reward> getMobRewards(int mobId) {
        return mobRewards.getOrDefault(mobId, List.of());
    }

    public static Map<Integer, List<Reward>> getAllMobRewards() {
        return Collections.unmodifiableMap(mobRewards);
    }

    private static void loadMobRewards(int mobId, Object yamlObject) throws ProviderError {
        if (!(yamlObject instanceof Map<?, ?> rewardData)) {
            throw new ProviderError("Could not resolve reward data for mob ID : %d", mobId);
        }
        if (!(rewardData.get("rewards") instanceof List<?> rewardList)) {
            // No Rewards
            return;
        }
        final List<Reward> rewards = new ArrayList<>();
        for (Object rewardObject : rewardList) {
            if (!(rewardObject instanceof List<?> rewardInfo)) {
                throw new ProviderError("Could not resolve reward info for npc ID : %d", mobId);
            }
            final int itemId = ((Number) rewardInfo.get(0)).intValue();
            final int min = ((Number) rewardInfo.get(1)).intValue();
            final int max = ((Number) rewardInfo.get(2)).intValue();
            final double prob = ((Number) rewardInfo.get(3)).doubleValue();
            final int questId = rewardInfo.size() > 4 ? ((Number) rewardInfo.get(4)).intValue() : 0;
            final int fieldId = rewardInfo.size() > 5 ? ((Number) rewardInfo.get(5)).intValue() : 0;
            rewards.add(Reward.item(itemId, min, max, prob, questId, fieldId));
        }
        mobRewards.put(mobId, Collections.unmodifiableList(rewards));
    }
}
