package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.DropPickUpMessage;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public final class DropHandler {
    private static final Logger log = LogManager.getLogger(DropHandler.class);

    @Handler(InHeader.DROP_PICK_UP_REQUEST)
    public static void handleDropPickUpRequest(User user, InPacket inPacket) {
        final Field field = user.getField();
        final byte fieldKey = inPacket.decodeByte();
        if (field.getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // update_time
        inPacket.decodeShort(); // pt->x
        inPacket.decodeShort(); // pt->y
        final int objectId = inPacket.decodeInt(); // dwDropID
        inPacket.decodeInt(); // dwCliCrc

        // Find drop in field
        final Optional<Drop> dropResult = field.getDropPool().getById(objectId);
        if (dropResult.isEmpty()) {
            user.dispose();
            return;
        }
        final Drop drop = dropResult.get();

        try (var locked = user.acquire()) {
            // Check if drop can be added to inventory
            final InventoryManager im = user.getInventoryManager();
            if (drop.isMoney()) {
                final long newMoney = ((long) im.getMoney()) + drop.getMoney();
                if (newMoney > GameConstants.MAX_MONEY) {
                    user.write(WvsContext.message(DropPickUpMessage.unavailableForPickUp()));
                    user.dispose();
                    return;
                }
            } else {
                if (im.canAddItem(drop.getItem()).isEmpty()) {
                    user.write(WvsContext.message(DropPickUpMessage.cannotGetAnymoreItems()));
                    user.dispose();
                    return;
                }
            }

            // Try removing drop from field
            if (!field.getDropPool().removeDrop(drop, DropLeaveType.PICKED_UP_BY_USER, user.getCharacterId(), 0)) {
                user.dispose();
                return;
            }

            // Add drop to inventory
            if (drop.isMoney()) {
                if (im.addMoney(drop.getMoney())) {
                    user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney()));
                    user.write(WvsContext.message(DropPickUpMessage.money(drop.getMoney(), false)));
                }
            } else {
                final Optional<List<InventoryOperation>> addItemResult = im.addItem(drop.getItem());
                if (addItemResult.isPresent()) {
                    user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                    user.write(WvsContext.message(DropPickUpMessage.item(drop.getItem())));
                }
            }
        }
    }
}
