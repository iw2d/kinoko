package kinoko.provider;

import kinoko.provider.item.SetItemInfo;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.cashshop.Commodity;
import kinoko.server.maker.MakerInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class EtcProvider implements WzProvider {
    public static final Path ETC_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Etc.wz");
    // Item info
    private static final List<SetItemInfo> setItemInfos = new ArrayList<>();
    // ItemMake info
    private static final Map<Integer, MakerInfo> itemMakeInfo = new HashMap<>(); // Item ID -> ItemMake Data
    // CashShop info
    private static final Map<Integer, Commodity> commodities = new HashMap<>(); // commodity id -> commodity
    private static final Map<Integer, List<Integer>> cashPackages = new HashMap<>(); // package id -> set<commodity id>
    // Other info
    private static final Set<Integer> titleQuestIds = new HashSet<>();
    private static final Set<String> forbiddenNames = new HashSet<>();
    private static final Map<Integer, Set<Integer>> makeCharInfo = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(ETC_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadSetItemInfo(wzPackage);
            loadCashShop(wzPackage);
            loadTitleQuestIds(wzPackage);
            loadForbiddenNames(wzPackage);
            loadMakeCharInfo(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Etc.wz", e);
        }
    }

    public static Map<Integer, MakerInfo> getItemMakeInfo() {
        return itemMakeInfo;
    }

    public static List<SetItemInfo> getSetItemInfos() {
        return setItemInfos;
    }

    public static Map<Integer, Commodity> getCommodities() {
        return commodities;
    }

    public static Map<Integer, List<Integer>> getCashPackages() {
        return cashPackages;
    }

    public static boolean isTitleQuest(int questId) {
        return titleQuestIds.contains(questId);
    }

    public static boolean isForbiddenName(String name) {
        return forbiddenNames.contains(name.toLowerCase());
    }

    public static boolean isValidStartingItem(int index, int id) {
        return makeCharInfo.getOrDefault(index, Set.of()).contains(id);
    }

    private static void loadSetItemInfo(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("SetItemInfo.img") instanceof WzImage infoImage)) {
            throw new ProviderError("Could not resolve Etc.wz/SetItemInfo.img");
        }
        for (var entry : infoImage.getProperty().getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzListProperty setItemProp)) {
                throw new ProviderError("Could not resolve set item info prop");
            }
            setItemInfos.add(SetItemInfo.from(setItemProp));
        }
    }

    private static void loadCashShop(WzPackage source) throws ProviderError {
        // Load commodities
        if (!(source.getDirectory().getImages().get("Commodity.img") instanceof WzImage commodityImage)) {
            throw new ProviderError("Could not resolve Etc.wz/Commodity.img");
        }
        for (var entry : commodityImage.getProperty().getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzListProperty commodityProp)) {
                throw new ProviderError("Failed to resolve commodity");
            }
            final int commodityId = WzProvider.getInteger(commodityProp.get("SN"));
            commodities.put(commodityId, new Commodity(
                    commodityId,
                    WzProvider.getInteger(commodityProp.get("ItemId")),
                    WzProvider.getInteger(commodityProp.get("Count"), 1),
                    WzProvider.getInteger(commodityProp.get("Price"), 0),
                    WzProvider.getInteger(commodityProp.get("Period"), 0),
                    WzProvider.getInteger(commodityProp.get("Gender"), 2),
                    WzProvider.getInteger(commodityProp.get("OnSale"), 0) != 0
            ));
        }
        // Load cash packages
        if (!(source.getDirectory().getImages().get("CashPackage.img") instanceof WzImage cashPackageImage)) {
            throw new ProviderError("Could not resolve Etc.wz/CashPackage.img");
        }
        for (var entry : cashPackageImage.getProperty().getItems().entrySet()) {
            final int packageId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty cashPackageProp) ||
                    !(cashPackageProp.get("SN") instanceof WzListProperty snProp)) {
                throw new ProviderError("Failed to resolve cash package");
            }
            final List<Integer> commodityIds = new ArrayList<>();
            for (var snEntry : snProp.getItems().entrySet()) {
                commodityIds.add(WzProvider.getInteger(snEntry.getValue()));
            }
            cashPackages.put(packageId, Collections.unmodifiableList(commodityIds));
        }
    }

    private static void loadItemMake(WzPackage source) throws ProviderError {
        // Load ItemMake data
        if (!(source.getDirectory().getImages().get("ItemMake.img") instanceof WzImage itemMakeImage)) {
            throw new ProviderError("Could not resolve Etc.wz/ItemMake.img");
        }

        for (var outerEntry : itemMakeImage.getProperty().getItems().entrySet()) {
            String groupKey = outerEntry.getKey();

            if (!(outerEntry.getValue() instanceof WzListProperty groupProp)) {
                System.err.println("Failed to resolve group property for key: " + groupKey);
                continue;
            }

            for (var itemEntry : groupProp.getItems().entrySet()) {
                String itemKey = itemEntry.getKey();

                if (!(itemEntry.getValue() instanceof WzListProperty itemProp)) {
                    System.err.println("Failed to resolve item property for key: " + itemKey);
                    continue;
                }

                try {
                    // Extract item properties
                    final int itemId = Integer.parseInt(itemKey); // Use the secondary key as itemId
                    final int reqLevel = WzProvider.getInteger(itemProp.get("reqLevel"), 0);
                    final int reqSkillLevel = WzProvider.getInteger(itemProp.get("reqSkillLevel"), 0);
                    final int itemNum = WzProvider.getInteger(itemProp.get("itemNum"), 0);
                    final int tuc = WzProvider.getInteger(itemProp.get("tuc"), 0);
                    final int meso = WzProvider.getInteger(itemProp.get("meso"), 0);
                    final int catalyst = WzProvider.getInteger(itemProp.get("catalyst"), 0);

                    // Parse random rewards
                    List<MakerInfo.Reward> randomRewards = new ArrayList<>();
                    if (itemProp.get("randomReward") instanceof WzListProperty randomRewardProp) {
                        // Loop through the entries in randomReward
                        for (var rewardEntry : randomRewardProp.getItems().entrySet()) {

                            if (rewardEntry.getValue() instanceof WzListProperty rewardProp) {
                                final int rewardItem = WzProvider.getInteger(rewardProp.get("item"), 0);
                                final int rewardCount = WzProvider.getInteger(rewardProp.get("itemNum"), 0);
                                final int rewardProb = WzProvider.getInteger(rewardProp.get("prob"), 0);

                                // Add to the rewards list
                                randomRewards.add(new MakerInfo.Reward(rewardItem, rewardCount, rewardProb));
                            }
                        }
                    }

                    // Parse recipes
                    List<MakerInfo.Recipe> recipeList = new ArrayList<>();
                    if (itemProp.get("recipe") instanceof WzListProperty recipeProp) {

                        for (var recipeEntry : recipeProp.getItems().entrySet()) {

                            if (recipeEntry.getValue() instanceof WzListProperty recipeItemProp) {
                                final int recipeItem = WzProvider.getInteger(recipeItemProp.get("item"), 0);
                                final int recipeCount = WzProvider.getInteger(recipeItemProp.get("count"), 0);

                                // Add to the recipe list
                                recipeList.add(new MakerInfo.Recipe(recipeItem, recipeCount));
                            }
                        }
                    }

                    // Create MakerInfo and store it in the map
                    MakerInfo makerInfo = new MakerInfo(itemId, reqLevel, reqSkillLevel, itemNum, tuc, meso, catalyst, recipeList, randomRewards);
                    itemMakeInfo.put(itemId, makerInfo);

                } catch (NumberFormatException ex) {
                    System.err.println("Invalid itemKey format: " + itemKey + " - " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("Error processing itemKey: " + itemKey + " - " + ex.getMessage());
                }
            }
        }
    }

    private static void loadTitleQuestIds(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("QuestCategory.img") instanceof WzImage categoryImage)) {
            throw new ProviderError("Could not resolve Etc.wz/QuestCategory.img");
        }
        final Set<Integer> titleQuestCategories = new HashSet<>();
        for (var entry : categoryImage.getProperty().getItems().entrySet()) {
            final int categoryId = WzProvider.getInteger(entry.getKey());
            final String categoryName = WzProvider.getString(entry.getValue());
            if (categoryName.equalsIgnoreCase("Title")) {
                titleQuestCategories.add(categoryId);
            }
        }
        for (QuestInfo questInfo : QuestProvider.getQuestInfos()) {
            if (titleQuestCategories.contains(questInfo.getQuestArea())) {
                titleQuestIds.add(questInfo.getQuestId());
            }
        }
    }

    private static void loadForbiddenNames(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("ForbiddenName.img") instanceof WzImage nameImage)) {
            throw new ProviderError("Could not resolve Etc.wz/ForbiddenName.img");
        }
        for (var value : nameImage.getProperty().getItems().values()) {
            if (value instanceof String name) {
                forbiddenNames.add(name);
            }
        }
    }

    private static void loadMakeCharInfo(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("MakeCharInfo.img") instanceof WzImage infoImage)) {
            throw new ProviderError("Could not resolve Etc.wz/MakeCharInfo.img");
        }
        for (var entry : infoImage.getProperty().getItems().entrySet()) {
            if (entry.getKey().equals("Name")) {
                continue;
            }
            if (!(entry.getValue() instanceof WzListProperty prop)) {
                throw new ProviderError("Failed to resolve MakeCharInfo");
            }
            if (entry.getKey().equals("Info")) {
                addMakeCharInfo(prop.get("CharFemale"));
                addMakeCharInfo(prop.get("CharMale"));
            } else {
                addMakeCharInfo(prop);
            }
        }
    }

    private static void addMakeCharInfo(WzListProperty prop) {
        for (var propEntry : prop.getItems().entrySet()) {
            final int index = Integer.parseInt(propEntry.getKey());
            if (!(propEntry.getValue() instanceof WzListProperty idList)) {
                throw new ProviderError("Failed to resolve MakeCharInfo");
            }
            for (var idEntry : idList.getItems().entrySet()) {
                final int id = (Integer) idEntry.getValue();
                if (!makeCharInfo.containsKey(index)) {
                    makeCharInfo.put(index, new HashSet<>());
                }
                makeCharInfo.get(index).add(id);
            }
        }
    }
}
