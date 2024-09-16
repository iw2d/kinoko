package kinoko.provider.quest;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.world.job.Job;

import java.util.ArrayList;
import java.util.List;

public final class QuestItemData {
    private final int itemId;
    private final int count;
    private final int prop;
    private final int gender;
    private final int job;
    private final int jobEx;
    private final boolean resignRemove;

    public QuestItemData(int itemId, int count, int prop, int gender, int job, int jobEx, boolean resignRemove) {
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
        return prop > 0;
    }

    public boolean isStatic() {
        return prop == 0;
    }

    public boolean isChoice() {
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

    public static List<QuestItemData> resolveItemData(WzListProperty itemList) throws ProviderError {
        final List<QuestItemData> items = new ArrayList<>();
        for (var itemEntry : itemList.getItems().entrySet()) {
            if (!(itemEntry.getValue() instanceof WzListProperty itemProp)) {
                throw new ProviderError("Failed to resolve quest item list");
            }
            items.add(QuestItemData.from(itemProp));
        }
        return items;
    }

    public static List<QuestItemData> resolveChoiceItemData(WzListProperty itemList) throws ProviderError {
        final List<QuestItemData> items = new ArrayList<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!(itemList.get(String.valueOf(i)) instanceof WzListProperty itemProp)) {
                break;
            }
            final int prop = WzProvider.getInteger(itemProp.get("prop"), 0);
            if (prop != -1) {
                continue;
            }
            if (itemProp.get("count") == null) {
                throw new ProviderError("Invalid choice item data");
            }
            items.add(QuestItemData.from(itemProp));
        }
        return items;
    }

    private static QuestItemData from(WzListProperty itemProp) throws ProviderError {
        return new QuestItemData(
                WzProvider.getInteger(itemProp.get("id")),
                WzProvider.getInteger(itemProp.get("count"), 0),
                WzProvider.getInteger(itemProp.get("prop"), 0),
                WzProvider.getInteger(itemProp.get("gender"), 2),
                WzProvider.getInteger(itemProp.get("job"), -1),
                WzProvider.getInteger(itemProp.get("jobEx"), -1),
                WzProvider.getInteger(itemProp.get("resignRemove"), 0) != 0
        );
    }
}
