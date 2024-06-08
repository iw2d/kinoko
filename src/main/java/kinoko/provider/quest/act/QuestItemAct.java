package kinoko.provider.quest.act;

import kinoko.packet.user.UserLocal;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.quest.QuestItemData;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Locked;
import kinoko.util.Util;
import kinoko.world.item.*;
import kinoko.world.quest.QuestResult;
import kinoko.world.quest.QuestResultType;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;

import java.util.*;
import java.util.stream.Collectors;

public final class QuestItemAct implements QuestAct {
    private final int questId;
    private final Set<QuestItemData> items;
    private final List<QuestItemData> choices;

    public QuestItemAct(int questId, Set<QuestItemData> items, List<QuestItemData> choices) {
        this.questId = questId;
        this.items = items;
        this.choices = choices;
    }

    public Set<QuestItemData> getItems() {
        return items;
    }

    public List<QuestItemData> getChoices() {
        return choices;
    }

    public void restoreLostItems(Locked<User> locked, Set<Integer> lostItems) {
        final User user = locked.get();
        final InventoryManager im = user.getInventoryManager();
        final Set<QuestItemData> filteredItems = getFilteredItems(user.getGender(), user.getJob()).stream()
                .filter(itemData -> lostItems.contains(itemData.getItemId()))
                .collect(Collectors.toUnmodifiableSet());
        final Map<InventoryType, Integer> requiredSlots = new EnumMap<>(InventoryType.class);

        // Calculate required slots and validate lost items
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic() || itemData.getCount() <= 0) {
                continue;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemData.getItemId());
            if (itemInfoResult.isEmpty()) {
                user.write(UserLocal.questResult(QuestResult.of(QuestResultType.Failed_Unknown)));
                return;
            }
            if (!itemInfoResult.get().isQuest()) {
                user.write(UserLocal.questResult(QuestResult.of(QuestResultType.Failed_Unknown)));
                return;
            }
            final InventoryType inventoryType = InventoryType.getByItemId(itemData.getItemId());
            requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + 1);
        }

        // Check for required slots
        for (var entry : requiredSlots.entrySet()) {
            final Inventory inventory = im.getInventoryByType(entry.getKey());
            final int remainingSlots = inventory.getRemaining();
            if (remainingSlots < entry.getValue()) {
                user.write(UserLocal.questResult(QuestResult.failedInventory(questId)));
                return;
            }
        }

        // Give missing items
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic() || itemData.getCount() <= 0) {
                continue;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemData.getItemId());
            if (itemInfoResult.isEmpty()) {
                user.write(UserLocal.questResult(QuestResult.of(QuestResultType.Failed_Unknown)));
                return;
            }
            final int count = itemData.getCount() - im.getItemCount(itemData.getItemId());
            if (count <= 0) {
                user.write(UserLocal.questResult(QuestResult.of(QuestResultType.Failed_Unknown)));
                return;
            }
            final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), count);
            final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);
            if (addItemResult.isEmpty()) {
                user.write(UserLocal.questResult(QuestResult.of(QuestResultType.Failed_Unknown)));
                return;
            }
            user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(item)));
        }
    }

    public void removeQuestItems(Locked<User> locked) {
        final User user = locked.get();
        final InventoryManager im = user.getInventoryManager();

        // Remove quest items
        for (QuestItemData itemData : getItems()) {
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemData.getItemId());
            if (itemInfoResult.isEmpty() || !itemInfoResult.get().isQuest()) {
                continue;
            }
            final int count = im.getItemCount(itemData.getItemId());
            if (count <= 0) {
                continue;
            }
            final Optional<List<InventoryOperation>> removeItemResult = im.removeItem(itemData.getItemId(), count);
            if (removeItemResult.isEmpty()) {
                user.write(UserLocal.questResult(QuestResult.of(QuestResultType.Failed_Unknown)));
                return;
            }
            user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(itemData.getItemId(), count)));
        }
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        final InventoryManager im = user.getInventoryManager();
        final Set<QuestItemData> filteredItems = getFilteredItems(user.getGender(), user.getJob());

        // Handle required slots for random items
        final Map<InventoryType, Integer> requiredSlots = new EnumMap<>(InventoryType.class);
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isRandom()) {
                continue;
            }
            final InventoryType inventoryType = InventoryType.getByItemId(itemData.getItemId());
            requiredSlots.put(inventoryType, 1);
        }

        // Handle required slots for choice items
        if (rewardIndex >= 0) {
            if (choices.size() < rewardIndex) {
                user.write(UserLocal.questResult(QuestResult.of(QuestResultType.Failed_Unknown)));
                return false;
            }
            final QuestItemData choiceItemData = choices.get(rewardIndex);
            final InventoryType inventoryType = InventoryType.getByItemId(choiceItemData.getItemId());
            requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + 1);
        }

        // Handle static items - required slots if count > 0, else check if present in inventory
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic()) {
                continue;
            }
            if (itemData.getCount() > 0) {
                final InventoryType inventoryType = InventoryType.getByItemId(itemData.getItemId());
                requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + 1);
            } else {
                if (!im.hasItem(itemData.getItemId(), itemData.getCount())) {
                    user.write(UserLocal.questResult(QuestResult.of(QuestResultType.Failed_Unknown)));
                    return false;
                }
            }
        }

        // Check for required slots
        for (var entry : requiredSlots.entrySet()) {
            final Inventory inventory = im.getInventoryByType(entry.getKey());
            final int remainingSlots = inventory.getRemaining();
            if (remainingSlots < entry.getValue()) {
                user.write(UserLocal.questResult(QuestResult.failedInventory(questId)));
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        final InventoryManager im = user.getInventoryManager();
        final Set<QuestItemData> filteredItems = getFilteredItems(user.getGender(), user.getJob());

        // Take required items
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic() || itemData.getCount() >= 0) {
                continue;
            }
            final int quantity = -itemData.getCount();
            final Optional<List<InventoryOperation>> removeItemResult = im.removeItem(itemData.getItemId(), quantity);
            if (removeItemResult.isEmpty()) {
                return false;
            }
            user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(itemData.getItemId(), itemData.getCount())));
        }

        // Give choice item
        if (rewardIndex >= 0) {
            if (choices.size() < rewardIndex) {
                return false;
            }
            final QuestItemData choiceItemData = choices.get(rewardIndex);
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(choiceItemData.getItemId());
            if (itemInfoResult.isEmpty()) {
                return false;
            }
            final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), choiceItemData.getCount());
            final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);
            if (addItemResult.isEmpty()) {
                return false;
            }
            user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(item)));
        }

        // Give static items
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic() || itemData.getCount() <= 0) {
                continue;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemData.getItemId());
            if (itemInfoResult.isEmpty()) {
                return false;
            }
            final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), itemData.getCount());
            final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);
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
            final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);
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

    public static QuestItemAct from(int questId, WzListProperty itemList) {
        final Set<QuestItemData> items = QuestItemData.resolveItemData(itemList, 1);
        final List<QuestItemData> choices = QuestItemData.resolveChoiceItemData(itemList);
        return new QuestItemAct(
                questId,
                Collections.unmodifiableSet(items),
                Collections.unmodifiableList(choices)
        );
    }
}
