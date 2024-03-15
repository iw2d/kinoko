package kinoko.provider.quest.act;

import kinoko.packet.user.UserLocal;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.quest.QuestItemData;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Locked;
import kinoko.util.Util;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.user.User;

import java.util.*;
import java.util.stream.Collectors;

public final class QuestItemAct implements QuestAct {
    private final Set<QuestItemData> items;

    public QuestItemAct(Set<QuestItemData> items) {
        this.items = items;
    }

    @Override
    public boolean canAct(Locked<User> locked) {
        final User user = locked.get();
        final Set<QuestItemData> filteredItems = getFilteredItems(user.getGender(), user.getJob());

        // Handle required slots for random items
        final Map<InventoryType, Integer> requiredSlots = new EnumMap<>(InventoryType.class);
        for (QuestItemData itemData : filteredItems) {
            if (itemData.isRandom()) {
                final InventoryType inventoryType = InventoryType.getByItemId(itemData.getItemId());
                requiredSlots.put(inventoryType, 1);
            }
        }

        // Handle static items
        for (QuestItemData itemData : filteredItems) {
            if (itemData.isRandom()) {
                continue;
            }
            if (itemData.getCount() > 0) {
                final InventoryType inventoryType = InventoryType.getByItemId(itemData.getItemId());
                requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + 1);
            } else {
                if (!user.getInventoryManager().hasItem(itemData.getItemId(), itemData.getCount())) {
                    return false;
                }
            }
        }

        // Check for required slots
        for (var entry : requiredSlots.entrySet()) {
            final Inventory inventory = user.getInventoryManager().getInventoryByType(entry.getKey());
            final int remainingSlots = inventory.getRemaining();
            if (remainingSlots < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked) {
        final User user = locked.get();
        final Set<QuestItemData> filteredItems = getFilteredItems(user.getGender(), user.getJob());

        // Take required items
        for (QuestItemData itemData : filteredItems) {
            if (itemData.isRandom() || itemData.getCount() >= 0) {
                continue;
            }
            final int quantity = -itemData.getCount();
            final Optional<List<InventoryOperation>> removeItemResult = user.getInventoryManager().removeItem(itemData.getItemId(), quantity);
            if (removeItemResult.isEmpty()) {
                return false;
            }
            user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(itemData.getItemId(), itemData.getCount())));
        }

        // Give static items
        for (QuestItemData itemData : filteredItems) {
            if (itemData.isRandom() || itemData.getCount() <= 0) {
                continue;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemData.getItemId());
            if (itemInfoResult.isEmpty()) {
                return false;
            }
            final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), itemData.getCount());
            final Optional<List<InventoryOperation>> addItemResult = user.getInventoryManager().addItem(item);
            if (addItemResult.isEmpty()) {
                return false;
            }
            user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(item)));
        }

        // Give random item
        final Set<QuestItemData> randomItems = filteredItems.stream()
                .filter(QuestItemData::isRandom)
                .collect(Collectors.toUnmodifiableSet());
        final Optional<QuestItemData> randomResult = Util.getRandomFromCollection(randomItems, QuestItemData::getProp);
        if (randomResult.isPresent()) {
            final QuestItemData itemData = randomResult.get();
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemData.getItemId());
            if (itemInfoResult.isEmpty()) {
                return false;
            }
            final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), itemData.getCount());
            final Optional<List<InventoryOperation>> addItemResult = user.getInventoryManager().addItem(item);
            if (addItemResult.isEmpty()) {
                return false;
            }
            user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(item)));
        }

        return true;
    }

    private Set<QuestItemData> getFilteredItems(int gender, int job) {
        return items.stream()
                .filter(itemData -> itemData.checkGender(gender) && itemData.checkJob(job))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static QuestItemAct from(WzListProperty itemList) {
        final Set<QuestItemData> items = QuestItemData.resolveItemData(itemList, 1);
        return new QuestItemAct(
                Collections.unmodifiableSet(items)
        );
    }
}
