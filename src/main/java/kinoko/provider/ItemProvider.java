package kinoko.provider;

import kinoko.provider.item.*;
import kinoko.provider.wz.WzDirectory;
import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.server.ServerConfig;
import kinoko.util.Util;
import kinoko.world.item.BodyPart;
import kinoko.world.item.ItemConstants;
import kinoko.world.item.ItemGrade;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class ItemProvider implements WzProvider {
    public static final Path CHARACTER_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Character.wz");
    public static final Path ITEM_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Item.wz");
    public static final List<String> EQUIP_TYPES = List.of("Accessory", "Cap", "Cape", "Coat", "Dragon", "Face", "Glove", "Hair", "Longcoat", "Mechanic", "Pants", "PetEquip", "Ring", "Shield", "Shoes", "TamingMob", "Weapon");
    public static final List<String> ITEM_TYPES = List.of("Consume", "Install", "Etc", "Cash");
    private static final Map<Integer, ItemInfo> itemInfos = new HashMap<>();
    private static final Map<Integer, ItemOptionInfo> itemOptionInfos = new HashMap<>(); // item option id -> item option info
    private static final Map<Integer, ItemRewardInfo> itemRewardInfos = new HashMap<>();
    private static final Map<Integer, MobSummonInfo> mobSummonInfos = new HashMap<>();
    private static final Map<Integer, Set<Integer>> petEquips = new HashMap<>(); // petEquipId -> set<petTemplateId>
    private static final Map<Integer, Map<Integer, PetInteraction>> petActions = new HashMap<>(); // petTemplateId -> (action -> PetInteraction)
    private static final Map<Integer, String> specialItemNames = new HashMap<>();

    public static void initialize() {
        // Character.wz
        try (final WzPackage source = WzPackage.from(CHARACTER_WZ)) {
            loadEquipInfos(source);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Character.wz", e);
        }
        // Item.wz
        try (final WzPackage source = WzPackage.from(ITEM_WZ)) {
            loadItemInfos(source);
            loadItemOptionInfos(source);
            loadItemNames(source);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Item.wz", e);
        }
    }

    public static List<ItemInfo> getItemInfos() {
        return itemInfos.values().stream().toList();
    }

    public static Optional<ItemInfo> getItemInfo(int itemId) {
        return Optional.ofNullable(itemInfos.get(itemId));
    }

    public static Optional<ItemOptionLevelData> getItemOptionInfo(int itemOptionId, int optionLevel) {
        if (!itemOptionInfos.containsKey(itemOptionId)) {
            return Optional.empty();
        }
        return itemOptionInfos.get(itemOptionId).getLevelData(optionLevel);
    }

    public static List<ItemOptionInfo> getPossibleItemOptions(ItemInfo itemInfo, ItemGrade itemGrade) {
        final Optional<BodyPart> bodyPartResult = BodyPart.getByItemId(itemInfo.getItemId()).stream().findFirst();
        if (bodyPartResult.isEmpty()) {
            return List.of();
        }
        final BodyPart bodyPart = bodyPartResult.get();
        final int reqLevel = itemInfo.getReqLevel();
        final List<ItemOptionInfo> possibleItemOptions = new ArrayList<>();
        for (ItemOptionInfo itemOptionInfo : itemOptionInfos.values()) {
            // Skip special options
            if (ItemOption.isSpecialOption(itemOptionInfo.getItemOptionId())) {
                continue;
            }
            // Check if option matches target item
            if (itemOptionInfo.isMatchingGrade(itemGrade) && itemOptionInfo.isMatchingLevel(reqLevel) && itemOptionInfo.isMatchingType(bodyPart)) {
                possibleItemOptions.add(itemOptionInfo);
            }
        }
        return possibleItemOptions;
    }

    public static Optional<ItemRewardInfo> getItemRewardInfo(int itemId) {
        return Optional.of(itemRewardInfos.get(itemId));
    }

    public static Optional<MobSummonInfo> getMobSummonInfo(int itemId) {
        return Optional.of(mobSummonInfos.get(itemId));
    }

    public static boolean isPetEquipSuitable(int itemId, int templateId) {
        return petEquips.getOrDefault(itemId, Set.of()).contains(templateId);
    }

    public static Optional<PetInteraction> getPetInteraction(int templateId, int action) {
        return Optional.ofNullable(petActions.getOrDefault(templateId, Map.of()).get(action));
    }

    public static Optional<String> getSpecialItemName(int itemId) {
        return Optional.ofNullable(specialItemNames.get(itemId));
    }

    private static void loadEquipInfos(WzPackage source) throws ProviderError, IOException {
        for (String directoryName : EQUIP_TYPES) {
            final WzDirectory directory = (WzDirectory) source.getItem(directoryName);
            if (directory == null) {
                throw new ProviderError("Could not resolve Character.wz/%s", directoryName);
            }
            for (var entry : directory.getImages().entrySet()) {
                final int itemId = Integer.parseInt(entry.getKey().replace(".img", ""));
                if (!(entry.getValue().getProperty() instanceof WzProperty itemProp)) {
                    throw new ProviderError("Failed to resolve item property");
                }
                itemInfos.put(itemId, ItemInfo.from(itemId, itemProp));
                // Pet equips
                if (!ItemConstants.isPetEquipItem(itemId)) {
                    continue;
                }
                final Set<Integer> suitablePets = new HashSet<>();
                for (var petEntry : entry.getValue().getProperty().getItems().entrySet()) {
                    if (!Util.isInteger(petEntry.getKey())) {
                        continue;
                    }
                    final int petTemplateId = Integer.parseInt(petEntry.getKey());
                    suitablePets.add(petTemplateId);
                }
                if (!suitablePets.isEmpty()) {
                    petEquips.put(itemId, Collections.unmodifiableSet(suitablePets));
                }
            }
        }
    }

    private static void loadItemInfos(WzPackage source) throws ProviderError, IOException {
        for (String directoryName : ITEM_TYPES) {
            final WzDirectory directory = (WzDirectory) source.getItem(directoryName);
            if (directory == null) {
                throw new ProviderError("Could not resolve Item.wz/%s", directoryName);
            }
            for (var image : directory.getImages().values()) {
                for (var entry : image.getProperty().getItems().entrySet()) {
                    final int itemId = Integer.parseInt(entry.getKey());
                    if (!(entry.getValue() instanceof WzProperty itemProp)) {
                        throw new ProviderError("Failed to resolve item property");
                    }
                    itemInfos.put(itemId, ItemInfo.from(itemId, itemProp));
                    // Item reward info
                    if (itemProp.get("reward") instanceof WzProperty rewardList) {
                        itemRewardInfos.put(itemId, ItemRewardInfo.from(itemId, rewardList));
                    }
                    // Mob summon info
                    if (itemProp.get("mob") instanceof WzProperty mobSummonList) {
                        mobSummonInfos.put(itemId, MobSummonInfo.from(itemId, mobSummonList));
                    }
                }
            }
        }
        if (!(source.getItem("Pet") instanceof WzDirectory petDirectory)) {
            throw new ProviderError("Could not resolve Item.wz/Pet");
        }
        for (var imageEntry : petDirectory.getImages().entrySet()) {
            final int itemId = Integer.parseInt(imageEntry.getKey().replace(".img", ""));
            itemInfos.put(itemId, ItemInfo.from(itemId, imageEntry.getValue().getProperty()));
            // Pet interactions
            if (!(imageEntry.getValue().getItem("interact") instanceof WzProperty interactList)) {
                continue;
            }
            final Map<Integer, PetInteraction> actions = new HashMap<>();
            for (var interactionEntry : interactList.getItems().entrySet()) {
                final int action = WzProvider.getInteger(interactionEntry.getKey());
                if (!(interactionEntry.getValue() instanceof WzProperty interactProp)) {
                    throw new ProviderError("Failed to resolve pet interact prop");
                }
                final PetInteraction interaction = PetInteraction.from(interactProp);
                actions.put(action, interaction);
            }
            petActions.put(itemId, Collections.unmodifiableMap(actions));
        }
    }

    private static void loadItemOptionInfos(WzPackage source) throws ProviderError {
        if (!((WzImage) source.getItem("ItemOption.img") instanceof WzImage itemOptionImage)) {
            throw new ProviderError("Could not resolve Item.wz/ItemOption.img");
        }
        for (var entry : itemOptionImage.getItems().entrySet()) {
            final int itemOptionId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzProperty itemOptionProp)) {
                throw new ProviderError("Failed to resolve item option prop");
            }
            itemOptionInfos.put(itemOptionId, ItemOptionInfo.from(itemOptionId, itemOptionProp));
        }
    }

    private static void loadItemNames(WzPackage source) throws ProviderError {
        final WzDirectory special = (WzDirectory) source.getItem("Special");
        loadItemNames((WzImage) special.getItem("0910.img"));
        loadItemNames((WzImage) special.getItem("0911.img"));
    }

    private static void loadItemNames(WzImage image) throws ProviderError {
        for (var entry : image.getItems().entrySet()) {
            final int itemId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzProperty itemNameProp)) {
                throw new ProviderError("Failed to resolve item name prop");
            }
            final String itemName = WzProvider.getString(itemNameProp.get("name"));
            specialItemNames.put(itemId, itemName);
        }
    }
}
