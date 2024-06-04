package kinoko.world.item;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.*;

public final class InventoryManager {
    private static final Logger log = LogManager.getLogger(InventoryManager.class);
    private Inventory equipped;
    private Inventory equipInventory;
    private Inventory consumeInventory;
    private Inventory installInventory;
    private Inventory etcInventory;
    private Inventory cashInventory;
    private int money;
    private Instant extSlotExpire;

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

    public Instant getExtSlotExpire() {
        return extSlotExpire;
    }

    public void setExtSlotExpire(Instant extSlotExpire) {
        this.extSlotExpire = extSlotExpire;
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

    public int getItemCount(int itemId) {
        int quantity = 0;
        for (Item item : getInventoryByItemId(itemId).getItems().values()) {
            if (item.getItemId() == itemId) {
                quantity += item.getQuantity();
            }
        }
        return quantity;
    }

    public boolean hasItem(int itemId, int quantity) {
        return getItemCount(itemId) >= quantity;
    }

    public Optional<InventoryOperation> updateItem(int position, Item item) {
        final InventoryType inventoryType = InventoryType.getByItemId(item.getItemId());
        final Inventory inventory = getInventoryByType(InventoryType.getByPosition(inventoryType, position));
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
        final Inventory inventory = getInventoryByType(InventoryType.getByPosition(inventoryType, position));
        if (inventory.getItem(position) != item) {
            return Optional.empty();
        }
        if (item.getQuantity() < quantity) {
            // Tried to remove more than the available quantity
            log.error("Tried to remove more than the available quantity : {} < {}", item.getQuantity(), quantity);
            return Optional.empty();
        } else if (item.getQuantity() > quantity) {
            // Deduct quantity
            item.setQuantity((short) (item.getQuantity() - 1));
            return Optional.of(InventoryOperation.itemNumber(inventoryType, position, item.getQuantity()));
        } else {
            // Remove item
            if (!inventory.removeItem(position, item)) {
                log.error("Failed to remove item from position {}", position);
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
        final List<InventoryOperation> inventoryOperations = new ArrayList<>();
        final InventoryType inventoryType = InventoryType.getByItemId(originalItem.getItemId());
        final Inventory inventory = getInventoryByType(inventoryType);
        final int slotMax = ItemProvider.getItemInfo(originalItem.getItemId()).map(ItemInfo::getSlotMax).orElse(0);
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
                if (existingItem.getQuantity() >= slotMax) {
                    continue;
                }
                final int newQuantity = Math.min(existingItem.getQuantity() + item.getQuantity(), slotMax);
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
        applyInventoryOperations(inventoryOperations);
        return Optional.of(inventoryOperations);
    }

    public boolean canAddItem(Item item) {
        return canAddItems(Set.of(item));
    }

    public boolean canAddItems(Set<Item> items) {
        final List<Tuple<Integer, Integer>> itemCountSet = items.stream()
                .map((item) -> new Tuple<>(item.getItemId(), (int) item.getQuantity()))
                .toList();
        return canAddItems(itemCountSet);
    }

    public boolean canAddItems(List<Tuple<Integer, Integer>> items) {
        final Map<InventoryType, Integer> requiredSlots = new EnumMap<>(InventoryType.class);
        final Map<Integer, Integer> itemCounter = new HashMap<>(); // item id -> count
        // Populate item counter map
        for (var tuple : items) {
            final int itemId = tuple.getLeft();
            final int count = tuple.getRight();
            itemCounter.put(itemId, itemCounter.getOrDefault(itemId, 0) + count);
        }
        // Check if item can be merged into existing stacks
        for (int itemId : itemCounter.keySet()) {
            final InventoryType inventoryType = InventoryType.getByItemId(itemId);
            final Inventory inventory = getInventoryByType(inventoryType);
            int count = itemCounter.get(itemId);
            final int slotMax = ItemProvider.getItemInfo(itemId).map(ItemInfo::getSlotMax).orElse(0);
            for (var entry : inventory.getItems().entrySet()) {
                final Item existingItem = entry.getValue();
                if (existingItem.getItemId() != itemId) {
                    continue;
                }
                if (existingItem.getQuantity() >= slotMax) {
                    continue;
                }
                final int newQuantity = Math.min(existingItem.getQuantity() + count, slotMax);
                final int delta = newQuantity - existingItem.getQuantity();
                count -= delta;
            }
            final int remainingStacks = Math.ceilDiv(count, slotMax);
            requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + remainingStacks);
        }
        // Check required slots
        for (var entry : requiredSlots.entrySet()) {
            final Inventory inventory = getInventoryByType(entry.getKey());
            final int remainingSlots = inventory.getRemaining();
            if (remainingSlots < entry.getValue()) {
                return false;
            }
        }
        return true;
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
