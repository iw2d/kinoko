package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.item.*;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class InventoryHandler {
    private static final Logger log = LogManager.getLogger(InventoryHandler.class);

    @Handler(InHeader.USER_GATHER_ITEM_REQUEST)
    public static void handlerUserGatherItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nType
        if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
            user.dispose();
            return;
        }
        try (var locked = user.acquire()) {
            final InventoryManager im = user.getInventoryManager();
            final Inventory inventory = im.getInventoryByType(inventoryType);
            // Find stackable items : itemId -> Set<Tuple<position, item>>
            final Map<Integer, Set<Tuple<Integer, Item>>> stackable = new HashMap<>();
            for (var entry : inventory.getItems().entrySet()) {
                final int position = entry.getKey();
                final Item item = entry.getValue();
                if (item.getItemType() != ItemType.BUNDLE || ItemConstants.isRechargeableItem(item.getItemId())) {
                    continue;
                }
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
                if (itemInfoResult.isEmpty() || itemInfoResult.get().getSlotMax() <= 1) {
                    continue;
                }
                if (!stackable.containsKey(item.getItemId())) {
                    stackable.put(item.getItemId(), new HashSet<>());
                }
                stackable.get(item.getItemId()).add(new Tuple<>(position, item));
            }
            // Get required inventory operations
            final List<InventoryOperation> inventoryOperations = new ArrayList<>();
            for (var entry : stackable.entrySet()) {
                if (entry.getValue().size() <= 1) {
                    continue;
                }
                final int slotMax = ItemProvider.getItemInfo(entry.getKey()).orElseThrow().getSlotMax(); // getItemInfo succeeded in above loop
                final List<Tuple<Integer, Item>> sortedItems = entry.getValue().stream()
                        .sorted(Comparator.comparingInt(Tuple::getLeft))
                        .toList();
                int total = sortedItems.stream().mapToInt((tuple) -> tuple.getRight().getQuantity()).sum();
                for (var tuple : sortedItems) {
                    final int position = tuple.getLeft();
                    if (total > slotMax) {
                        inventoryOperations.add(InventoryOperation.itemNumber(inventoryType, position, slotMax));
                        total -= slotMax;
                    } else {
                        if (total > 0) {
                            inventoryOperations.add(InventoryOperation.itemNumber(inventoryType, position, total));
                            total = 0;
                        } else {
                            inventoryOperations.add(InventoryOperation.delItem(inventoryType, position));
                        }
                    }
                }
            }
            // Apply inventory operations and update client
            im.applyInventoryOperations(inventoryOperations);
            user.write(WvsContext.inventoryOperation(inventoryOperations, true));
            user.write(WvsContext.gatherItemResult(inventoryType));
        }
    }

    @Handler(InHeader.USER_SORT_ITEM_REQUEST)
    public static void handlerUserSortItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nType
        if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
            user.dispose();
            return;
        }
        try (var locked = user.acquire()) {
            final InventoryManager im = user.getInventoryManager();
            // Create array for sorting
            final Item[] items = new Item[GameConstants.INVENTORY_MAX_SLOTS]; // using 0-based indexing for positions (inventory uses 1-based)
            for (var entry : im.getInventoryByType(inventoryType).getItems().entrySet()) {
                items[entry.getKey() - 1] = entry.getValue();
            }
            // Selection sort to find required swaps
            final List<InventoryOperation> inventoryOperations = new ArrayList<>();
            for (int i = 0; i < items.length - 1; i++) {
                int k = i; // minimum index
                for (int j = i + 1; j < items.length; j++) {
                    if (items[j] == null) {
                        continue;
                    }
                    // Consolidate, sorting by ID (increasing) and quantity (decreasing)
                    if (items[k] == null ||
                            items[j].getItemId() < items[k].getItemId() ||
                            (items[j].getItemId() == items[k].getItemId() &&
                                    items[j].getQuantity() > items[k].getQuantity())) {
                        k = j;
                    }
                }
                // Perform swap
                final Item temp = items[k];
                items[k] = items[i];
                items[i] = temp;
                inventoryOperations.add(InventoryOperation.position(inventoryType, k + 1, i + 1)); // again, inventory uses 1-based positions
            }
            // Apply inventory operations and update client
            im.applyInventoryOperations(inventoryOperations);
            user.write(WvsContext.inventoryOperation(inventoryOperations, true));
            user.write(WvsContext.sortItemResult(inventoryType));
        }
    }

    @Handler(InHeader.USER_CHANGE_SLOT_POSITION_REQUEST)
    public static void handleUserChangeSlotPositionRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int type = inPacket.decodeByte(); // nType
        final InventoryType inventoryType = InventoryType.getByValue(type);
        if (inventoryType == null) {
            log.error("Unknown inventory type : {}", type);
            return;
        }
        final short oldPos = inPacket.decodeShort(); // nOldPos
        final short newPos = inPacket.decodeShort(); // nNewPos
        final short count = inPacket.decodeShort(); // nCount

        try (var locked = user.acquire()) {
            final InventoryManager im = user.getInventoryManager();
            final Inventory inventory = im.getInventoryByType(InventoryType.getByPosition(inventoryType, oldPos));
            final Item item = inventory.getItem(oldPos);
            if (item == null) {
                log.error("Could not find item in {} inventory, position {}", inventoryType.name(), oldPos);
                return;
            }
            if (newPos == 0) {
                // CDraggableItem::ThrowItem : item is deleted if (quest || tradeBlock) && POSSIBLE_TRADING attribute not set
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
                final boolean isQuest = itemInfoResult.map(ItemInfo::isQuest).orElse(false);
                final boolean isTradeBlock = itemInfoResult.map(ItemInfo::isTradeBlock).orElse(false);
                final DropEnterType dropEnterType = ((isQuest || isTradeBlock) && !item.isPossibleTrading()) ?
                        DropEnterType.FADING_OUT :
                        DropEnterType.CREATE;
                if (item.getItemType() == ItemType.BUNDLE && !ItemConstants.isRechargeableItem(item.getItemId()) &&
                        item.getQuantity() > count) {
                    // Update item count
                    item.setQuantity((short) (item.getQuantity() - count));
                    user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(inventoryType, oldPos, item.getQuantity()), true));
                    // Create partial item
                    final Item partialItem = new Item(item);
                    partialItem.setItemSn(user.getNextItemSn());
                    partialItem.setQuantity(count);
                    // Create drop
                    final Drop drop = Drop.item(DropOwnType.NO_OWN, user, partialItem, 0);
                    user.getField().getDropPool().addDrop(drop, dropEnterType, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
                } else {
                    // Full drop
                    if (!inventory.removeItem(oldPos, item)) {
                        log.error("Failed to remove item in {} inventory, position {}", inventoryType.name(), oldPos);
                        return;
                    }
                    // Remove item from client inventory
                    user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, oldPos), true));
                    // Create drop
                    final Drop drop = Drop.item(DropOwnType.NO_OWN, user, item, user.getCharacterId());
                    user.getField().getDropPool().addDrop(drop, dropEnterType, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
                }
            } else {
                final Inventory secondInventory = im.getInventoryByType(InventoryType.getByPosition(inventoryType, newPos));
                final Item secondItem = secondInventory.getItem(newPos);
                inventory.putItem(oldPos, secondItem);
                secondInventory.putItem(newPos, item);
                // Swap item position in client inventory
                user.write(WvsContext.inventoryOperation(InventoryOperation.position(inventoryType, oldPos, newPos), true));
            }
        }
    }

    @Handler(InHeader.USER_DROP_MONEY_REQUEST)
    public static void handleUserDropMoneyRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int money = inPacket.decodeInt(); // nAmount
        try (var locked = user.acquire()) {
            final InventoryManager im = user.getInventoryManager();
            if (money <= 0 || !im.addMoney(-money)) {
                user.dispose();
                return;
            }
            final Drop drop = Drop.money(DropOwnType.NO_OWN, user, money, user.getCharacterId());
            user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney()));
        }
    }
}
