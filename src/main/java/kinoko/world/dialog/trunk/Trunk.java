package kinoko.world.dialog.trunk;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.user.DBChar;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class Trunk implements Encodable {
    public static final List<InventoryType> INVENTORY_TYPES = List.of(
            InventoryType.EQUIP,
            InventoryType.CONSUME,
            InventoryType.INSTALL,
            InventoryType.ETC,
            InventoryType.CASH
    );

    private Inventory inventory;
    private int money;

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void encodeItems(DBChar flag, OutPacket outPacket) {
        // CTrunkDlg::SetGetItems
        outPacket.encodeByte(inventory.getSize()); // nSlotCount
        outPacket.encodeLong(flag.getValue()); // dbcharFlag
        if (flag.hasFlag(DBChar.MONEY)) {
            outPacket.encodeInt(money); // nMoney
        }
        for (InventoryType inventoryType : INVENTORY_TYPES) {
            if (flag.hasFlag(inventoryType.getFlag())) {
                final Set<Item> items = inventory.getItems().values().stream()
                        .filter((item) -> InventoryType.getByItemId(item.getItemId()) == inventoryType)
                        .collect(Collectors.toUnmodifiableSet());
                outPacket.encodeByte(items.size()); // nCount
                for (Item item : items) {
                    item.encode(outPacket); // GW_ItemSlotBase::Decode
                }
            }
        }
    }

    @Override
    public void encode(OutPacket outPacket) {
        encodeItems(DBChar.ALL, outPacket);
    }
}
