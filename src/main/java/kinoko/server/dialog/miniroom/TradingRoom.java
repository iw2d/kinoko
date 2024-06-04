package kinoko.server.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.util.Locked;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.Item;
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

    public User getInviter() {
        return inviter;
    }

    public boolean addItem(Locked<User> locked, int itemId, int quantity, int slot) {
        return false;
    }

    public boolean addMoney(Locked<User> locked, int money) {
        return false;
    }

    public boolean completeTrade(Locked<User> locked) {
        // Check for confirmation
        final User user = locked.get();
        final User other = user.getCharacterId() != inviter.getCharacterId() ? inviter : target;
        if (other == null || !trade.get(getPosition(other))) {
            return false;
        }
        try (var lockedOther = other.acquire()) {
            // Check that user can add items + money from other's position
            final int indexForUser = getPosition(other);
            final InventoryManager uim = user.getInventoryManager();
            if (!uim.canAddItems(items.get(indexForUser).values().stream().collect(Collectors.toUnmodifiableSet()))) {
                user.write(WvsContext.message(Message.system("You do not have enough inventory space.")));
                other.write(WvsContext.message(Message.system(user.getCharacterName() + " does not have enough inventory space.")));
                return false;
            }
            if (!uim.canAddMoney(money.get(indexForUser))) {
                user.write(WvsContext.message(Message.system("You cannot hold any more mesos.")));
                other.write(WvsContext.message(Message.system(user.getCharacterName() + " cannot hold any more mesos.")));
                return false;
            }
            // Check that other can add items + money from user's position
            final int indexForOther = getPosition(user);
            final InventoryManager oim = other.getInventoryManager();
            if (!oim.canAddItems(items.get(indexForOther).values().stream().collect(Collectors.toUnmodifiableSet()))) {
                other.write(WvsContext.message(Message.system("You do not have enough inventory space.")));
                user.write(WvsContext.message(Message.system(user.getCharacterName() + " does not have enough inventory space.")));
                return false;
            }
            if (!oim.canAddMoney(money.get(indexForOther))) {
                other.write(WvsContext.message(Message.system("You cannot hold any more mesos.")));
                user.write(WvsContext.message(Message.system(user.getCharacterName() + " cannot hold any more mesos.")));
                return false;
            }
            // Add all items + money
            addItemsAndMoney(user, indexForUser);
            addItemsAndMoney(other, indexForOther);
        }
        return true;
    }

    /**
     * This should only be called after acquiring the {@link kinoko.util.Lockable<User>} object.
     *
     * @see User#isLocked()
     */
    public void cancelTrade(User user) {
        // Return items and update client
        assert user.isLocked();
        addItemsAndMoney(user, getPosition(user));
        user.write(MiniRoomPacket.leave(getPosition(user), LeaveType.USER_REQUEST)); // no message
        user.setDialog(null);
        final User other = user.getCharacterId() != inviter.getCharacterId() ? inviter : target;
        if (other == null) {
            return;
        }
        try (var lockedOther = other.acquire()) {
            // Return the other user's items and update their client
            addItemsAndMoney(lockedOther.get(), getPosition(other));
            other.write(MiniRoomPacket.leave(getPosition(user), LeaveType.CLOSED)); // Trade cancelled by the other character.
            other.setDialog(null);
        }
    }

    private void addItemsAndMoney(User user, int index) {
        assert user.isLocked();
        final InventoryManager im = user.getInventoryManager();
        final List<InventoryOperation> inventoryOperations = new ArrayList<>();
        for (Item item : items.get(index).values()) {
            final Optional<List<InventoryOperation>> addResult = im.addItem(item);
            if (addResult.isEmpty()) {
                throw new IllegalStateException("Failed to add item to inventory");
            }
            inventoryOperations.addAll(addResult.get());
        }
        if (!im.addMoney(money.get(index))) {
            throw new IllegalStateException("Failed to add money");
        }
        user.write(WvsContext.inventoryOperation(inventoryOperations, false));
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
    }
}
