package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.world.item.InventoryManager;
import kinoko.world.user.User;

import java.util.ArrayList;
import java.util.List;

public final class ItemRewardInfo {
    private final int itemId;
    private final List<ItemRewardEntry> entries;

    public ItemRewardInfo(int itemId, List<ItemRewardEntry> entries) {
        this.itemId = itemId;
        this.entries = entries;
    }

    public int getItemId() {
        return itemId;
    }

    public List<ItemRewardEntry> getEntries() {
        return entries;
    }

    public boolean canAddReward(User user) {
        final InventoryManager im = user.getInventoryManager();
        for (ItemRewardEntry entry : getEntries()) {
            if (!im.canAddItem(entry.getItemId(), entry.getCount())) {
                return false;
            }
        }
        return true;
    }

    public static ItemRewardInfo from(int itemId, WzProperty rewardList) throws ProviderError {
        final List<ItemRewardEntry> entries = new ArrayList<>();
        for (var entry : rewardList.getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzProperty rewardProp)) {
                throw new ProviderError("Failed to resolve reward prop");
            }
            entries.add(new ItemRewardEntry(
                    WzProvider.getInteger(rewardProp.get("item")),
                    WzProvider.getInteger(rewardProp.get("count")),
                    WzProvider.getInteger(rewardProp.get("prob")),
                    WzProvider.getInteger(rewardProp.get("period"), 0),
                    WzProvider.getString(rewardProp.get("Effect"), null)
            ));
        }
        return new ItemRewardInfo(itemId, entries);
    }
}
