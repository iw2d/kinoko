package kinoko.world.item;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.user.DBChar;

import java.util.ArrayList;
import java.util.List;

public final class Trunk implements Encodable {
    public static final List<InventoryType> INVENTORY_TYPES = List.of(
            InventoryType.EQUIP,
            InventoryType.CONSUME,
            InventoryType.INSTALL,
            InventoryType.ETC,
            InventoryType.CASH
    );

    private final List<Item> items = new ArrayList<>();
    private int size;
    private int money;

    public Trunk(int size) {
        this.size = size;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getRemaining() {
        return Math.max(size - items.size(), 0);
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean canAddMoney(int money) {
        final long newMoney = ((long) getMoney()) + money;
        return newMoney <= Integer.MAX_VALUE && newMoney >= 0;
    }

    public boolean addMoney(int money) {
        final long newMoney = ((long) getMoney()) + money;
        if (newMoney > Integer.MAX_VALUE || newMoney < 0) {
            return false;
        }
        setMoney((int) newMoney);
        return true;
    }

    public void encodeItems(DBChar flag, OutPacket outPacket) {
        // CTrunkDlg::SetGetItems
        outPacket.encodeByte(size); // nSlotCount
        outPacket.encodeLong(flag.getValue()); // dbcharFlag
        if (flag.hasFlag(DBChar.MONEY)) {
            outPacket.encodeInt(money); // nMoney
        }
        for (InventoryType inventoryType : INVENTORY_TYPES) {
            if (flag.hasFlag(inventoryType.getFlag())) {
                final List<Item> filteredItems = items.stream()
                        .filter((item) -> InventoryType.getByItemId(item.getItemId()) == inventoryType)
                        .toList();
                outPacket.encodeByte(filteredItems.size()); // nCount
                for (Item item : filteredItems) {
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
