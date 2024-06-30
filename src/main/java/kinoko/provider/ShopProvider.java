package kinoko.provider;

import kinoko.server.ServerConfig;
import kinoko.world.dialog.shop.ShopItem;
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
            shopItems.add(ShopItem.from(itemId, price, quantity, maxPerSlot));
        }
        if (shopData.containsKey("recharge") && shopData.get("recharge").equals(true)) {
            shopItems.addAll(rechargeableItems);
        }

        npcShopItems.put(npcId, Collections.unmodifiableList(shopItems));
    }

    private static List<ShopItem> initializeRechargeableItems() {
        return Arrays.asList(
                ShopItem.rechargeable(2070000, 500, 1.0), // Subi Throwing-Stars
                ShopItem.rechargeable(2070001, 500, 1.0), // Wolbi Throwing-Stars
                ShopItem.rechargeable(2070002, 700, 1.0), // Mokbi Throwing-Stars
                ShopItem.rechargeable(2070003, 500, 1.0), // Kumbi Throwing-Stars
                ShopItem.rechargeable(2070004, 1000, 1.0), // Tobi Throwing-Stars
                ShopItem.rechargeable(2070005, 1000, 1.0), // Steely Throwing-Knives
                ShopItem.rechargeable(2070006, 800, 1.0), // Ilbi Throwing-Stars
                ShopItem.rechargeable(2070007, 1000, 1.0), // Hwabi Throwing-Stars
                ShopItem.rechargeable(2070008, 800, 1.0), // Snowball
                ShopItem.rechargeable(2070009, 800, 1.0), // Wooden Top
                ShopItem.rechargeable(2070010, 800, 1.0), // Icicle
                ShopItem.rechargeable(2070011, 800, 1.0), // Maple Throwing-Stars
                ShopItem.rechargeable(2070012, 1000, 1.0), // Paper Fighter Plane
                ShopItem.rechargeable(2070013, 1000, 1.0), // Orange
                // ShopItem.rechargeable(2070015, 1000, 1.0), // A Beginner Thief's Throwing Stars
                ShopItem.rechargeable(2070016, 800, 1.0), // Crystal Ilbi Throwing-Stars
                ShopItem.rechargeable(2070018, 800, 1.0), // Balanced Fury
                ShopItem.rechargeable(2330000, 800, 1.0), // Bullet
                ShopItem.rechargeable(2330001, 1200, 1.0), // Split Bullet
                ShopItem.rechargeable(2330002, 1600, 1.0), // Mighty Bullet
                ShopItem.rechargeable(2330003, 2200, 1.0), // Vital Bullet
                ShopItem.rechargeable(2330004, 2600, 1.0), // Shiny Bullet
                ShopItem.rechargeable(2330005, 3000, 1.0), // Eternal Bullet
                // ShopItem.rechargeable(2330006, 600, 1.0), // Bullet for Novice Pirates
                ShopItem.rechargeable(2331000, 800, 1.0), // Blaze Capsule
                ShopItem.rechargeable(2332000, 800, 1.0) // Glaze Capsule
        );
    }
}
