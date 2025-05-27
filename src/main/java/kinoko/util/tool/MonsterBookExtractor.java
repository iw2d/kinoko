package kinoko.util.tool;

import kinoko.provider.*;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.reward.Reward;
import kinoko.provider.wz.WzArchive;
import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.world.item.ItemConstants;

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
        MobProvider.initialize();
        QuestProvider.initialize();
        StringProvider.initialize();

        // Load monster book rewards
        try (final WzPackage source = WzPackage.from(StringProvider.STRING_WZ)) {
            loadMonsterBookRewards(source);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading String.wz", e);
        }

        // Load BMS rewards
        try (final WzArchive archive = WzArchive.from(RewardExtractor.REWARD_IMG)) {
            final WzImage rewardImage = archive.getImage();
            final Map<Integer, List<Reward>> mobRewards = loadRewards(rewardImage, "m", false);

            // Create YAML
            for (var entry : monsterBookRewards.entrySet()) {
                final int mobId = entry.getKey();

                final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(mobId);
                if (mobTemplateResult.isEmpty()) {
                    throw new IllegalStateException("Could not resolve mob template for mob ID : " + mobId);
                }
                final MobTemplate mobTemplate = mobTemplateResult.get();

                final List<Reward> bmsRewards = mobRewards.getOrDefault(mobId, List.of());
                final Path filePath = Path.of(RewardProvider.REWARD_DATA.toString(), String.format("%d.yaml", mobId));
                if (filePath.toFile().exists()) {
                    continue;
                }
                try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    bw.write(String.format("# %s (%d)\n\n", StringProvider.getMobName(mobId), mobId));
                    bw.write("rewards:\n");
                    for (int itemId : entry.getValue().stream()
                            .sorted(Comparator.comparingInt(Integer::intValue)).toList()) {
                        bw.write(formatReward(itemId, mobTemplate.isBoss()));
                    }
                    for (Reward reward : bmsRewards) {
                        if (reward.isQuest()) {
                            bw.write(String.format("  - [ %d, %d, %d, %f, %d ] # %s\n", reward.getItemId(), reward.getMin(), reward.getMax(), reward.getProb(), reward.getQuestId(), StringProvider.getItemName(reward.getItemId())));
                        }
                    }
                }
            }

            // Check missing mobs from BMS
            for (var entry : mobRewards.entrySet()) {
                final int mobId = entry.getKey();
                if (monsterBookRewards.containsKey(mobId)) {
                    continue;
                }
                if (mobId == 9300018) {
                    // Tutorial sentinel - BMS has a different item ID
                    continue;
                }
                final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(mobId);
                if (mobTemplateResult.isEmpty()) {
                    System.err.println("Could not resolve mob template for mob ID : " + mobId);
                    continue;
                }
                final MobTemplate mobTemplate = mobTemplateResult.get();
                final Path filePath = Path.of(RewardProvider.REWARD_DATA.toString(), String.format("%d.yaml", mobId));
                if (filePath.toFile().exists()) {
                    continue;
                }
                try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    bw.write(String.format("# %s (%d)\n\n", StringProvider.getMobName(mobId), mobId));
                    bw.write("rewards:\n");
                    for (Reward reward : entry.getValue().stream()
                            .sorted(Comparator.comparingInt(Reward::getItemId)).toList()) {
                        if (reward.isQuest()) {
                            bw.write(String.format("  - [ %d, %d, %d, %f, %d ] # %s\n", reward.getItemId(), reward.getMin(), reward.getMax(), reward.getProb(), reward.getQuestId(), StringProvider.getItemName(reward.getItemId())));
                        } else {
                            if ((mobId >= 9000000 && mobId < 9100000) || (mobId >= 9300000 && mobId < 9400000)) {
                                // event - pq mobs
                                bw.write(String.format("  - [ %d, %d, %d, %f ] # %s\n", reward.getItemId(), reward.getMin(), reward.getMax(), reward.getProb(), StringProvider.getItemName(reward.getItemId())));
                            } else {
                                bw.write(formatReward(reward.getItemId(), mobTemplate.isBoss()));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void loadMonsterBookRewards(WzPackage source) throws ProviderError {
        if (!((WzImage) source.getItem("MonsterBook.img") instanceof WzImage monsterBookImage)) {
            throw new ProviderError("Could not resolve String.wz/MonsterBook.img");
        }
        for (var entry : monsterBookImage.getItems().entrySet()) {
            final int mobId = WzProvider.getInteger(entry.getKey());
            if (!(entry.getValue() instanceof WzProperty entryProp) ||
                    !(entryProp.get("reward") instanceof WzProperty rewardProp)) {
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

    private static String formatReward(int itemId, boolean isBoss) {
        int min = 1;
        int max = 1;
        double prob = 0;
        if (ItemConstants.isEquip(itemId)) {
            if (isBoss) {
                prob = 0.0002;
            } else {
                prob = 0.0001;
            }
        } else if (ItemConstants.isConsume(itemId)) {
            if (itemId / 10000 == 204) {
                if (itemId == 2049100) {
                    // chaos scroll
                    if (isBoss) {
                        prob = 0.000100;
                    } else {
                        prob = 0.000001;
                    }
                } else {
                    // scroll
                    if (isBoss) {
                        prob = 0.010000;
                    } else {
                        prob = 0.000100;
                    }
                }
            } else if (itemId / 10000 == 200 || itemId / 10000 == 201 || itemId / 10000 == 202 || itemId / 10000 == 205) {
                // potion
                if (isBoss) {
                    prob = 0.100000;
                } else {
                    if (itemId == 2000004 || itemId == 2000005) {
                        prob = 0.001000; // elixir / power elixir
                    } else {
                        prob = 0.010000;
                    }
                }
            } else if (itemId / 10000 == 206) {
                min = 10;
                max = 20;
                prob = 0.008000; // arrows
            } else if (itemId / 10000 == 238) {
                prob = 0.02; // monster book card
            } else if (ItemConstants.isRechargeableItem(itemId)) {
                prob = 0.0004;
            } else if (itemId / 10000 == 228) {
                prob = 0.200000; // skill book
            } else if (itemId / 10000 == 229) {
                // mastery book
                if (isBoss) {
                    prob = 0.100000;
                } else {
                    prob = 0.000100;
                }
            }
        } else if (itemId / 1000 == 4000) {
            if (itemId == 4000021) {
                prob = 0.040000; // Leather
            } else {
                prob = 0.400000; // mob ETC
            }
        } else if (itemId / 10000 == 401 || itemId / 10000 == 402) {
            prob = 0.002000; // ore
        } else if (itemId / 1000 == 4004) {
            prob = 0.001000; // crystal ore
        } else if (itemId / 10000 == 403) {
            prob = 0.050000; // monster card / omok piece / quest item
        } else if (itemId / 10000 == 413 || itemId / 1000 == 4007) {
            prob = 0.000300; // production stims || magic powder
        } else if (itemId / 1000 == 4006) {
            prob = 0.000700; // the magic rock / summoning rock
        } else if (itemId / 1000 == 4003) {
            prob = 0.040000; // stiff/ soft feather
        }
        return String.format("  - [ %d, %d, %d, %f ] # %s\n", itemId, min, max, prob, StringProvider.getItemName(itemId));
    }
}
