package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.GameConstants;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.item.*;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class InventoryHandler {
    private static final Logger log = LogManager.getLogger(InventoryHandler.class);

    @Handler(InHeader.USER_GATHER_ITEM_REQUEST)
    public static void handlerUserGatherItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final InventoryType type = InventoryType.getByValue(inPacket.decodeByte()); // nType
        // TODO
    }

    @Handler(InHeader.USER_SORT_ITEM_REQUEST)
    public static void handlerUserSortItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final InventoryType type = InventoryType.getByValue(inPacket.decodeByte()); // nType
        // TODO
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
            if (money <= 0 || !user.getInventoryManager().addMoney(-money)) {
                user.dispose();
                return;
            }
            final Drop drop = Drop.money(DropOwnType.NO_OWN, user, money, user.getCharacterId());
            user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
            user.write(WvsContext.statChanged(Stat.MONEY, user.getInventoryManager().getMoney()));
        }
    }
}
