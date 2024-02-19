package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.WvsContext;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.drop.Drop;
import kinoko.world.drop.DropEnterType;
import kinoko.world.drop.DropOwnType;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
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
        final int oldPos = inPacket.decodeShort(); // nOldPos
        final int newPos = inPacket.decodeShort(); // nNewPos
        final int count = inPacket.decodeShort(); // nCount

        final InventoryType actualType = InventoryType.getByPosition(inventoryType, oldPos);
        final Inventory inventory = user.getInventory().getInventoryByType(actualType);
        final Item item = inventory.getItem(oldPos);
        if (item == null) {
            log.error("Could not find item in {} inventory, position {}", actualType.name(), oldPos);
            return;
        }
        if (newPos == 0) {
            // TODO: implement count

            // CDraggableItem::ThrowItem
            if (!inventory.removeItem(oldPos, item)) {
                log.error("Failed to remove item in {} inventory, position {}", actualType.name(), oldPos);
                return;
            }
            // Remove item from client inventory
            user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, oldPos), true));
            // Create drop
            final Drop drop = Drop.item(DropOwnType.NO_OWN, user, item, 0);
            drop.setX(user.getX()); // TODO: findFootholdBelow
            drop.setY(user.getY());
            user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE);
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
        drop.setX(user.getX()); // TODO: findFootholdBelow
        drop.setY(user.getY());
        user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE);
    }
}
