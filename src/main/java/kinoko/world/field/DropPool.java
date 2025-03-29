package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.QuestProvider;
import kinoko.provider.map.Foothold;
import kinoko.provider.quest.QuestInfo;
import kinoko.util.Rect;
import kinoko.world.GameConstants;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class DropPool extends FieldObjectPool<Drop> {
    public DropPool(Field field) {
        super(field);
    }

    public void addDrop(Drop drop, DropEnterType enterType, int x, int y, int delay) {
        // Clamp x position to map bounds
        final Rect rootBounds = field.getMapInfo().getRootBounds();
        final int boundLeft = rootBounds.getLeft() + GameConstants.DROP_BOUND_OFFSET;
        final int boundRight = rootBounds.getRight() - GameConstants.DROP_BOUND_OFFSET;
        if (boundLeft <= boundRight) {
            x = Math.clamp(x, boundLeft, boundRight);
        }
        // Assign foothold
        final Optional<Foothold> footholdResult = field.getFootholdBelow(x, y);
        if (footholdResult.isPresent()) {
            drop.setX(x);
            drop.setY(footholdResult.get().getYFromX(x));
        } else {
            drop.setX(x);
            drop.setY(y);
        }
        drop.setField(field);
        drop.setId(field.getNewObjectId());
        // Handle drop reactors
        if (enterType != DropEnterType.FADING_OUT) {
            addObject(drop);
            field.getReactorPool().forEach((reactor) -> reactor.handleDrop(drop));
        }
        // Handle quest drops
        if (drop.isQuest()) {
            field.getUserPool().forEach((user) -> {
                final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(drop.getQuestId());
                if (questRecordResult.isEmpty() || questRecordResult.get().getState() != QuestState.PERFORM) {
                    return;
                }
                final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(drop.getQuestId());
                if (questInfoResult.isPresent() && questInfoResult.get().hasRequiredItem(user, drop.getItem().getItemId())) {
                    return;
                }
                user.write(FieldPacket.dropEnterField(drop, enterType, delay));
            });
        } else {
            field.broadcastPacket(FieldPacket.dropEnterField(drop, enterType, delay));
        }
    }

    public void addDrops(List<Drop> drops, DropEnterType enterType, int centerX, int centerY, int initialDelay, int addDelay) {
        // Split and shuffle drops
        final List<Drop> normalDrops = new ArrayList<>();
        final List<Drop> questDrops = new ArrayList<>();
        for (Drop drop : drops) {
            if (drop.isQuest()) {
                questDrops.add(drop);
            } else {
                normalDrops.add(drop);
            }
        }
        Collections.shuffle(normalDrops);
        // Add quest drops on the outer edges to avoid displacing normal drops
        for (int i = 0; i < questDrops.size(); i++) {
            if (i % 2 == 0) {
                normalDrops.addFirst(questDrops.get(i));
            } else {
                normalDrops.addLast(questDrops.get(i));
            }
        }
        // Add normal drops
        int dropX = centerX - (normalDrops.size() * GameConstants.DROP_SPREAD / 2);
        int delay = initialDelay;
        for (Drop drop : normalDrops) {
            addDrop(drop, enterType, dropX, centerY, delay);
            dropX += GameConstants.DROP_SPREAD;
            delay += addDelay;
        }
    }

    public synchronized boolean removeDrop(Drop drop, DropLeaveType leaveType, int pickUpId, int petIndex, int delay) {
        if (!removeObject(drop)) {
            return false;
        }
        field.broadcastPacket(FieldPacket.dropLeaveField(drop, leaveType, pickUpId, petIndex, delay));
        return true;
    }

    public void pickUpDrop(User user, Drop drop, DropLeaveType leaveType, int petIndex) {
        // Verify user can pick up drop
        if (!drop.canPickUp(user)) {
            return;
        }

        // Check if drop can be added to inventory
        final InventoryManager im = user.getInventoryManager();
        if (drop.isMoney()) {
            final long newMoney = ((long) im.getMoney()) + drop.getMoney();
            if (newMoney > GameConstants.MONEY_MAX) {
                user.write(MessagePacket.unavailableForPickUp());
                return;
            }
        } else {
            // Inventory full
            if (!im.canAddItem(drop.getItem())) {
                user.write(MessagePacket.cannotGetAnymoreItems());
                return;
            }
            // Quest item handling
            if (drop.isQuest()) {
                final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(drop.getQuestId());
                if (questRecordResult.isEmpty() || questRecordResult.get().getState() != QuestState.PERFORM) {
                    user.write(MessagePacket.unavailableForPickUp());
                    return;
                }
                final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(drop.getQuestId());
                if (questInfoResult.isPresent() && questInfoResult.get().hasRequiredItem(user, drop.getItem().getItemId())) {
                    user.write(MessagePacket.cannotGetAnymoreItems());
                    return;
                }
            }
        }

        // Try removing drop from field
        if (!removeDrop(drop, leaveType, user.getCharacterId(), petIndex, 0)) {
            return;
        }

        // Add drop to inventory
        if (drop.isMoney()) {
            int money = drop.getMoney();
            if (drop.getOwnType() == DropOwnType.PARTYOWN) {
                final List<User> partyMembers = user.getField().getUserPool().getPartyMembers(user.getPartyId());
                if (!partyMembers.isEmpty()) {
                    final int split = (int) Math.round(0.8 * money / partyMembers.size());
                    for (User member : partyMembers) {
                        if (member.getCharacterId() == user.getCharacterId()) {
                            continue;
                        }
                        if (member.getInventoryManager().addMoney(split)) {
                            money -= split;
                            member.write(WvsContext.statChanged(Stat.MONEY, member.getInventoryManager().getMoney(), false));
                            member.write(MessagePacket.pickUpMoney(split, false));
                        }
                    }
                }
            }
            if (money <= 0 || !im.addMoney(money)) {
                throw new IllegalStateException("Could not add money to inventory");
            }
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
            user.write(MessagePacket.pickUpMoney(money, false));
        } else {
            final Optional<List<InventoryOperation>> addItemResult = im.addItem(drop.getItem());
            if (addItemResult.isPresent()) {
                user.write(WvsContext.inventoryOperation(addItemResult.get(), false));
                user.write(MessagePacket.pickUpItem(drop.getItem()));
            }
        }
    }

    public void expireDrops(Instant now) {
        final var iter = objects.values().iterator();
        while (iter.hasNext()) {
            final Drop drop = iter.next();
            // Check drop expire time and remove drop
            if (now.isBefore(drop.getExpireTime())) {
                continue;
            }
            iter.remove();
            field.broadcastPacket(FieldPacket.dropLeaveField(drop, DropLeaveType.TIMEOUT, 0, 0, 0));
        }
    }
}
