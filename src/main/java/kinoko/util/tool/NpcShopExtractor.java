package kinoko.util.tool;

import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.world.dialog.shop.ShopItem;
import kinoko.world.item.ItemConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

final class NpcShopExtractor {
    public static final Path NPC_SHOP_IMG = Path.of(ServerConfig.WZ_DIRECTORY, "bms", "NpcShop.img");

    public static void main(String[] args) throws IOException {
        ItemProvider.initialize();
        MapProvider.initialize();
        StringProvider.initialize();

        // Extract npc shop image
        final WzImage npcShopImage = readImage(NPC_SHOP_IMG);
        final Map<Integer, List<ShopItem>> npcShopItems = new HashMap<>();
        for (var entry : npcShopImage.getProperty().getItems().entrySet()) {
            final int npcId = WzProvider.getInteger(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty itemList)) {
                throw new ProviderError("Failed to resolve shop item list");
            }
            final List<ShopItem> items = new ArrayList<>();
            for (var itemEntry : itemList.getItems().entrySet()) {
                if (!(itemEntry.getValue() instanceof WzListProperty itemProp)) {
                    throw new ProviderError("Failed to resolve shop item prop");
                }
                int itemId = 0;
                int price = 0;
                double unitPrice = 0;
                for (var propEntry : itemProp.getItems().entrySet()) {
                    switch (propEntry.getKey()) {
                        case "item" -> {
                            itemId = WzProvider.getInteger(propEntry.getValue());
                        }
                        case "price" -> {
                            price = WzProvider.getInteger(propEntry.getValue());
                        }
                        case "unitPrice" -> {
                            final String unitPriceString = WzProvider.getString(propEntry.getValue());
                            assert unitPriceString.startsWith("[R8]");
                            unitPrice = Double.parseDouble(unitPriceString.replace("[R8]", ""));
                        }
                        case "period", "stock" -> {
                            // ignored
                        }
                        default -> {
                            System.err.printf("Unhandled shop item prop : %s", propEntry.getKey());
                        }
                    }
                }
                if (itemId <= 0) {
                    continue;
                }
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
                if (itemInfoResult.isEmpty()) {
                    System.err.printf("Could not resolve item info for item ID : %d", itemId);
                    continue;
                }
                final ItemInfo ii = itemInfoResult.get();
                if (unitPrice > 0) {
                    items.add(ShopItem.rechargeable(itemId, ii.getSlotMax(), unitPrice));
                } else if (price > 0) {
                    items.add(ShopItem.from(itemId, price, 1, ii.getSlotMax()));
                } else {
                    System.err.printf("Could not resolve price for item ID : %d", itemId);
                }
            }
            npcShopItems.put(npcId, items);
        }

//        // Write to CSV
//        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(ServerConfig.DATA_DIRECTORY, "npc_shop.csv"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
//            for (var entry : npcShopItems.entrySet().stream()
//                    .sorted(Comparator.comparingInt(Map.Entry::getKey)).toList()) {
//                final int npcId = entry.getKey();
//                bw.write(String.format("# %s%n", StringProvider.getNpcName(npcId)));
//                for (ShopItem si : entry.getValue().stream()
//                        .sorted(Comparator.comparingInt(ShopItem::getItemId)).toList()) {
//                    final String itemName = StringProvider.getItemName(si.getItemId());
//                    final String line = String.format("%d, %d, %d, %d, %d, %d, %d, %f",
//                            npcId,
//                            si.getItemId(),
//                            si.getPrice(),
//                            si.getQuantity(),
//                            si.getMaxPerSlot(),
//                            si.getTokenItemId(),
//                            si.getTokenPrice(),
//                            si.getUnitPrice()
//                    );
//                    bw.write(String.format("%-120s# %s%n", line, itemName));
//                }
//                bw.write("\n");
//            }
//        }

        // Create YAML
        for (var entry : npcShopItems.entrySet()) {
            final int npcId = entry.getKey();
            final List<String> npcFields = MapProvider.getMapInfos().stream()
                    .filter((mapInfo) -> mapInfo.getLifeInfos().stream().anyMatch((lifeInfo) -> lifeInfo.getTemplateId() == npcId))
                    .map((mapInfo) -> String.format("%s (%d)", StringProvider.getMapName(mapInfo.getMapId()), mapInfo.getMapId()))
                    .toList();

            final Path filePath = Path.of(ShopProvider.SHOP_DATA.toString(), String.format("%d.yaml", npcId));
            try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                bw.write(String.format("# %s (%d) - %s\n\n", StringProvider.getNpcName(npcId), npcId, String.join(", ", npcFields)));
                if (entry.getValue().stream().anyMatch((si) -> si.getUnitPrice() > 0)) {
                    bw.write("recharge: true\n");
                }
                bw.write("items:\n");
                for (ShopItem si : entry.getValue().stream()
                        .sorted(Comparator.comparingInt(ShopItem::getItemId)).toList()) {
                    if (si.getUnitPrice() > 0) {
                        continue;
                    }
                    final String itemName = StringProvider.getItemName(si.getItemId());
                    final String line;
                    if (si.getQuantity() > 1 || si.getMaxPerSlot() > 1) {
                        line = String.format("  - [ %d, %d, %d, %d ]",
                                si.getItemId(),
                                si.getPrice(),
                                si.getQuantity(),
                                si.getMaxPerSlot()
                        );
                    } else {
                        line = String.format("  - [ %d, %d ]",
                                si.getItemId(),
                                si.getPrice()
                        );
                    }
                    bw.write(String.format("%s # %s\n", line, itemName));
                }
            }
        }

        // Rechargeable Items
        for (var entry : StringProvider.getItemNames().entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .toList()) {
            final int itemId = entry.getKey();
            final String itemName = entry.getValue();
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
            if (itemInfoResult.isEmpty() || !ItemConstants.isRechargeableItem(itemId)) {
                continue;
            }
            // System.out.printf("ShopItem.rechargeable(%d, %d, 1.0), // %s\n", itemId, itemInfoResult.get().getSlotMax(), itemName);
        }
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
