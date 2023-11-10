package kinoko.provider;

import kinoko.provider.item.ItemInfo;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemProvider {
    private static final List<String> EQUIP_TYPES = List.of("Accessory", "Cap", "Cape", "Coat", "Dragon", "Face", "Glove", "Hair", "Longcoat", "Mechanic", "Pants", "PetEquip", "Ring", "Shield", "Shoes", "TamingMob", "Weapon");
    private static final List<String> ITEM_TYPES = List.of("Consume", "Install", "Etc", "Cash");

    private static final Map<Integer, ItemInfo> itemInfos = new HashMap<>();

    public static void initialize() {
        // Character.wz
        final File characterFile = Path.of(ServerConfig.WZ_DIRECTORY, "Character.wz").toFile();
        try (final WzReader reader = WzReader.build(characterFile, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadEquipInfos(wzPackage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Item.wz
        final File itemFile = Path.of(ServerConfig.WZ_DIRECTORY, "Item.wz").toFile();
        try (final WzReader reader = WzReader.build(itemFile, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadItemInfos(wzPackage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadEquipInfos(WzPackage source) throws ProviderError {
        for (String directoryName : EQUIP_TYPES) {
            final WzDirectory directory = source.getDirectory().getDirectories().get(directoryName);
            if (directory == null) {
                throw new ProviderError("Could not resolve Character.wz/{}", directoryName);
            }
            for (var entry : directory.getImages().entrySet()) {
                final int itemId = Integer.parseInt(entry.getKey().replace(".img", ""));
                itemInfos.put(itemId, ItemInfo.from(itemId, entry.getValue().getProperty()));
            }
        }
    }

    private static void loadItemInfos(WzPackage source) throws ProviderError {
        for (String directoryName : ITEM_TYPES) {
            final WzDirectory directory = source.getDirectory().getDirectories().get(directoryName);
            if (directory == null) {
                throw new ProviderError("Could not resolve Item.wz/{}", directoryName);
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
