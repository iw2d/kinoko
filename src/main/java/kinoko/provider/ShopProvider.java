package kinoko.provider;

import kinoko.world.dialog.shop.ShopItem;
import kinoko.world.life.npc.Npc;

import java.util.List;

public final class ShopProvider implements DataProvider {
    public static List<ShopItem> getNpcShopItems(Npc npc) {
        return List.of();
    }

    public static void initialize() {

    }
}
