package kinoko.server.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.packet.InPacket;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

import java.util.*;
import java.util.stream.Collectors;

public final class TradingRoom extends MiniRoom {
    private final Map<User, Map<Integer, Item>> items = new HashMap<>(); // user -> slot, items
    private final Map<User, Integer> money = new HashMap<>(); // user -> offered money
    private final Map<User, Boolean> confirm = new HashMap<>(); // user -> trade confirmation


    public TradingRoom() {
        super(null, null, 0);
    }

    public User getOther(User user) {
        return getUserIndex(user) == 0 ? getUser(1) : getUser(0);
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.TradingRoom;
    }

    @Override
    public int getMaxUsers() {
        return 2;
    }

    @Override
    public void handlePacket(User user, MiniRoomProtocol mrp, InPacket inPacket) {
        final User other = getOther(user);
        if (other == null) {
            log.error("Received mini room action {} without another player in the trading room", mrp);
            return;
        }
        switch (mrp) {
            case TRP_PutItem -> {
                // CTradingRoomDlg::PutItem
                final int type = inPacket.decodeByte(); // nItemTI
                final InventoryType inventoryType = InventoryType.getByValue(type);
                if (inventoryType == null) {
                    log.error("Unknown inventory type : {}", type);
                    return;
                }
                final int position = inPacket.decodeShort(); // nSlotPosition
                final int quantity = inPacket.decodeShort(); // nInputNo_Result
                final int index = inPacket.decodeByte(); // ItemIndexFromPoint
                if (!putItem(user, inventoryType, position, quantity, index)) {
                    log.error("Failed to add item to trading room");
                    user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                }
            }
            case TRP_PutMoney -> {
                // CTradingRoomDlg::PutMoney
                final int addMoney = inPacket.decodeInt(); // nInputNo_Result
                if (!putMoney(user, addMoney)) {
                    log.error("Failed to add money to trading room");
                    user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                }
            }
            case TRP_Trade -> {
                // CTradingRoomDlg::Trade
                confirm.put(user, true);
                // Update other
                if (!confirm.getOrDefault(other, false)) {
                    other.write(MiniRoomPacket.TradingRoom.trade());
                    return;
                }
                // Complete trade
                if (!completeTrade(user)) {
                    cancelTrade(user, MiniRoomLeaveType.TradeFail); // Trade unsuccessful.
                }
            }
            case TRP_ItemCRC -> {
                // ignored
            }
            default -> {
                log.error("Unhandled trading room action {}", mrp);
            }
        }
    }

    @Override
    public void leave(User user) {
        cancelTrade(user, MiniRoomLeaveType.Closed); // Trade cancelled by the other character.
    }

    @Override
    public void updateBalloon() {
        throw new IllegalStateException("Tried to update balloon for trading room");
    }


    // UTILITY METHODS -------------------------------------------------------------------------------------------------

    public void cancelTrade(User user, MiniRoomLeaveType leaveType) {
        // Return items and update client
        addItemsAndMoney(user, items.getOrDefault(user, Map.of()).values(), money.getOrDefault(user, 0));
        user.write(MiniRoomPacket.leave(0, leaveType));
        user.setDialog(null);
        final User other = getOther(user);
        if (other != null) {
            // Return the other user's items and update their client
            addItemsAndMoney(other, items.getOrDefault(other, Map.of()).values(), money.getOrDefault(other, 0));
            other.write(MiniRoomPacket.leave(1, leaveType));
            other.setDialog(null);
        }
        getField().getMiniRoomPool().removeMiniRoom(this);
    }

    private boolean putItem(User user, InventoryType inventoryType, int position, int quantity, int index) {
        // Resolve other user
        final User other = getOther(user);
        if (other == null) {
            return false;
        }
        // Check if item can be placed in the index
        if (index < 1 || index > 9) {
            return false;
        }
        if (items.getOrDefault(user, Map.of()).containsKey(index)) {
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
        if (itemInfoResult.isEmpty()) {
            return false;
        }
        final ItemInfo itemInfo = itemInfoResult.get();
        if ((item.hasAttribute(ItemAttribute.EQUIP_BINDED) || itemInfo.isQuest() || itemInfo.isTradeBlock()) && !item.isPossibleTrading()) {
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
            items.computeIfAbsent(user, (key) -> new HashMap<>()).put(index, partialItem);
            user.write(MiniRoomPacket.TradingRoom.putItem(0, index, partialItem));
            other.write(MiniRoomPacket.TradingRoom.putItem(1, index, partialItem));
        } else {
            // Remove full stack from inventory
            if (!inventory.removeItem(position, item)) {
                return false;
            }
            user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, position), true));
            // Put item in trading room
            items.computeIfAbsent(user, (key) -> new HashMap<>()).put(index, item);
            user.write(MiniRoomPacket.TradingRoom.putItem(0, index, item));
            other.write(MiniRoomPacket.TradingRoom.putItem(1, index, item));
        }
        return true;
    }

    private boolean putMoney(User user, int addMoney) {
        // Resolve other user
        final User other = getOther(user);
        if (other == null) {
            return false;
        }
        // Check if money can be added to trading room
        if (addMoney < 0) {
            return false;
        }
        final long newMoney = (long) money.getOrDefault(user, 0) + addMoney;
        if (newMoney > Integer.MAX_VALUE || newMoney < 0) {
            return false;
        }
        // Move money
        final InventoryManager im = user.getInventoryManager();
        if (!im.addMoney(-addMoney)) {
            return false;
        }
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
        money.put(user, (int) newMoney);
        user.write(MiniRoomPacket.TradingRoom.putMoney(0, (int) newMoney));
        other.write(MiniRoomPacket.TradingRoom.putMoney(1, (int) newMoney));
        return true;
    }

    private boolean completeTrade(User user) {
        // Check for confirmations
        if (!confirm.getOrDefault(user, false)) {
            return false;
        }
        final User other = getOther(user);
        if (other == null || !confirm.getOrDefault(other, false)) {
            return false;
        }
        // Check that user can add items + money from other's position
        final Set<Item> itemsForUser = items.getOrDefault(other, Map.of()).values().stream().collect(Collectors.toUnmodifiableSet());
        final int moneyForUser = GameConstants.getTradeTax(money.getOrDefault(other, 0));
        if (!user.getInventoryManager().canAddItems(itemsForUser)) {
            user.write(MessagePacket.system("You do not have enough inventory space."));
            other.write(MessagePacket.system(user.getCharacterName() + " does not have enough inventory space."));
            return false;
        }
        if (!user.getInventoryManager().canAddMoney(moneyForUser)) {
            user.write(MessagePacket.system("You cannot hold any more mesos."));
            other.write(MessagePacket.system(user.getCharacterName() + " cannot hold any more mesos."));
            return false;
        }
        // Check that other can add items + money from user's position
        final Set<Item> itemsForOther = items.getOrDefault(user, Map.of()).values().stream().collect(Collectors.toUnmodifiableSet());
        final int moneyForOther = GameConstants.getTradeTax(money.getOrDefault(user, 0));
        if (!other.getInventoryManager().canAddItems(itemsForOther)) {
            other.write(MessagePacket.system("You do not have enough inventory space."));
            user.write(MessagePacket.system(user.getCharacterName() + " does not have enough inventory space."));
            return false;
        }
        if (!other.getInventoryManager().canAddMoney(moneyForOther)) {
            other.write(MessagePacket.system("You cannot hold any more mesos."));
            user.write(MessagePacket.system(user.getCharacterName() + " cannot hold any more mesos."));
            return false;
        }
        // Process items
        for (Item item : itemsForUser) {
            item.setPossibleTrading(false);
        }
        for (Item item : itemsForOther) {
            item.setPossibleTrading(false);
        }
        // Add all items + money
        addItemsAndMoney(user, itemsForUser, moneyForUser);
        addItemsAndMoney(other, itemsForOther, moneyForOther);
        // Complete trade
        broadcastPacket(MiniRoomPacket.leave(0, MiniRoomLeaveType.TradeDone)); // Trade successful. Please check the results.
        user.setDialog(null);
        other.setDialog(null);
        return true;
    }

    private void addItemsAndMoney(User user, Collection<Item> addItems, int addMoney) {
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
