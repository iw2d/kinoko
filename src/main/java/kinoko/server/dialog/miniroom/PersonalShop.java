package kinoko.server.dialog.miniroom;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.MiniRoomPacket;
import kinoko.packet.user.UserPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.GameConstants;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.item.ItemAttribute;
import kinoko.world.user.User;

import java.util.*;

public final class PersonalShop extends MiniRoom {
    private final String title;
    private final User owner;
    private final Map<Integer, User> guests = new HashMap<>();
    private final List<PlayerShopItem> items = new ArrayList<>();
    private final List<String> blockedList = new ArrayList<>();
    private boolean open = false;

    public PersonalShop(String title, User owner) {
        this.title = title;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public User getOwner() {
        return owner;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<PlayerShopItem> getItems() {
        return items;
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {
        final User user = locked.get();
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
                        targetPosition < 0 || setCount <= 0 || setSize <= 0 || price <= 0 ||
                        totalPrice <= 0 || totalPrice > Integer.MAX_VALUE ||
                        isOpen() || user.getCharacterId() != owner.getCharacterId() ||
                        items.size() >= GameConstants.PLAYER_SHOP_SLOT_MAX) {
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
                if ((item.hasAttribute(ItemAttribute.EQUIP_BINDED) || itemInfo.isQuest() || itemInfo.isTradeBlock()) && !item.isPossibleTrading()) {
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
                    items.add(new PlayerShopItem(partialItem, setCount, setSize, price));
                } else {
                    items.add(new PlayerShopItem(item, setCount, setSize, price));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
                user.write(MiniRoomPacket.PlayerShop.refresh(items));
            }
            case PSP_BuyItem -> {
                final int itemIndex = inPacket.decodeByte(); // nIdx
                final int setCount = inPacket.decodeShort();
                inPacket.decodeInt(); // ItemCRC
                if (itemIndex < 0 || itemIndex >= items.size() || setCount <= 0 || !isOpen() || user.getCharacterId() == owner.getCharacterId()) {
                    log.error("Received invalid personal shop action {}", mrp);
                    user.dispose();
                    return;
                }
            }
            case PSP_MoveItemToInventory -> {
                final int itemIndex = inPacket.decodeShort(); // nIdx
                if (itemIndex < 0 || itemIndex >= items.size() || isOpen() || user.getCharacterId() != owner.getCharacterId()) {
                    log.error("Received invalid personal shop action {}", mrp);
                    return;
                }
                final Optional<List<InventoryOperation>> addItemResult = user.getInventoryManager().addItem(items.remove(itemIndex).getItem());
                if (addItemResult.isEmpty()) {
                    throw new IllegalStateException("Could not add item to inventory");
                }
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                user.write(MiniRoomPacket.PlayerShop.refresh(items));
            }
            case PSP_DeliverBlackList -> {
                if (isOpen() || user.getCharacterId() != owner.getCharacterId() || items.isEmpty()) {
                    log.error("Received invalid personal shop action {}", mrp);
                    return;
                }
                final int size = inPacket.decodeShort();
                for (int i = 0; i < size; i++) {
                    blockedList.add(inPacket.decodeString()); // CConfig->m_asBlackList
                }
                setOpen(true);
                owner.getField().broadcastPacket(UserPacket.userMiniRoomBalloon(owner, this));
            }
            default -> {
                log.error("Unhandled personal shop action {}", mrp);
            }
        }
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.PersonalShop;
    }

    @Override
    public boolean checkPassword(String password) {
        return true;
    }

    @Override
    public int getMaxUsers() {
        return 3;
    }

    @Override
    public boolean addUser(User user) {
        if (blockedList.stream().anyMatch((name) -> name.equalsIgnoreCase(user.getCharacterName()))) {
            return false;
        }
        for (int i = 1; i <= 3; i++) {
            if (!guests.containsKey(i)) {
                guests.put(i, user);
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<Integer, User> getUsers() {
        final Map<Integer, User> users = new HashMap<>();
        users.put(0, owner);
        users.putAll(guests);
        return users;
    }
}
