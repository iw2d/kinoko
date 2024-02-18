package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.WvsContext;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.life.Life;
import kinoko.world.life.drop.Drop;
import kinoko.world.life.drop.DropEnterType;
import kinoko.world.life.drop.DropOwnType;
import kinoko.world.user.User;
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
        final int oldPos = inPacket.decodeShort(); // nOldPos
        final int newPos = inPacket.decodeShort(); // nNewPos
        final int count = inPacket.decodeShort(); // nCount

        final InventoryType oldType = (inventoryType == InventoryType.EQUIP && oldPos < 0) ? InventoryType.EQUIPPED : inventoryType;
        final InventoryType newType = (inventoryType == InventoryType.EQUIP && newPos < 0) ? InventoryType.EQUIPPED : inventoryType;

        final int actualPos = oldType == InventoryType.EQUIPPED ? -oldPos : oldPos;

        final Inventory inventory = user.getInventory().getInventoryByType(oldType);
        final Item item = inventory.getItems().get(actualPos);
        if (item == null) {
            log.error("Could not find item in {} inventory, position {}", oldType.name(), actualPos);
            return;
        }
        if (newPos == 0) {
            // CDraggableItem::ThrowItem
            if (!inventory.getItems().remove(actualPos, item)) {
                log.error("Failed to remove item in {} inventory, position {}", oldType.name(), actualPos);
                return;
            }
            // Remove item from inventory
            user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, oldPos)));
            // Create drop
            final Drop drop = Drop.item(DropOwnType.NO_OWN, user, item, 0);
            user.getField().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY());
        } else {
            // TODO
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
        user.getField().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY());
    }

    @Handler(InHeader.DROP_PICK_UP_REQUEST)
    public static void handleDropPickUpRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte();
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // update_time
        inPacket.decodeShort(); // pt->x
        inPacket.decodeShort(); // pt->y
        final int objectId = inPacket.decodeInt(); // dwDropID
        inPacket.decodeInt(); // dwCliCrc

        final Field field = user.getField();
        final Optional<Life> lifeResult = field.getLifeById(objectId);
        if (lifeResult.isEmpty() || !(lifeResult.get() instanceof Drop drop)) {
            user.dispose();
            return;
        }

        // TODO
        user.dispose();
    }
}
