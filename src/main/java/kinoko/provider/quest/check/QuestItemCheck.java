package kinoko.provider.quest.check;

import kinoko.provider.quest.QuestItemData;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Locked;
import kinoko.world.user.User;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class QuestItemCheck implements QuestCheck {
    private final Set<QuestItemData> items;

    public QuestItemCheck(Set<QuestItemData> items) {
        this.items = items;
    }

    @Override
    public boolean check(Locked<User> locked) {
        final User user = locked.get();
        final Set<QuestItemData> filteredItems = getFilteredItems(user.getCharacterStat().getGender(), user.getCharacterStat().getJob());
        for (QuestItemData itemData : filteredItems) {
            if (!user.getInventoryManager().hasItem(itemData.getItemId(), itemData.getCount())) {
                return false;
            }
        }
        return true;
    }

    private Set<QuestItemData> getFilteredItems(byte gender, short job) {
        return items.stream()
                .filter(itemData -> itemData.checkGender(gender) && itemData.checkJob(job))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static QuestItemCheck from(WzListProperty itemList) {
        final Set<QuestItemData> items = QuestItemData.resolveItemData(itemList);
        return new QuestItemCheck(
                Collections.unmodifiableSet(items)
        );
    }
}
