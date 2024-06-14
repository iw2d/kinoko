package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.QuestProvider;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.script.ScriptDispatcher;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public final class FieldHandler {
    private static final Logger log = LogManager.getLogger(FieldHandler.class);

    @Handler(InHeader.CANCEL_INVITE_PARTY_MATCH)
    public static void handleCancelInvitePartyMatch(User user, InPacket inPacket) {
    }


    // BEGIN_DROPPOOL --------------------------------------------------------------------------------------------------

    @Handler(InHeader.DropPickUpRequest)
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
                if (newMoney > GameConstants.MONEY_MAX) {
                    user.write(MessagePacket.unavailableForPickUp());
                    user.dispose();
                    return;
                }
            } else {
                // Inventory full
                if (!im.canAddItem(drop.getItem())) {
                    user.write(MessagePacket.cannotGetAnymoreItems());
                    user.dispose();
                    return;
                }
                // Quest item handling
                if (drop.isQuest()) {
                    final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(drop.getQuestId());
                    if (questRecordResult.isEmpty()) {
                        user.write(MessagePacket.unavailableForPickUp());
                        user.dispose();
                        return;
                    }
                    final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(drop.getQuestId());
                    if (questInfoResult.isPresent() && questInfoResult.get().hasRequiredItem(user, drop.getItem().getItemId())) {
                        user.write(MessagePacket.cannotGetAnymoreItems());
                        user.dispose();
                        return;
                    }
                }
            }

            // Try removing drop from field
            if (!field.getDropPool().removeDrop(drop, DropLeaveType.PICKED_UP_BY_USER, user.getCharacterId(), 0, 0)) {
                user.dispose();
                return;
            }

            // Add drop to inventory
            if (drop.isMoney()) {
                if (!im.addMoney(drop.getMoney())) {
                    throw new IllegalStateException("Could not add money to inventory");
                }
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                user.write(MessagePacket.pickUpMoney(drop.getMoney(), false));
            } else {
                final Optional<List<InventoryOperation>> addItemResult = im.addItem(drop.getItem());
                if (addItemResult.isEmpty()) {
                    throw new IllegalStateException("Could not add item to inventory");
                }
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                user.write(MessagePacket.pickUpItem(drop.getItem()));
            }
        }
    }


    // BEGIN_REACTORPOOL -----------------------------------------------------------------------------------------------

    @Handler(InHeader.ReactorHit)
    public static void handleReactorHit(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwID
        inPacket.decodeInt(); // skillReactor?
        inPacket.decodeInt(); // dwHitOption
        final short delay = inPacket.decodeShort(); // tDelay
        final int skillId = inPacket.decodeInt(); // skillId, 0 for basic attack

        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getById(objectId);
        if (reactorResult.isEmpty()) {
            log.error("Received ReactorHit for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedReactor = reactorResult.get().acquire()) {
            // Hit reactor
            final Reactor reactor = lockedReactor.get();
            if (reactor.isNotHitable()) {
                log.error("{} : tried to hit reactor that is not hitable", reactor);
                return;
            }
            if (!reactor.hit(skillId)) {
                log.error("{} : could not hit reactor with skill ID {}", reactor, skillId);
                return;
            }
            field.getReactorPool().hitReactor(reactor, delay);
            // Check if last state and dispatch action script
            if (!reactor.isLastState() || !reactor.hasAction()) {
                return;
            }
            ScriptDispatcher.startReactorScript(user, reactor);
        }
    }

    @Handler(InHeader.ReactorTouch)
    public static void handleReactorTouch(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwID
        final boolean inside = inPacket.decodeBoolean(); // PtInRect

        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getById(objectId);
        if (reactorResult.isEmpty()) {
            log.error("Received ReactorTouch for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedReactor = reactorResult.get().acquire()) {
            // Hit reactor
            final Reactor reactor = lockedReactor.get();
            if (!reactor.isActivateByTouch()) {
                log.error("{} : tried to hit reactor that is not activated by touch", reactor);
                return;
            }
            // There are no reactors activated by touch in v95
            log.error(String.format("Unexpected reactor touch received for %s", reactor));
        }
    }

    @Handler(InHeader.RequireFieldObstacleStatus)
    public static void handleRequireFieldObstacleStatus(User user, InPacket inPacket) {
    }
}
