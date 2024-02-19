package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.WvsContext;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.GameConstants;
import kinoko.world.drop.Drop;
import kinoko.world.drop.DropEnterType;
import kinoko.world.drop.DropOwnType;
import kinoko.world.item.*;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        final InventoryType actualType = InventoryType.getByPosition(inventoryType, oldPos);
        final Inventory inventory = user.getInventory().getInventoryByType(actualType);
        final Item item = inventory.getItem(oldPos);
        if (item == null) {
            log.error("Could not find item in {} inventory, position {}", actualType.name(), oldPos);
            return;
        }
        if (newPos == 0) {
            // CDraggableItem::ThrowItem
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
                user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
            } else {
                // Full drop
                if (!inventory.removeItem(oldPos, item)) {
                    log.error("Failed to remove item in {} inventory, position {}", actualType.name(), oldPos);
                    return;
                }
                // Remove item from client inventory
                user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, oldPos), true));
                // Create drop
                final Drop drop = Drop.item(DropOwnType.NO_OWN, user, item, 0);
                user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
            }
        } else {
            final InventoryType secondType = InventoryType.getByPosition(inventoryType, newPos);
            final Inventory secondInventory = user.getInventory().getInventoryByType(secondType);
            final Item secondItem = secondInventory.getItem(newPos);
            inventory.putItem(oldPos, secondItem);
            secondInventory.putItem(newPos, item);
            // Swap item position in client inventory
            user.write(WvsContext.inventoryOperation(InventoryOperation.position(inventoryType, oldPos, newPos), true));
        }
    }

    @Handler(InHeader.USER_DROP_MONEY_REQUEST)
    public static void handleUserDropMoneyRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int money = inPacket.decodeInt(); // nAmount
        if (money <= 0 || !user.addMoney(-money)) {
            user.dispose();
            return;
        }
        final Drop drop = Drop.money(DropOwnType.NO_OWN, user, money, 0);
        user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
    }
}
