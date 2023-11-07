package kinoko.world.user;

import kinoko.world.item.Inventory;
import lombok.Data;

@Data
public final class CharacterInventory {
    private Inventory equipped;
    private Inventory equipInventory;
    private Inventory consumeInventory;
    private Inventory installInventory;
    private Inventory etcInventory;
    private Inventory cashInventory;
    private int money;
}
