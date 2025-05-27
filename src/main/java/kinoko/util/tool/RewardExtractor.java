package kinoko.util.tool;

import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.quest.QuestItemData;
import kinoko.provider.quest.check.QuestCheck;
import kinoko.provider.quest.check.QuestItemCheck;
import kinoko.provider.reward.Reward;
import kinoko.provider.wz.WzArchive;
import kinoko.provider.wz.WzCrypto;
import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.server.ServerConfig;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

abstract class RewardExtractor {
    public static final Path REACTOR_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Reactor.wz");
    public static final Path REWARD_IMG = Path.of(ServerConfig.WZ_DIRECTORY, "bms", "Reward.img");

    public static void main(String[] args) throws IOException {
        ItemProvider.initialize();
        QuestProvider.initialize();
        StringProvider.initialize();

        // Load reactor actions
        final Map<Integer, String> reactorActions = new HashMap<>();
        try (final WzPackage source = WzPackage.from(REACTOR_WZ)) {
            for (var imageEntry : source.getDirectory().getImages().entrySet()) {
                final int reactorId = Integer.parseInt(imageEntry.getKey().replace(".img", ""));
                if (!(imageEntry.getValue().getItem("action") instanceof String reactorAction)) {
                    System.err.printf("Failed to resolve action for reactor ID : %d%n", reactorId);
                    continue;
                }
                reactorActions.put(reactorId, reactorAction);
            }
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Reactor.wz", e);
        }

        WzCrypto.setCipher(null);
        try (final WzArchive archive = WzArchive.from(REWARD_IMG)) {
            final WzImage rewardImage = archive.getImage();

            // Extract mob rewards
            final Map<Integer, List<Reward>> mobRewards = loadRewards(rewardImage, "m", false);
            try (BufferedWriter bw = Files.newBufferedWriter(Path.of(ServerConfig.DATA_DIRECTORY, "mob_reward.csv"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (var entry : mobRewards.entrySet().stream()
                        .sorted(Comparator.comparingInt(Map.Entry::getKey)).toList()) {
                    final int mobId = entry.getKey();
                    final String mobName = StringProvider.getMobName(mobId);
                    if (mobName == null) {
                        System.err.printf("Could not resolve name for mob ID : %d%n", mobId);
                        continue;
                    }
                    bw.write(String.format("# %s%n", mobName));
                    for (Reward r : entry.getValue().stream()
                            .sorted(Comparator.comparingInt(Reward::getItemId)).toList()) {
                        final String itemName;
                        if (r.isMoney()) {
                            itemName = "money";
                        } else {
                            itemName = StringProvider.getItemName(r.getItemId());
                            if (itemName == null) {
                                System.err.printf("Could not resolve item name for item ID : %d%n", r.getItemId());
                                continue;
                            }
                        }
                        final String line = String.format("%d, %d, %d, %d, %f, %d", mobId, r.getItemId(), r.getMin(), r.getMax(), r.getProb(), r.getQuestId());
                        bw.write(String.format("%-120s# %s%n", line, itemName));
                    }
                    bw.write("\n");
                }
            }

            // Extract reactor rewards
            final Map<Integer, List<Reward>> reactorRewards = loadRewards(rewardImage, "r", true);
            try (BufferedWriter bw = Files.newBufferedWriter(Path.of(ServerConfig.DATA_DIRECTORY, "reactor_reward.csv"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (var entry : reactorRewards.entrySet().stream()
                        .sorted(Comparator.comparingInt(Map.Entry::getKey)).toList()) {
                    final int reactorId = entry.getKey();
                    if (!reactorActions.containsKey(reactorId)) {
                        System.err.printf("Could not resolve reactor action for reactor ID : %d%n", reactorId);
                        continue;
                    }
                    bw.write(String.format("# %s%n", reactorActions.get(reactorId)));
                    for (Reward r : entry.getValue().stream()
                            .sorted(Comparator.comparingInt(Reward::getItemId)).toList()) {
                        final String itemName;
                        if (r.isMoney()) {
                            itemName = "money";
                        } else {
                            itemName = StringProvider.getItemName(r.getItemId());
                            if (itemName == null) {
                                System.err.printf("Could not resolve item name for item ID : %d%n", r.getItemId());
                                continue;
                            }
                        }
                        final String line = String.format("%s, %d, %d, %d, %f, %d", reactorActions.get(reactorId), r.getItemId(), r.getMin(), r.getMax(), r.getProb(), r.getQuestId());
                        bw.write(String.format("%-120s# %s%n", line, itemName));
                    }
                    bw.write("\n");
                }
            }
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Reward.img", e);
        }
    }

    public static Map<Integer, List<Reward>> loadRewards(WzImage image, String prefix, boolean includeMoney) {
        final Map<Integer, List<Reward>> rewardMap = new HashMap<>();
        for (var entry : image.getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzProperty rewardList)) {
                throw new ProviderError("Failed to read reward list");
            }
            if (!entry.getKey().startsWith(prefix)) {
                continue;
            }
            final int templateId = Integer.parseInt(entry.getKey().replaceFirst(prefix, ""));
            final List<Reward> rewards = new ArrayList<>();
            for (var rewardEntry : rewardList.getItems().entrySet()) {
                if (!(rewardEntry.getValue() instanceof WzProperty rewardProp)) {
                    throw new ProviderError("Failed to read reward prop");
                }
                if (rewardProp.getItems().containsKey("dateExpire")) {
                    //System.err.printf("Date expire reward for template %d%n", templateId);
                    continue;
                }
                int money = -1;
                int itemId = -1;
                int min = 1;
                int max = 1;
                double prob = -1;
                for (var propEntry : rewardProp.getItems().entrySet()) {
                    switch (propEntry.getKey()) {
                        case "money" -> {
                            money = WzProvider.getInteger(propEntry.getValue());
                        }
                        case "item" -> {
                            itemId = WzProvider.getInteger(propEntry.getValue());
                        }
                        case "min" -> {
                            min = WzProvider.getInteger(propEntry.getValue());
                        }
                        case "max" -> {
                            max = WzProvider.getInteger(propEntry.getValue());
                        }
                        case "prob" -> {
                            final String propString = WzProvider.getString(propEntry.getValue());
                            assert propString.startsWith("[R8]");
                            prob = Double.parseDouble(propString.replace("[R8]", ""));
                        }
                    }
                }
                if (includeMoney && money > 0 && prob > 0) {
                    rewards.add(Reward.money(money, money, prob));
                } else if (itemId > 0 && prob > 0) {
                    final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
                    if (itemInfoResult.isEmpty()) {
                        System.err.printf("Could not resolve item info for item ID : %d%n", itemId);
                        continue;
                    }
                    final ItemInfo ii = itemInfoResult.get();
                    final List<Integer> questIds = new ArrayList<>();
                    if (ii.isQuest()) {
                        for (QuestInfo qi : QuestProvider.getQuestInfos()) {
                            for (QuestCheck qc : qi.getCompleteChecks()) {
                                if (!(qc instanceof QuestItemCheck qic)) {
                                    continue;
                                }
                                for (QuestItemData itemData : qic.getItems()) {
                                    if (itemData.getItemId() == itemId) {
                                        questIds.add(qi.getQuestId());
                                    }
                                }
                            }
                        }
                    }
                    assert questIds.size() <= 1;
                    rewards.add(Reward.item(itemId, min, max, prob, questIds.stream().findFirst().orElse(0)));
                }
            }
            if (!rewards.isEmpty()) {
                rewardMap.put(templateId, rewards);
            }
        }
        return rewardMap;
    }
}
