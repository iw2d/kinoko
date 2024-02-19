package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.DropPickUpMessage;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.GameConstants;
import kinoko.world.drop.Drop;
import kinoko.world.drop.DropLeaveType;
import kinoko.world.field.Field;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class DropHandler {
    private static final Logger log = LogManager.getLogger(DropHandler.class);

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
        final Optional<Drop> dropResult = field.getDropPool().getById(objectId);
        if (dropResult.isEmpty()) {
            user.dispose();
            return;
        }

        final Drop drop = dropResult.get();
        if (drop.isMoney()) {
            final long newMoney = ((long) user.getMoney()) + drop.getMoney();
            if (newMoney > GameConstants.MAX_MONEY) {
                user.write(WvsContext.message(DropPickUpMessage.unavailableForPickUp()));
                user.dispose();
                return;
            }
        } else {
            if (user.getInventory().canAddItem(drop.getItem()).isEmpty()) {
                user.write(WvsContext.message(DropPickUpMessage.cannotGetAnymoreItems()));
                user.dispose();
                return;
            }
        }
        if (field.getDropPool().removeDrop(drop, DropLeaveType.PICKED_UP_BY_USER, user.getId(), 0)) {
            if (drop.isMoney()) {
                user.addMoney(drop.getMoney());
            } else {
                user.addItem(drop.getItem());
            }
        } else {
            user.dispose();
        }
    }
}
