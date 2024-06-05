package kinoko.server.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.util.Locked;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

import java.util.*;
import java.util.stream.Collectors;

public final class TradingRoom extends MiniRoom {
    private final Map<Integer, Map<Integer, Item>> items = new HashMap<>(); // position -> slot, items
    private final Map<Integer, Integer> money = new HashMap<>(); // position -> money
    private final Map<Integer, Boolean> trade = new HashMap<>(); // position -> trade confirmation
    private final User inviter;
    private User target;

    public TradingRoom(User inviter) {
        this.inviter = inviter;
        for (int i = 0; i < 2; i++) {
            items.put(i, new HashMap<>());
            money.put(i, 0);
            trade.put(i, false);
        }
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.TRADING_ROOM;
    }

    @Override
    public boolean checkPassword(String password) {
        return true;
    }

    @Override
    public int getMaxUsers() {
        return 2;
    }

    @Override
    public boolean addUser(User user) {
        // Add target
        if (target != null) {
            return false;
        }
        this.target = user;
        // Update inviter client
        this.inviter.write(MiniRoomPacket.enterBase(1, user));
        return true;
    }

    @Override
    public Map<Integer, User> getUsers() {
        if (target == null) {
            return Map.of(
                    0, inviter
            );
        } else {
            return Map.of(
                    0, inviter,
                    1, target
            );
        }
    }


    // UTILITY METHODS -------------------------------------------------------------------------------------------------

    public User getInviter() {
        return inviter;
    }

    public boolean addItem(Locked<User> locked, InventoryType inventoryType, int position, int quantity, int index) {
        // Resolve other user
        final User user = locked.get();
        final User other = user.getCharacterId() != inviter.getCharacterId() ? inviter : target;
        if (other == null) {
            return false;
        }
        // Check if item can be placed in the index
        if (index < 1 || index > 9) {
            return false;
        }
        if (items.get(getPosition(user)).containsKey(index)) {
            return false;
        }
        // Resolve item
        final InventoryManager im = user.getInventoryManager();
        final Inventory inventory = im.getInventoryByType(inventoryType);
        final Item item = inventory.getItem(position);
        if (item == null) {
            return false;
        }
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
        final boolean isQuest = itemInfoResult.map(ItemInfo::isQuest).orElse(false);
        final boolean isTradeBlock = itemInfoResult.map(ItemInfo::isTradeBlock).orElse(false);
        if ((isQuest || isTradeBlock) && !item.isPossibleTrading()) {
            return false;
        }
        if (item.getItemType() == ItemType.BUNDLE && !ItemConstants.isRechargeableItem(item.getItemId()) &&
                item.getQuantity() > quantity) {
            // Update item count
            item.setQuantity((short) (item.getQuantity() - quantity));
            user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(inventoryType, position, item.getQuantity()), true));
            // Create partial item
            final Item partialItem = new Item(item);
            partialItem.setItemSn(user.getNextItemSn());
            partialItem.setQuantity((short) quantity);
            // Put item in trading room
            items.get(getPosition(user)).put(index, partialItem);
            user.write(MiniRoomPacket.TradingRoom.putItem(0, index, partialItem));
            other.write(MiniRoomPacket.TradingRoom.putItem(1, index, partialItem));
        } else {
            // Remove full stack from inventory
            if (!inventory.removeItem(position, item)) {
                return false;
            }
            user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, position), true));
            // Put item in trading room
            items.get(getPosition(user)).put(index, item);
            user.write(MiniRoomPacket.TradingRoom.putItem(0, index, item));
            other.write(MiniRoomPacket.TradingRoom.putItem(1, index, item));
        }
        return true;
    }

    public boolean addMoney(Locked<User> locked, int addMoney) {
        // Resolve other user
        final User user = locked.get();
        final User other = user.getCharacterId() != inviter.getCharacterId() ? inviter : target;
        if (other == null) {
            return false;
        }
        // Check if money can be added to trading room
        if (addMoney < 0) {
            return false;
        }
        final long newMoney = ((long) money.get(getPosition(user))) + addMoney;
        if (newMoney > Integer.MAX_VALUE || newMoney < 0) {
            return false;
        }
        // Move money
        final InventoryManager im = user.getInventoryManager();
        if (!im.addMoney(-addMoney)) {
            return false;
        }
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
        money.put(getPosition(user), (int) newMoney);
        user.write(MiniRoomPacket.TradingRoom.putMoney(0, (int) newMoney));
        other.write(MiniRoomPacket.TradingRoom.putMoney(1, (int) newMoney));
        return true;
    }

    public boolean confirmTrade(Locked<User> locked) {
        // Resolve other user
        final User user = locked.get();
        final User other = user.getCharacterId() != inviter.getCharacterId() ? inviter : target;
        if (other == null) {
            return false;
        }
        // Confirm trade for user and update other
        trade.put(getPosition(user), true);
        other.write(MiniRoomPacket.TradingRoom.trade());
        return trade.get(getPosition(other));
    }

    public boolean completeTrade(Locked<User> locked) {
        // Check for confirmations
        final User user = locked.get();
        if (!trade.get(getPosition(user))) {
            return false;
        }
        final User other = user.getCharacterId() != inviter.getCharacterId() ? inviter : target;
        if (other == null || !trade.get(getPosition(other))) {
            return false;
        }
        try (var lockedOther = other.acquire()) {
            // Check that user can add items + money from other's position
            final Set<Item> itemsForUser = items.get(getPosition(other)).values().stream().collect(Collectors.toUnmodifiableSet());
            final int moneyForUser = GameConstants.getTradeTax(money.get(getPosition(other)));
            if (!user.getInventoryManager().canAddItems(itemsForUser)) {
                user.write(WvsContext.message(Message.system("You do not have enough inventory space.")));
                other.write(WvsContext.message(Message.system(user.getCharacterName() + " does not have enough inventory space.")));
                return false;
            }
            if (!user.getInventoryManager().canAddMoney(moneyForUser)) {
                user.write(WvsContext.message(Message.system("You cannot hold any more mesos.")));
                other.write(WvsContext.message(Message.system(user.getCharacterName() + " cannot hold any more mesos.")));
                return false;
            }
            // Check that other can add items + money from user's position
            final Set<Item> itemsForOther = items.get(getPosition(user)).values().stream().collect(Collectors.toUnmodifiableSet());
            final int moneyForOther = GameConstants.getTradeTax(money.get(getPosition(user)));
            if (!other.getInventoryManager().canAddItems(itemsForOther)) {
                other.write(WvsContext.message(Message.system("You do not have enough inventory space.")));
                user.write(WvsContext.message(Message.system(user.getCharacterName() + " does not have enough inventory space.")));
                return false;
            }
            if (!other.getInventoryManager().canAddMoney(moneyForOther)) {
                other.write(WvsContext.message(Message.system("You cannot hold any more mesos.")));
                user.write(WvsContext.message(Message.system(user.getCharacterName() + " cannot hold any more mesos.")));
                return false;
            }
            // Add all items + money
            addItemsAndMoney(user, itemsForUser, moneyForUser);
            addItemsAndMoney(other, itemsForOther, moneyForOther);
            // Complete trade
            broadcastPacket(MiniRoomPacket.leave(0, LeaveType.TRADE_DONE)); // Trade successful. Please check the results.
            user.setDialog(null);
            other.setDialog(null);
        }
        return true;
    }

    /**
     * This should only be called after acquiring the {@link kinoko.util.Lockable<User>} object.
     *
     * @see User#isLocked()
     */
    public void cancelTradeUnsafe(User user) {
        // Return items and update client
        assert user.isLocked();
        addItemsAndMoney(user, items.get(getPosition(user)).values(), money.get(getPosition(user)));
        user.write(MiniRoomPacket.leave(0, LeaveType.USER_REQUEST)); // no message
        user.setDialog(null);
        final User other = user.getCharacterId() != inviter.getCharacterId() ? inviter : target;
        if (other != null) {
            try (var lockedOther = other.acquire()) {
                // Return the other user's items and update their client
                addItemsAndMoney(lockedOther.get(), items.get(getPosition(other)).values(), money.get(getPosition(other)));
                other.write(MiniRoomPacket.leave(1, LeaveType.CLOSED)); // Trade cancelled by the other character.
                other.setDialog(null);
            }
        }
    }

    public void cancelTrade(Locked<User> locked, LeaveType leaveType) {
        // Return items and update client
        final User user = locked.get();
        addItemsAndMoney(user, items.get(getPosition(user)).values(), money.get(getPosition(user)));
        user.write(MiniRoomPacket.leave(0, leaveType));
        user.setDialog(null);
        final User other = user.getCharacterId() != inviter.getCharacterId() ? inviter : target;
        if (other != null) {
            try (var lockedOther = other.acquire()) {
                // Return the other user's items and update their client
                addItemsAndMoney(lockedOther.get(), items.get(getPosition(other)).values(), money.get(getPosition(other)));
                other.write(MiniRoomPacket.leave(1, leaveType));
                other.setDialog(null);
            }
        }
    }

    private void addItemsAndMoney(User user, Collection<Item> addItems, int addMoney) {
        assert user.isLocked();
        final InventoryManager im = user.getInventoryManager();
        final List<InventoryOperation> inventoryOperations = new ArrayList<>();
        for (Item item : addItems) {
            final Optional<List<InventoryOperation>> addResult = im.addItem(item);
            if (addResult.isEmpty()) {
                throw new IllegalStateException("Failed to add item to inventory");
            }
            inventoryOperations.addAll(addResult.get());
        }
        if (!im.addMoney(addMoney)) {
            throw new IllegalStateException("Failed to add money");
        }
        user.write(WvsContext.inventoryOperation(inventoryOperations, false));
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
    }
}
