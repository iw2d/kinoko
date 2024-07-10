package kinoko.provider.quest.check;

import kinoko.provider.quest.QuestItemData;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Locked;
import kinoko.world.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class QuestItemCheck implements QuestCheck {
    private final List<QuestItemData> items;

    public QuestItemCheck(List<QuestItemData> items) {
        this.items = items;
    }

    public List<QuestItemData> getItems() {
        return items;
    }

    @Override
    public boolean check(Locked<User> locked) {
        final User user = locked.get();
        final Set<QuestItemData> filteredItems = getFilteredItems(user.getGender(), user.getJob());
        for (QuestItemData itemData : filteredItems) {
            final boolean hasItem = user.getInventoryManager().hasItem(itemData.getItemId(), itemData.getCount());
            if (itemData.getCount() <= 0 && hasItem) {
                // Should not have item
                return false;
            }
            if (itemData.getCount() > 0 && !hasItem) {
                // Should have item
                return false;
            }
        }
        return true;
    }

    private Set<QuestItemData> getFilteredItems(int gender, int job) {
        return items.stream()
                .filter(itemData -> itemData.checkGender(gender) && itemData.checkJob(job))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static QuestItemCheck from(WzListProperty itemList) {
        final List<QuestItemData> items = QuestItemData.resolveItemData(itemList, 0);
        return new QuestItemCheck(
                Collections.unmodifiableList(items)
        );
    }
}
