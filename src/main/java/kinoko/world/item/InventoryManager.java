package kinoko.world.item;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class InventoryManager {
    private Inventory equipped;
    private Inventory equipInventory;
    private Inventory consumeInventory;
    private Inventory installInventory;
    private Inventory etcInventory;
    private Inventory cashInventory;
    private int money;

    public Inventory getEquipped() {
        return equipped;
    }

    public void setEquipped(Inventory equipped) {
        this.equipped = equipped;
    }

    public Inventory getEquipInventory() {
        return equipInventory;
    }

    public void setEquipInventory(Inventory equipInventory) {
        this.equipInventory = equipInventory;
    }

    public Inventory getConsumeInventory() {
        return consumeInventory;
    }

    public void setConsumeInventory(Inventory consumeInventory) {
        this.consumeInventory = consumeInventory;
    }

    public Inventory getInstallInventory() {
        return installInventory;
    }

    public void setInstallInventory(Inventory installInventory) {
        this.installInventory = installInventory;
    }

    public Inventory getEtcInventory() {
        return etcInventory;
    }

    public void setEtcInventory(Inventory etcInventory) {
        this.etcInventory = etcInventory;
    }

    public Inventory getCashInventory() {
        return cashInventory;
    }

    public void setCashInventory(Inventory cashInventory) {
        this.cashInventory = cashInventory;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Inventory getInventoryByItemId(int itemId) {
        return getInventoryByType(InventoryType.getByItemId(itemId));
    }

    public Inventory getInventoryByType(InventoryType inventoryType) {
        switch (inventoryType) {
            case EQUIPPED -> {
                return getEquipped();
            }
            case EQUIP -> {
                return getEquipInventory();
            }
            case CONSUME -> {
                return getConsumeInventory();
            }
            case INSTALL -> {
                return getInstallInventory();
            }
            case ETC -> {
                return getEtcInventory();
            }
            default -> {
                return getCashInventory();
            }
        }
    }


    // HELPER METHODS -------------------------------------------------------------------------------------------------

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

    public boolean hasItem(int itemId, int quantity) {
        boolean hasItem = false;
        for (Item item : getInventoryByItemId(itemId).getItems().values()) {
            if (item.getItemId() == itemId) {
                hasItem = true;
                quantity -= item.getQuantity();
            }
        }
        return hasItem && quantity <= 0;
    }

    public Optional<InventoryOperation> updateItem(int position, Item item) {
        final InventoryType inventoryType = InventoryType.getByItemId(item.getItemId());
        final Inventory inventory = getInventoryByType(inventoryType);
        if (inventory.getItem(position) != item) {
            return Optional.empty();
        }
        return Optional.of(InventoryOperation.newItem(inventoryType, position, item));
    }

    public Optional<InventoryOperation> removeItem(int position, Item item) {
        return removeItem(position, item, item.getQuantity());
    }

    public Optional<InventoryOperation> removeItem(int position, Item item, int quantity) {
        final InventoryType inventoryType = InventoryType.getByItemId(item.getItemId());
        final Inventory inventory = getInventoryByType(inventoryType);
        if (inventory.getItem(position) != item) {
            return Optional.empty();
        }
        if (item.getQuantity() < quantity) {
            // Tried to remove more than the available quantity
            return Optional.empty();
        } else if (item.getQuantity() > quantity) {
            // Deduct quantity
            item.setQuantity((short) (item.getQuantity() - 1));
            return Optional.of(InventoryOperation.itemNumber(inventoryType, position, item.getQuantity()));
        } else {
            // Remove item
            if (!inventory.removeItem(position, item)) {
                return Optional.empty();
            }
            return Optional.of(InventoryOperation.delItem(inventoryType, position));
        }
    }

    public Optional<List<InventoryOperation>> removeItem(int itemId, int quantity) {
        final List<InventoryOperation> inventoryOperations = new ArrayList<>();
        final InventoryType inventoryType = InventoryType.getByItemId(itemId);
        final Inventory inventory = getInventoryByType(inventoryType);
        for (var entry : inventory.getItems().entrySet()) {
            final Item existingItem = entry.getValue();
            if (existingItem.getItemId() != itemId) {
                continue;
            }
            final int newQuantity = Math.max(existingItem.getQuantity() - quantity, 0);
            if (newQuantity > 0) {
                inventoryOperations.add(InventoryOperation.itemNumber(inventoryType, entry.getKey(), newQuantity));
            } else {
                inventoryOperations.add(InventoryOperation.delItem(inventoryType, entry.getKey()));
            }
            quantity -= existingItem.getQuantity();
            if (quantity <= 0) {
                break;
            }
        }
        if (quantity > 0) {
            return Optional.empty();
        }
        applyInventoryOperations(inventoryOperations);
        return Optional.of(inventoryOperations);
    }

    public Optional<List<InventoryOperation>> addItem(Item originalItem) {
        final Optional<List<InventoryOperation>> addItemResult = canAddItem(originalItem);
        if (addItemResult.isEmpty()) {
            return addItemResult;
        }
        applyInventoryOperations(addItemResult.get());
        return addItemResult;
    }

    public Optional<List<InventoryOperation>> canAddItem(Item originalItem) {
        final List<InventoryOperation> inventoryOperations = new ArrayList<>();
        final InventoryType inventoryType = InventoryType.getByItemId(originalItem.getItemId());
        final Inventory inventory = getInventoryByType(inventoryType);
        // Retrieve item info
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(originalItem.getItemId());
        if (itemInfoResult.isEmpty()) {
            return Optional.empty();
        }
        final ItemInfo ii = itemInfoResult.get();
        // Clone item and try adding item to inventory
        final Item item = new Item(originalItem);
        boolean canAddItem = false;
        if (item.getItemType() == ItemType.BUNDLE && !ItemConstants.isRechargeableItem(item.getItemId())) {
            // Merge into existing stacks
            for (var entry : inventory.getItems().entrySet()) {
                final Item existingItem = entry.getValue();
                if (existingItem.getItemId() != item.getItemId()) {
                    continue;
                }
                if (existingItem.getQuantity() >= ii.getSlotMax()) {
                    continue;
                }
                final int newQuantity = Math.min(existingItem.getQuantity() + item.getQuantity(), ii.getSlotMax());
                final int delta = newQuantity - existingItem.getQuantity();
                // Create item number operation for existing item and reduce item quantity
                inventoryOperations.add(InventoryOperation.itemNumber(inventoryType, entry.getKey(), newQuantity));
                item.setQuantity((short) (item.getQuantity() - delta));
                if (item.getQuantity() == 0) {
                    canAddItem = true;
                    break;
                }
            }
        }
        if (item.getQuantity() > 0) {
            // Find available slot
            for (int i = 1; i <= inventory.getSize(); i++) { // slots are 1-based in client
                if (inventory.getItem(i) != null) {
                    continue;
                }
                // Create new item operation
                inventoryOperations.add(InventoryOperation.newItem(inventoryType, i, item));
                canAddItem = true;
                break;
            }
        }
        if (!canAddItem) {
            return Optional.empty();
        }
        return Optional.of(inventoryOperations);
    }

    public void applyInventoryOperations(List<InventoryOperation> inventoryOperations) {
        for (InventoryOperation op : inventoryOperations) {
            final InventoryType inventoryType = InventoryType.getByPosition(op.getInventoryType(), op.getPosition());
            final Inventory inventory = getInventoryByType(inventoryType);
            switch (op.getOperationType()) {
                case NEW_ITEM -> {
                    inventory.putItem(op.getPosition(), op.getItem());
                }
                case ITEM_NUMBER -> {
                    inventory.getItem(op.getPosition()).setQuantity((short) op.getNewQuantity());
                }
                case POSITION -> {
                    final Item firstItem = inventory.removeItem(op.getPosition());
                    if (op.getNewPosition() == 0) {
                        inventory.removeItem(op.getPosition(), firstItem);
                    } else {
                        final Inventory secondInventory = getInventoryByType(InventoryType.getByPosition(op.getInventoryType(), op.getNewPosition()));
                        final Item secondItem = secondInventory.removeItem(op.getNewPosition());
                        inventory.putItem(op.getPosition(), secondItem);
                        secondInventory.putItem(op.getNewPosition(), firstItem);
                    }
                }
                case DEL_ITEM -> {
                    inventory.removeItem(op.getPosition());
                }
                case EXP -> {
                    final Item item = inventory.getItem(op.getPosition());
                    item.getEquipData().setExp(op.getNewExp());
                }
            }
        }
    }
}
