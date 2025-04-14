package kinoko.server.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.packet.user.UserPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.packet.InPacket;
import kinoko.world.GameConstants;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PersonalShop extends MiniRoom {
    private final List<PlayerShopItem> items = new ArrayList<>();
    private final List<String> blockedList = new ArrayList<>();
    private boolean open = false;

    public PersonalShop(String title) {
        super(title, null, 0);
    }

    public List<PlayerShopItem> getItems() {
        return items;
    }

    public int getOpenUserIndex() {
        for (int i = 0; i < getMaxUsers(); i++) {
            if (!getUsers().containsKey(i)) {
                return i;
            }
        }
        return -1;
    }

    public List<String> getBlockedList() {
        return blockedList;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.PersonalShop;
    }

    @Override
    public int getMaxUsers() {
        return 4;
    }

    @Override
    public void handlePacket(User user, MiniRoomProtocol mrp, InPacket inPacket) {
        switch (mrp) {
            case PSP_PutItem -> {
                final int targetType = inPacket.decodeByte(); // nTI
                final int targetPosition = inPacket.decodeShort(); // nPos
                final int setCount = inPacket.decodeShort(); // nCount / nSet
                final int setSize = inPacket.decodeShort(); // nSet
                final int price = inPacket.decodeInt(); // nPrice
                // Validate action
                final long totalPrice = ((long) price * setCount);
                final InventoryType inventoryType = InventoryType.getByValue(targetType);
                if (inventoryType == null || inventoryType == InventoryType.EQUIPPED ||
                        targetPosition < 0 || setCount <= 0 || setSize <= 0 ||
                        price <= 0 || totalPrice <= 0 || totalPrice > Integer.MAX_VALUE ||
                        items.size() >= GameConstants.PLAYER_SHOP_SLOT_MAX ||
                        isOpen() || !isOwner(user)) {
                    log.error("Received invalid personal shop action {}", mrp);
                    user.dispose();
                    return;
                }
                // Resolve item
                final int totalCount = setCount * setSize;
                final Item item = user.getInventoryManager().getInventoryByType(inventoryType).getItem(targetPosition);
                if (item == null || item.getQuantity() < totalCount) {
                    log.error("Could not resolve item in inventory type {} position {} for personal shop action {}", inventoryType, targetPosition, mrp);
                    user.dispose();
                    return;
                }
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
                if (itemInfoResult.isEmpty()) {
                    log.error("Could not resolve item info for item ID : {}", item.getItemId());
                    user.dispose();
                    return;
                }
                final ItemInfo itemInfo = itemInfoResult.get();
                if (itemInfo.isTradeBlock(item) || itemInfo.isAccountSharable()) {
                    log.error("Tried to put an untradable item into personal shop");
                    user.dispose();
                    return;
                }
                // Move item from inventory to shop
                final Optional<InventoryOperation> removeItemResult = user.getInventoryManager().removeItem(targetPosition, item, totalCount);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException("Could not remove item from inventory");
                }
                if (item.getQuantity() > totalCount) {
                    final Item partialItem = new Item(item);
                    partialItem.setItemSn(user.getNextItemSn());
                    partialItem.setQuantity((short) totalCount);
                    items.add(new PlayerShopItem(partialItem, price, setSize));
                } else {
                    items.add(new PlayerShopItem(item, price, setSize));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
                user.write(MiniRoomPacket.PlayerShop.refresh(items));
            }
            case PSP_BuyItem -> {
                final int itemIndex = inPacket.decodeByte(); // nIdx
                final int setCount = inPacket.decodeShort();
                inPacket.decodeInt(); // ItemCRC
                if (itemIndex < 0 || itemIndex >= items.size() || setCount <= 0 ||
                        !isOpen() || isOwner(user)) {
                    log.error("Received invalid personal shop action {}", mrp);
                    user.write(MiniRoomPacket.PlayerShop.buyResult(PlayerShopBuyResult.Unknown)); // Due to an error, the trade did not happen.
                    user.dispose();
                    return;
                }
                // Resolve item
                final InventoryManager im = user.getInventoryManager();
                final PlayerShopItem item = items.get(itemIndex);
                final int totalCount = item.getSetSize() * setCount;
                if (totalCount <= 0 || item.getItem().getQuantity() < totalCount || !im.canAddItem(item.getItem().getItemId(), totalCount)) {
                    user.write(MiniRoomPacket.PlayerShop.buyResult(PlayerShopBuyResult.NoSlot)); // Please check if your inventory is full or not.
                    user.dispose();
                    return;
                }
                // Resolve price
                final long totalPrice = ((long) item.getPrice() * setCount);
                if (totalPrice <= 0 || totalPrice > Integer.MAX_VALUE || !user.getInventoryManager().canAddMoney((int) -totalPrice)) {
                    user.write(MiniRoomPacket.PlayerShop.buyResult(PlayerShopBuyResult.NoMoney)); // You do not have enough mesos.
                    user.dispose();
                    return;
                }
                final User owner = getUser(0);
                final int moneyForOwner = GameConstants.getPersonalShopTax((int) totalPrice);
                if (!owner.getInventoryManager().canAddMoney(moneyForOwner)) {
                    user.write(MiniRoomPacket.PlayerShop.buyResult(PlayerShopBuyResult.OverPrice)); // The price of the item is too high for the trade.
                    user.dispose();
                    return;
                }
                // Do transaction
                item.getItem().setQuantity((short) (item.getItem().getQuantity() - totalCount));
                final Item buyItem = new Item(item.getItem());
                buyItem.setItemSn(owner.getNextItemSn());
                buyItem.setQuantity((short) totalCount);
                if (!im.addMoney((int) -totalPrice)) {
                    throw new IllegalStateException("Could not deduct total price from user");
                }
                final Optional<List<InventoryOperation>> addItemResult = im.addItem(buyItem);
                if (addItemResult.isEmpty()) {
                    throw new IllegalStateException("Could not add bought item to inventory");
                }
                if (!owner.getInventoryManager().addMoney(moneyForOwner)) {
                    throw new IllegalStateException("Could not add money to personal shop owner");
                }
                // Update clients
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                owner.write(WvsContext.statChanged(Stat.MONEY, owner.getInventoryManager().getMoney(), false));
                if (isNoMoreItem()) {
                    closeShop(owner, MiniRoomLeaveType.NoMoreItem);
                } else {
                    owner.write(MiniRoomPacket.PlayerShop.addSoldItem(itemIndex, setCount, user.getCharacterName()));
                    broadcastPacket(MiniRoomPacket.PlayerShop.refresh(items));
                }
            }
            case PSP_MoveItemToInventory -> {
                final int itemIndex = inPacket.decodeShort(); // nIdx
                if (itemIndex < 0 || itemIndex >= items.size() ||
                        isOpen() || !isOwner(user)) {
                    log.error("Received invalid personal shop action {}", mrp);
                    return;
                }
                final Optional<List<InventoryOperation>> addItemResult = user.getInventoryManager().addItem(items.remove(itemIndex).getItem());
                if (addItemResult.isEmpty()) {
                    throw new IllegalStateException("Could not add personal shop item to inventory");
                }
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                user.write(MiniRoomPacket.PlayerShop.moveItemToInventory(items.size(), itemIndex));
            }
            case PSP_DeliverBlackList -> {
                if (isOpen() || !isOwner(user) || items.isEmpty()) {
                    log.error("Received invalid personal shop action {}", mrp);
                    return;
                }
                final int size = inPacket.decodeShort();
                for (int i = 0; i < size; i++) {
                    blockedList.add(inPacket.decodeString()); // CConfig->m_asBlackList
                }
            }
            default -> {
                log.error("Unhandled personal shop action {}", mrp);
            }
        }
    }

    @Override
    public void leave(User user) {
        final int userIndex = getUserIndex(user);
        if (userIndex == 0) {
            closeShop(user, MiniRoomLeaveType.UserRequest);
        } else {
            broadcastPacket(MiniRoomPacket.leave(userIndex, MiniRoomLeaveType.UserRequest));
            removeUser(userIndex);
            user.setDialog(null);
            updateBalloon();
        }
    }

    @Override
    public void updateBalloon() {
        getField().broadcastPacket(UserPacket.userMiniRoomBalloon(getUser(0), this));
    }

    public void closeShop(User owner, MiniRoomLeaveType leaveType) {
        assert isOwner(owner);
        // Return items
        final List<InventoryOperation> inventoryOperations = new ArrayList<>();
        for (PlayerShopItem item : items) {
            if (item.getItem().getQuantity() == 0) {
                continue;
            }
            final Optional<List<InventoryOperation>> addItemResult = owner.getInventoryManager().addItem(item.getItem());
            if (addItemResult.isEmpty()) {
                throw new IllegalStateException("Could not add personal shop item to inventory");
            }
            inventoryOperations.addAll(addItemResult.get());
        }
        owner.write(WvsContext.inventoryOperation(inventoryOperations, false));
        // Remove guests
        for (int i = 1; i < getMaxUsers(); i++) {
            final User guest = getUser(i);
            if (guest == null) {
                continue;
            }
            guest.write(MiniRoomPacket.leave(i, MiniRoomLeaveType.HostOut)); // The shop is closed.
            guest.setDialog(null);
        }
        // Remove shop
        broadcastPacket(MiniRoomPacket.leave(0, leaveType));
        owner.setDialog(null);
        getField().getMiniRoomPool().removeMiniRoom(this);
        getField().broadcastPacket(UserPacket.userMiniRoomBalloonRemove(owner));
    }

    private boolean isNoMoreItem() {
        for (PlayerShopItem item : items) {
            if (item.getSetCount() > 0) {
                return false;
            }
        }
        return true;
    }
}
