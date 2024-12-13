package kinoko.provider;

import kinoko.provider.item.*;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.util.Util;
import kinoko.world.item.BodyPart;
import kinoko.world.item.EquipData;
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
    private static final Map<Integer, Map<String, Byte>> itemMakeStatsCache = new HashMap<Integer, Map<String, Byte>>();
    private static final Map<Integer, ItemOptionInfo> itemOptionInfos = new HashMap<>(); // item option id -> item option info
    private static final Map<Integer, Set<Integer>> petEquips = new HashMap<>(); // petEquipId -> set<petTemplateId>
    private static final Map<Integer, Map<Integer, PetInteraction>> petActions = new HashMap<>(); // petTemplateId -> (action -> PetInteraction)
    private static final Map<Integer, String> specialItemNames = new HashMap<>();

    public static void initialize() {
        // Character.wz
        try (final WzReader reader = WzReader.build(CHARACTER_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadEquipInfos(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Character.wz", e);
        }
        // Item.wz
        try (final WzReader reader = WzReader.build(ITEM_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadItemInfos(wzPackage);
            loadItemOptionInfos(wzPackage);
            loadItemNames(wzPackage);
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
            final WzDirectory directory = source.getDirectory().getDirectories().get(directoryName);
            if (directory == null) {
                throw new ProviderError("Could not resolve Character.wz/%s", directoryName);
            }
            for (var entry : directory.getImages().entrySet()) {
                final int itemId = Integer.parseInt(entry.getKey().replace(".img", ""));
                if (!(entry.getValue().getProperty() instanceof WzListProperty itemProp)) {
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
            final WzDirectory directory = source.getDirectory().getDirectories().get(directoryName);
            if (directory == null) {
                throw new ProviderError("Could not resolve Item.wz/%s", directoryName);
            }
            for (var image : directory.getImages().values()) {
                for (var entry : image.getProperty().getItems().entrySet()) {
                    final int itemId = Integer.parseInt(entry.getKey());
                    if (!(entry.getValue() instanceof WzListProperty itemProp)) {
                        throw new ProviderError("Failed to resolve item property");
                    }
                    itemInfos.put(itemId, ItemInfo.from(itemId, itemProp));
                }
            }
        }
        if (!(source.getDirectory().getDirectories().get("Pet") instanceof WzDirectory petDirectory)) {
            throw new ProviderError("Could not resolve Item.wz/Pet");
        }
        for (var imageEntry : petDirectory.getImages().entrySet()) {
            final int itemId = Integer.parseInt(imageEntry.getKey().replace(".img", ""));
            itemInfos.put(itemId, ItemInfo.from(itemId, imageEntry.getValue().getProperty()));
            // Pet interactions
            if (!(imageEntry.getValue().getProperty().get("interact") instanceof WzListProperty interactList)) {
                continue;
            }
            final Map<Integer, PetInteraction> actions = new HashMap<>();
            for (var interactionEntry : interactList.getItems().entrySet()) {
                final int action = WzProvider.getInteger(interactionEntry.getKey());
                if (!(interactionEntry.getValue() instanceof WzListProperty interactProp)) {
                    throw new ProviderError("Failed to resolve pet interact prop");
                }
                final PetInteraction interaction = PetInteraction.from(interactProp);
                actions.put(action, interaction);
            }
            petActions.put(itemId, Collections.unmodifiableMap(actions));
        }
    }

    private static void loadItemOptionInfos(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("ItemOption.img") instanceof WzImage itemOptionImage)) {
            throw new ProviderError("Could not resolve Item.wz/ItemOption.img");
        }
        for (var entry : itemOptionImage.getProperty().getItems().entrySet()) {
            final int itemOptionId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty itemOptionProp)) {
                throw new ProviderError("Failed to resolve item option prop");
            }
            itemOptionInfos.put(itemOptionId, ItemOptionInfo.from(itemOptionId, itemOptionProp));
        }
    }

    private static void loadItemNames(WzPackage source) throws ProviderError {
        final WzDirectory special = source.getDirectory().getDirectories().get("Special");
        loadItemNames(special.getImages().get("0910.img"));
        loadItemNames(special.getImages().get("0911.img"));
    }

    private static void loadItemNames(WzImage image) throws ProviderError {
        for (var entry : image.getProperty().getItems().entrySet()) {
            final int itemId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty itemNameProp)) {
                throw new ProviderError("Failed to resolve item name prop");
            }
            final String itemName = WzProvider.getString(itemNameProp.get("name"));
            specialItemNames.put(itemId, itemName);
        }
    }

    public static Map<String, Byte> getItemMakeStats(final int itemId) {
        // Check if stats are already cached
        if (itemMakeStatsCache.containsKey(itemId)) {
            return itemMakeStatsCache.get(itemId);
        }

        // Ensure the item ID belongs to the desired category
        if (itemId / 10000 != 425) {
            return null;
        }

        // Retrieve item information
        Optional<ItemInfo> itemInfoOpt = ItemProvider.getItemInfo(itemId);
        if (itemInfoOpt.isEmpty()) {
            return null;
        }

        // Convert ItemInfo to EquipData
        EquipData equipData = EquipData.from(itemInfoOpt.get());

        // Extract stats from EquipData
        final Map<String, Byte> ret = new LinkedHashMap<>();
        ret.put("incPAD", (byte) equipData.getIncPad()); // Physical attack
        ret.put("incMAD", (byte) equipData.getIncMad()); // Magic attack
        ret.put("incACC", (byte) equipData.getIncAcc()); // Accuracy
        ret.put("incEVA", (byte) equipData.getIncEva()); // Evasion
        ret.put("incSpeed", (byte) equipData.getIncSpeed()); // Speed
        ret.put("incJump", (byte) equipData.getIncJump()); // Jump
        ret.put("incMaxHP", (byte) equipData.getIncMaxHp()); // Max HP
        ret.put("incMaxMP", (byte) equipData.getIncMaxMp()); // Max MP
        ret.put("incSTR", (byte) equipData.getIncStr()); // Strength
        ret.put("incINT", (byte) equipData.getIncInt()); // Intelligence
        ret.put("incLUK", (byte) equipData.getIncLuk()); // Luck
        ret.put("incDEX", (byte) equipData.getIncDex()); // Dexterity

        // Optional fields //TODO: Not sure if this needs added in v95
        /*if (itemInfoOpt.get().getItemInfos().containsKey(ItemInfoType.randoption)) {
            ret.put("randOption", (byte) itemInfoOpt.get().getInfo(ItemInfoType.));
        }*/

        if (itemInfoOpt.get().getItemInfos().containsKey(ItemInfoType.randstat)) {
            ret.put("randStat", (byte) itemInfoOpt.get().getInfo(ItemInfoType.randstat));
        }

        // Cache the result for future use
        itemMakeStatsCache.put(itemId, ret);

        return ret;
    }
}
