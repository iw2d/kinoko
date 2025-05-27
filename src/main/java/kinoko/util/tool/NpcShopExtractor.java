package kinoko.util.tool;

import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.wz.WzArchive;
import kinoko.provider.wz.WzCrypto;
import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.server.ServerConfig;
import kinoko.server.dialog.shop.ShopItem;
import kinoko.world.item.ItemConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

final class NpcShopExtractor {
    public static final Path NPC_SHOP_IMG = Path.of(ServerConfig.WZ_DIRECTORY, "bms", "NpcShop.img");
    private static final List<ShopItem> subiAndBullet = List.of(
            ShopItem.from(2070000, 500, 500, 500), // Subi Throwing-Stars
            ShopItem.from(2330000, 500, 500, 500) // Bullet
            // TODO magnifying glass?
    );

    public static void main(String[] args) throws IOException {
        ItemProvider.initialize();
        MapProvider.initialize();
        StringProvider.initialize();

        // Extract npc shop image
        WzCrypto.setCipher(null);
        try (final WzArchive archive = WzArchive.from(NPC_SHOP_IMG)) {
            final WzImage npcShopImage = archive.getImage();
            final Map<Integer, List<ShopItem>> npcShopItems = new HashMap<>();
            for (var entry : npcShopImage.getItems().entrySet()) {
                final int npcId = WzProvider.getInteger(entry.getKey());
                if (!(entry.getValue() instanceof WzProperty itemList)) {
                    throw new ProviderError("Failed to resolve shop item list");
                }
                final List<ShopItem> items = new ArrayList<>();
                for (var itemEntry : itemList.getItems().entrySet()) {
                    if (!(itemEntry.getValue() instanceof WzProperty itemProp)) {
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
                    items.add(new ShopItem(itemId, price, 1, ii.getSlotMax(), 0, 0, unitPrice));
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
                final List<ShopItem> shopItems = entry.getValue();
                final List<String> npcFields = MapProvider.getMapInfos().stream()
                        .filter((mapInfo) -> mapInfo.getLifeInfos().stream().anyMatch((lifeInfo) -> lifeInfo.getTemplateId() == npcId))
                        .map((mapInfo) -> String.format("%s (%d)", StringProvider.getMapName(mapInfo.getMapId()), mapInfo.getMapId()))
                        .toList();

                final boolean recharge = shopItems.stream().anyMatch((si) -> si.getUnitPrice() > 0);
                final boolean hasSubi = shopItems.stream().anyMatch((si) -> si.getItemId() == 2070000 && si.getPrice() > 0);
                if (hasSubi) {
                    shopItems.addAll(subiAndBullet);
                }

                final Path filePath = Path.of(ShopProvider.SHOP_DATA.toString(), String.format("%d.yaml", npcId));
                try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    bw.write(String.format("# %s (%d) - %s\n\n", StringProvider.getNpcName(npcId), npcId, String.join(", ", npcFields)));
                    if (recharge) {
                        bw.write("recharge: true\n");
                    }
                    bw.write("items:\n");
                    for (ShopItem si : shopItems.stream()
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
                        bw.write(String.format("%s # %s\n", line, StringProvider.getItemName(si.getItemId())));
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
    }
}
