package kinoko.util.tool;

import kinoko.provider.*;
import kinoko.provider.reward.Reward;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

final class MonsterBookExtractor extends RewardExtractor {
    private static final Map<Integer, List<Integer>> monsterBookRewards = new HashMap<>(); // mob id -> item ids

    public static void main(String[] args) throws IOException {
        ItemProvider.initialize();
        QuestProvider.initialize();
        StringProvider.initialize();

        // Load monster book rewards
        try (final WzReader reader = WzReader.build(StringProvider.STRING_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadMonsterBookRewards(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading String.wz", e);
        }

        // Load BMS rewards
        final WzImage rewardImage = readImage(RewardExtractor.REWARD_IMG);
        final Map<Integer, List<Reward>> mobRewards = loadRewards(rewardImage, "m", false);

        // Create YAML
        for (var entry : monsterBookRewards.entrySet()) {
            final int mobId = entry.getKey();

            final List<Reward> bmsRewards = mobRewards.getOrDefault(mobId, List.of());

            final Path filePath = Path.of(RewardProvider.REWARD_DATA.toString(), String.format("%d.yaml", mobId));
            try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                bw.write(String.format("# %s (%d)\n\n", StringProvider.getMobName(mobId), mobId));
                bw.write("rewards:\n");
                for (int itemId : entry.getValue().stream()
                        .sorted(Comparator.comparingInt(Integer::intValue)).toList()) {
                    final Optional<Reward> rewardResult = bmsRewards.stream().filter((r) -> r.getItemId() == itemId).findFirst();
                    if (rewardResult.isEmpty()) {
                        bw.write(String.format("  - [ %d, %d, %d, %f ] # %s#TODO\n", itemId, 1, 1, 0.1, StringProvider.getItemName(itemId)));
                    } else {
                        final Reward reward = rewardResult.get();
                        assert !reward.isQuest();
                        bw.write(String.format("  - [ %d, %d, %d, %f ] # %s\n", itemId, reward.getMin(), reward.getMax(), reward.getProb(), StringProvider.getItemName(itemId)));
                    }
                }
                for (Reward reward : bmsRewards) {
                    if (reward.isQuest()) {
                        bw.write(String.format("  - [ %d, %d, %d, %f, %d ] # %s\n", reward.getItemId(), reward.getMin(), reward.getMax(), reward.getProb(), reward.getQuestId(), StringProvider.getItemName(reward.getItemId())));
                    }
                }
            }
        }
    }

    private static void loadMonsterBookRewards(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("MonsterBook.img") instanceof WzImage monsterBookImage)) {
            throw new ProviderError("Could not resolve String.wz/MonsterBook.img");
        }
        for (var entry : monsterBookImage.getProperty().getItems().entrySet()) {
            final int mobId = WzProvider.getInteger(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty entryProp) ||
                    !(entryProp.get("reward") instanceof WzListProperty rewardProp)) {
                throw new ProviderError("Could not resolve monster book info");
            }
            final List<Integer> rewards = new ArrayList<>();
            for (var rewardEntry : rewardProp.getItems().entrySet()) {
                final int itemId = WzProvider.getInteger(rewardEntry.getValue());
                rewards.add(itemId);
            }
            monsterBookRewards.put(mobId, Collections.unmodifiableList(rewards));
        }
    }
}
