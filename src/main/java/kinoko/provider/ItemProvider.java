package kinoko.provider;

import kinoko.provider.item.ItemInfo;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ItemProvider implements WzProvider {
    public static final Path CHARACTER_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Character.wz");
    public static final Path ITEM_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Item.wz");
    public static final List<String> EQUIP_TYPES = List.of("Accessory", "Cap", "Cape", "Coat", "Dragon", "Face", "Glove", "Hair", "Longcoat", "Mechanic", "Pants", "PetEquip", "Ring", "Shield", "Shoes", "TamingMob", "Weapon");
    public static final List<String> ITEM_TYPES = List.of("Consume", "Install", "Etc", "Cash");
    private static final Map<Integer, ItemInfo> itemInfos = new HashMap<>();

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
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Item.wz", e);
        }
    }

    public static Optional<ItemInfo> getItemInfo(int itemId) {
        if (!itemInfos.containsKey(itemId)) {
            return Optional.empty();
        }
        return Optional.of(itemInfos.get(itemId));
    }

    private static void loadEquipInfos(WzPackage source) throws ProviderError, IOException {
        for (String directoryName : EQUIP_TYPES) {
            final WzDirectory directory = source.getDirectory().getDirectories().get(directoryName);
            if (directory == null) {
                throw new ProviderError("Could not resolve Character.wz/%s", directoryName);
            }
            for (var entry : directory.getImages().entrySet()) {
                final int itemId = Integer.parseInt(entry.getKey().replace(".img", ""));
                itemInfos.put(itemId, ItemInfo.from(itemId, entry.getValue().getProperty()));
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
    }
}
