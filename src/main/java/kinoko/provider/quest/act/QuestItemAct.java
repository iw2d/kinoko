package kinoko.provider.quest.act;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryType;
import kinoko.world.job.Job;
import kinoko.world.user.User;

import java.util.*;
import java.util.stream.Collectors;

public final class QuestItemAct implements QuestAct {
    private final Set<ItemData> items;

    public QuestItemAct(Set<ItemData> items) {
        this.items = items;
    }

    @Override
    public boolean canAct(User user) {
        final Map<InventoryType, Integer> requiredSlots = new EnumMap<>(InventoryType.class);
        final Set<ItemData> filteredItems = getFilteredItems(user);
        // Handle required slots for random items
        for (ItemData itemData : filteredItems) {
            if (itemData.isRandom()) {
                final InventoryType inventoryType = InventoryType.getByItemId(itemData.getItemId());
                requiredSlots.put(inventoryType, 1);
            }
        }
        // Handle static items
        for (ItemData itemData : filteredItems) {
            if (itemData.isRandom()) {
                continue;
            }
            if (itemData.getCount() > 0) {
                final InventoryType inventoryType = InventoryType.getByItemId(itemData.getItemId());
                requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + 1);
            } else {
                if (!user.hasItem(itemData.getItemId(), itemData.getCount())) {
                    return false;
                }
            }
        }
        // Check for required slots
        for (var entry : requiredSlots.entrySet()) {
            final Inventory inventory = user.getInventory().getInventoryByType(entry.getKey());
            final int remainingSlots = inventory.getSize() - inventory.getItems().size();
            if (remainingSlots < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void doAct(User user) {
        // TODO
    }

    private Set<ItemData> getFilteredItems(User user) {
        return items.stream()
                .filter(itemData -> itemData.checkGender(user.getGender()) && itemData.checkJob(user.getJob()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static QuestItemAct from(WzListProperty itemList) {
        final Set<ItemData> items = new HashSet<>();
        for (var itemEntry : itemList.getItems().entrySet()) {
            if (!(itemEntry.getValue() instanceof WzListProperty itemProp)) {
                throw new ProviderError("Failed to resolve quest act item list");
            }
            final ItemData itemData = new ItemData(
                    WzProvider.getInteger(itemProp.get("id")),
                    WzProvider.getInteger(itemProp.get("count"), 1),
                    WzProvider.getInteger(itemProp.get("prop"), -1),
                    WzProvider.getInteger(itemProp.get("gender"), 2),
                    WzProvider.getInteger(itemProp.get("job"), -1),
                    WzProvider.getInteger(itemProp.get("jobEx"), -1),
                    WzProvider.getInteger(itemProp.get("resignRemove"), 0) != 0
            );
            assert !(itemData.getCount() <= 0 && itemData.getProp() != -1);
            items.add(itemData);
        }
        return new QuestItemAct(
                Collections.unmodifiableSet(items)
        );
    }

    private static class ItemData {
        private final int itemId;
        private final int count;
        private final int prop;
        private final int gender;
        private final int job;
        private final int jobEx;
        private final boolean resignRemove;

        public ItemData(int itemId, int count, int prop, int gender, int job, int jobEx, boolean resignRemove) {
            this.itemId = itemId;
            this.count = count;
            this.prop = prop;
            this.gender = gender;
            this.job = job;
            this.jobEx = jobEx;
            this.resignRemove = resignRemove;
        }

        public int getItemId() {
            return itemId;
        }

        public int getCount() {
            return count;
        }

        public int getProp() {
            return prop;
        }

        public boolean isResignRemove() {
            return resignRemove;
        }

        public boolean isRandom() {
            return prop == -1;
        }

        public boolean checkJob(int jobId) {
            final int jobFlag;
            if (jobId == Job.EVAN_BEGINNER.getJobId()) {
                jobFlag = 0x20000;
            } else {
                jobFlag = 1 << (jobId / 100);
            }
            final int jobExFlag = jobFlag & this.jobEx;
            return (jobExFlag | jobFlag & this.job) != 0 || jobId / 100 == 9;
        }

        public boolean checkGender(int gender) {
            return this.gender == 2 || this.gender == gender;
        }
    }
}
