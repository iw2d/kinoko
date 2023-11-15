package kinoko.provider;

import io.fury.Fury;
import io.fury.ThreadLocalFury;
import io.fury.ThreadSafeFury;
import io.fury.config.Language;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.item.ItemSpecType;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.Server;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class ItemProvider {
    public static final Path CHARACTER_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Character.wz");
    public static final Path ITEM_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Item.wz");
    public static final Path ITEM_DIRECTORY = Path.of(ServerConfig.DAT_DIRECTORY, "item");
    public static final List<String> EQUIP_TYPES = List.of("Accessory", "Cap", "Cape", "Coat", "Dragon", "Face", "Glove", "Hair", "Longcoat", "Mechanic", "Pants", "PetEquip", "Ring", "Shield", "Shoes", "TamingMob", "Weapon");
    public static final List<String> ITEM_TYPES = List.of("Consume", "Install", "Etc", "Cash");
    private static final Logger log = LogManager.getLogger(ItemProvider.class);
    private static final ThreadSafeFury fury = new ThreadLocalFury(classLoader -> {
        final Fury f = Fury.builder().withLanguage(Language.JAVA)
                .withClassLoader(classLoader)
                .build();
        f.register(ItemInfo.class);
        f.register(ItemInfoType.class);
        f.register(ItemSpecType.class);
        return f;
    });

    public static void initialize(boolean reset) {
        if (!Files.isDirectory(ITEM_DIRECTORY) || reset) {
            try {
                if (Files.isDirectory(ITEM_DIRECTORY)) {
                    try (final Stream<Path> walk = Files.walk(ITEM_DIRECTORY)) {
                        walk.sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::delete);
                    }
                }
                Files.deleteIfExists(ITEM_DIRECTORY);
                Files.createDirectories(ITEM_DIRECTORY);
                // Character.wz
                try (final WzReader reader = WzReader.build(CHARACTER_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
                    final WzPackage wzPackage = reader.readPackage();
                    loadEquipInfos(wzPackage);
                } catch (ProviderError e) {
                    log.error("Exception caught while loading Character.wz", e);
                }
                // Item.wz
                try (final WzReader reader = WzReader.build(ITEM_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
                    final WzPackage wzPackage = reader.readPackage();
                    loadItemInfos(wzPackage);
                } catch (ProviderError e) {
                    log.error("Exception caught while loading Item.wz", e);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static Optional<ItemInfo> getItemInfo(int itemId) {
        try {
            final byte[] data = Files.readAllBytes(getPath(itemId));
            return Optional.of(fury.deserializeJavaObject(data, ItemInfo.class));
        } catch (IOException e) {
            log.error("Exception caught while loading ItemInfo for item ID : {}", itemId, e);
        }
        return Optional.empty();
    }

    private static Path getPath(int itemId) {
        return Path.of(ITEM_DIRECTORY.toString(), String.format("%d.dat", itemId));
    }

    private static void loadEquipInfos(WzPackage source) throws ProviderError, IOException {
        for (String directoryName : EQUIP_TYPES) {
            final WzDirectory directory = source.getDirectory().getDirectories().get(directoryName);
            if (directory == null) {
                throw new ProviderError("Could not resolve Character.wz/{}", directoryName);
            }
            for (var entry : directory.getImages().entrySet()) {
                final int itemId = Integer.parseInt(entry.getKey().replace(".img", ""));
                final ItemInfo itemInfo = ItemInfo.from(itemId, entry.getValue().getProperty());

                final byte[] data = fury.serializeJavaObject(itemInfo);
                Files.write(getPath(itemId), data);
            }
        }
    }

    private static void loadItemInfos(WzPackage source) throws ProviderError, IOException {
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
                    final ItemInfo itemInfo = ItemInfo.from(itemId, itemProp);

                    final byte[] data = fury.serializeJavaObject(itemInfo);
                    Files.write(getPath(itemId), data);
                }
            }
        }
    }
}
