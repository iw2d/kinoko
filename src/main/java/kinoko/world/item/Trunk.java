package kinoko.world.item;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
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


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public boolean canAddItem(Item item, int quantity) {
        if (item.getItemType() != ItemType.BUNDLE || ItemConstants.isRechargeableItem(item.getItemId())) {
            return getRemaining() > 0;
        }
        // Check if item can be merged into existing stacks
        int count = quantity;
        final int slotMax = ItemProvider.getItemInfo(item.getItemId()).map(ItemInfo::getSlotMax).orElse(0);
        for (Item existingItem : items) {
            if (existingItem.getItemId() != item.getItemId()) {
                continue;
            }
            if (existingItem.getQuantity() >= slotMax) {
                continue;
            }
            final int newQuantity = Math.min(existingItem.getQuantity() + count, slotMax);
            final int delta = newQuantity - existingItem.getQuantity();
            count -= delta;
            if (count == 0) {
                break;
            }
        }
        final int remainingStacks = Math.ceilDiv(count, slotMax);
        return getRemaining() >= remainingStacks;
    }

    public void addItem(Item item) {
        if (item.getItemType() != ItemType.BUNDLE || ItemConstants.isRechargeableItem(item.getItemId())) {
            items.add(item);
            return;
        }
        // Check if item can be merged into existing stacks
        final int slotMax = ItemProvider.getItemInfo(item.getItemId()).map(ItemInfo::getSlotMax).orElse(0);
        for (Item existingItem : items) {
            if (existingItem.getItemId() != item.getItemId()) {
                continue;
            }
            if (existingItem.getQuantity() >= slotMax) {
                continue;
            }
            final int newQuantity = Math.min(existingItem.getQuantity() + item.getQuantity(), slotMax);
            final int delta = newQuantity - existingItem.getQuantity();
            existingItem.setQuantity((short) newQuantity);
            item.setQuantity((short) (item.getQuantity() - delta));
            if (item.getQuantity() == 0) {
                break;
            }
        }
        if (item.getQuantity() > 0) {
            items.add(item);
        }
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

    public Item getItem(InventoryType inventoryType, int position) {
        final List<Item> filteredItems = items.stream()
                .filter((item) -> InventoryType.getByItemId(item.getItemId()) == inventoryType)
                .toList();
        if (position < 0 || position >= filteredItems.size()) {
            return null;
        }
        return filteredItems.get(position);
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
