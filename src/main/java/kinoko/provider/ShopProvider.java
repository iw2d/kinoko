package kinoko.provider;

import kinoko.server.ServerConfig;
import kinoko.server.dialog.shop.ShopItem;
import kinoko.world.life.npc.Npc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class ShopProvider implements DataProvider {
    public static final Path NPC_SHOP = Path.of(ServerConfig.DATA_DIRECTORY, "npc_shop.csv");
    private static final Map<Integer, List<ShopItem>> npcShopItems = new HashMap<>(); // npcId -> shop items

    public static void initialize() {
        try {
            DataProvider.readData(NPC_SHOP).forEach((props) -> {
                final int npcId = Integer.parseInt(props.get(0));
                final ShopItem shopItem = getShopItem(props);
                if (!npcShopItems.containsKey(npcId)) {
                    npcShopItems.put(npcId, new ArrayList<>());
                }
                npcShopItems.get(npcId).add(shopItem);
            });
            for (var entry : npcShopItems.entrySet()) {
                npcShopItems.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception caught while loading Reward Data", e);
        }
    }

    public static List<ShopItem> getNpcShopItems(Npc npc) {
        if (!npcShopItems.containsKey(npc.getTemplateId())) {
            return List.of();
        }
        return npcShopItems.get(npc.getTemplateId());
    }

    private static ShopItem getShopItem(List<String> props) {
        return new ShopItem(
                Integer.parseInt(props.get(1)),
                Integer.parseInt(props.get(2)),
                Integer.parseInt(props.get(3)),
                Integer.parseInt(props.get(4)),
                Integer.parseInt(props.get(5)),
                Integer.parseInt(props.get(6)),
                Double.parseDouble(props.get(7))
        );
    }
}
