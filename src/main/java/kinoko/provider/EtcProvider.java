package kinoko.provider;

import kinoko.provider.item.SetItemInfo;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.cashshop.Commodity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class EtcProvider implements WzProvider {
    public static final Path ETC_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Etc.wz");
    // Item info
    private static final Set<SetItemInfo> setItemInfos = new HashSet<>();
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

    public static Set<SetItemInfo> getSetItemInfos() {
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
