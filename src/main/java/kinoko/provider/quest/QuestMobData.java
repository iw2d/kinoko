package kinoko.provider.quest;

import kinoko.provider.MobProvider;
import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

import java.util.ArrayList;
import java.util.List;

public final class QuestMobData {
    private final int order;
    private final int mobId;
    private final int count;

    public QuestMobData(int order, int mobId, int count) {
        this.order = order;
        this.mobId = mobId;
        this.count = count;
    }

    public int getOrder() {
        return order;
    }

    public int getMobId() {
        return mobId;
    }

    public int getCount() {
        return count;
    }

    public boolean isMatch(int mobId) {
        return this.mobId == mobId || MobProvider.getQuestCountGroup(this.mobId).contains(mobId);
    }

    public static List<QuestMobData> resolveMobData(WzListProperty mobList) {
        final List<QuestMobData> mobs = new ArrayList<>();
        for (var mobEntry : mobList.getItems().entrySet()) {
            if (!(mobEntry.getValue() instanceof WzListProperty mobProp)) {
                throw new ProviderError("Failed to resolve quest mob list");
            }
            final int order = Integer.parseInt(mobEntry.getKey());
            final int mobId = WzProvider.getInteger(mobProp.get("id"));
            final int count = WzProvider.getInteger(mobProp.get("count"));
            final QuestMobData mobData = new QuestMobData(order, mobId, count);
            mobs.add(mobData);
        }
        for (int i = 0; i < mobs.size() - 1; i++) {
            assert (mobs.get(i).getOrder() < mobs.get(i + 1).getOrder());
        }
        return mobs;
    }
}
