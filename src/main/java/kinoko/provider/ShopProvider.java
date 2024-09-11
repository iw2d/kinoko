package kinoko.provider;

import kinoko.provider.item.ItemInfo;
import kinoko.server.ServerConfig;
import kinoko.server.dialog.shop.ShopItem;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class ShopProvider implements DataProvider {
    public static final Path SHOP_DATA = Path.of(ServerConfig.DATA_DIRECTORY, "shop");
    private static final Map<Integer, List<ShopItem>> npcShopItems = new HashMap<>(); // npcId -> shop items
    private static final List<ShopItem> rechargeableItems = initializeRechargeableItems();

    public static void initialize() {
        final Load yamlLoader = new Load(LoadSettings.builder().build());
        try (final Stream<Path> paths = Files.list(SHOP_DATA)) {
            for (Path path : paths.toList()) {
                final String fileName = path.getFileName().toString();
                if (!fileName.endsWith(".yaml")) {
                    continue;
                }
                final int npcId = Integer.parseInt(fileName.replace(".yaml", ""));
                try (final InputStream is = Files.newInputStream(path)) {
                    loadNpcShopItems(npcId, yamlLoader.loadFromInputStream(is));
                }
            }
        } catch (IOException e) {
            throw new ProviderError("Exception caught while loading Shop Data", e);
        }
    }

    public static boolean isShop(int templateId) {
        return npcShopItems.containsKey(templateId);
    }

    public static List<ShopItem> getNpcShopItems(int templateId) {
        if (!npcShopItems.containsKey(templateId)) {
            return List.of();
        }
        return npcShopItems.get(templateId);
    }

    private static void loadNpcShopItems(int npcId, Object yamlObject) throws ProviderError {
        if (!(yamlObject instanceof Map<?, ?> shopData)) {
            throw new ProviderError("Could not resolve shop data for npc ID : %d", npcId);
        }
        if (!(shopData.get("items") instanceof List<?> itemList)) {
            throw new ProviderError("Could not resolve shop items for npc ID : %d", npcId);
        }
        final List<ShopItem> shopItems = new ArrayList<>();
        for (Object itemObject : itemList) {
            if (!(itemObject instanceof List<?> itemInfo)) {
                throw new ProviderError("Could not resolve shop item info for npc ID : %d", npcId);
            }
            final int itemId = ((Number) itemInfo.get(0)).intValue();
            final int price = ((Number) itemInfo.get(1)).intValue();
            final int quantity = itemInfo.size() > 2 ? ((Number) itemInfo.get(2)).intValue() : 1;
            final int maxPerSlot = itemInfo.size() > 3 ? ((Number) itemInfo.get(3)).intValue() : 1;
            double unitPrice = 0.0;
            for (ShopItem si : rechargeableItems) {
                if (si.getItemId() == itemId) {
                    unitPrice = si.getUnitPrice();
                    break;
                }
            }
            shopItems.add(new ShopItem(itemId, price, quantity, maxPerSlot, 0, 0, unitPrice));
        }
        if (shopData.containsKey("recharge") && shopData.get("recharge").equals(true)) {
            for (ShopItem rechargeableItem : rechargeableItems) {
                if (shopItems.stream().noneMatch((existingItem) -> existingItem.getItemId() == rechargeableItem.getItemId())) {
                    shopItems.add(rechargeableItem);
                }
            }
        }

        npcShopItems.put(npcId, Collections.unmodifiableList(shopItems));
    }

    private static List<ShopItem> initializeRechargeableItems() throws ProviderError {
        final List<Integer> itemIds = Arrays.asList(
                2070000, // Subi Throwing-Stars
                2070001, // Wolbi Throwing-Stars
                2070002, // Mokbi Throwing-Stars
                2070003, // Kumbi Throwing-Stars
                2070004, // Tobi Throwing-Stars
                2070005, // Steely Throwing-Knives
                2070006, // Ilbi Throwing-Stars
                2070007, // Hwabi Throwing-Stars
                2070008, // Snowball
                2070009, // Wooden Top
                2070010, // Icicle
                2070011, // Maple Throwing-Stars
                2070012, // Paper Fighter Plane
                2070013, // Orange
                // 2070015, // A Beginner Thief's Throwing Stars
                2070016, // Crystal Ilbi Throwing-Stars
                2070018, // Balanced Fury
                2330000, // Bullet
                2330001, // Split Bullet
                2330002, // Mighty Bullet
                2330003, // Vital Bullet
                2330004, // Shiny Bullet
                2330005, // Eternal Bullet
                // 2330006, // Bullet for Novice Pirates
                2331000, // Blaze Capsule
                2332000 // Glaze Capsule
        );
        final List<ShopItem> rechargeableItems = new ArrayList<>();
        for (int itemId : itemIds) {
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
            if (itemInfoResult.isEmpty()) {
                throw new ProviderError("Could not resolve rechargeable item ID : %d", itemId);
            }
            final ItemInfo ii = itemInfoResult.get();
            rechargeableItems.add(ShopItem.rechargeable(itemId, ii.getSlotMax(), ii.getUnitPrice()));
        }
        return Collections.unmodifiableList(rechargeableItems);
    }
}
