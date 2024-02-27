package kinoko.util.tool;

import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.quest.QuestItemData;
import kinoko.provider.quest.check.QuestCheck;
import kinoko.provider.quest.check.QuestItemCheck;
import kinoko.provider.reward.Reward;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

final class BmsExtractor {
    public static final Path REACTOR_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Reactor.wz");
    public static final Path REWARD_IMG = Path.of(ServerConfig.WZ_DIRECTORY, "bms", "Reward.img");

    public static void main(String[] args) throws IOException {
        ItemProvider.initialize();
        QuestProvider.initialize();
        StringProvider.initialize();

        final WzImage rewardImage = readImage(REWARD_IMG);

        // Extract mob rewards
        final Map<Integer, Set<Reward>> mobRewards = loadRewards(rewardImage, "m", false);
        try (BufferedWriter bw = Files.newBufferedWriter(RewardProvider.MOB_REWARD, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
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

        // Load reactor actions
        final Map<Integer, String> reactorActions = new HashMap<>();
        try (final WzReader reader = WzReader.build(REACTOR_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            for (var imageEntry : wzPackage.getDirectory().getImages().entrySet()) {
                final int reactorId = Integer.parseInt(imageEntry.getKey().replace(".img", ""));
                if (!(imageEntry.getValue().getProperty().get("action") instanceof String reactorAction)) {
                    System.err.printf("Failed to resolve action for reactor ID : %d%n", reactorId);
                    continue;
                }
                reactorActions.put(reactorId, reactorAction);
            }
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Reactor.wz", e);
        }

        // Extract reactor rewards
        final Map<Integer, Set<Reward>> reactorRewards = loadRewards(rewardImage, "r", true);
        try (BufferedWriter bw = Files.newBufferedWriter(RewardProvider.REACTOR_REWARD, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
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
    }

    public static Map<Integer, Set<Reward>> loadRewards(WzImage image, String prefix, boolean includeMoney) {
        final Map<Integer, Set<Reward>> rewardMap = new HashMap<>();
        for (var entry : image.getProperty().getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzListProperty rewardList)) {
                throw new ProviderError("Failed to read reward list");
            }
            if (!entry.getKey().startsWith(prefix)) {
                continue;
            }
            final int templateId = Integer.parseInt(entry.getKey().replaceFirst(prefix, ""));
            final Set<Reward> rewards = new HashSet<>();
            for (var rewardEntry : rewardList.getItems().entrySet()) {
                if (!(rewardEntry.getValue() instanceof WzListProperty rewardProp)) {
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
                    final Set<Integer> questIds = new HashSet<>();
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

    private static WzImage readImage(Path path) {
        try (final WzReader reader = WzReader.build(path, new WzReaderConfig(WzConstants.WZ_EMPTY_IV, ServerConstants.GAME_VERSION))) {
            final WzImage image = new WzImage(0);
            if (!(reader.readProperty(image, reader.getBuffer(0)) instanceof WzListProperty listProperty)) {
                throw new WzReaderError("Image property is not a list");
            }
            image.setProperty(listProperty);
            return image;
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Reward.img", e);
        }
    }
}
