package kinoko.provider.quest;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;
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

    public int getGender() {
        return gender;
    }

    public int getJob() {
        return job;
    }

    public int getJobEx() {
        return jobEx;
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
        // CQuest::LoadReward
        final long myJobFlag;
        if (jobId == Job.EVAN_BEGINNER.getJobId()) {
            myJobFlag = 0x20000L;
        } else {
            myJobFlag = 1L << (jobId / 100);
        }
        int job = this.job;
        int jobEx = this.jobEx;
        if ((job | jobEx) == 0) {
            job = -1;
            jobEx = -1;
        }
        return (((myJobFlag >>> 32) & jobEx) | myJobFlag & job) != 0 || jobId / 100 == 9;
    }

    public boolean checkGender(int gender) {
        return this.gender == 2 || this.gender == gender;
    }

    public static List<QuestItemData> resolveItemData(WzProperty itemList) throws ProviderError {
        final List<QuestItemData> items = new ArrayList<>();
        for (var itemEntry : itemList.getItems().entrySet()) {
            if (!(itemEntry.getValue() instanceof WzProperty itemProp)) {
                throw new ProviderError("Failed to resolve quest item list");
            }
            items.add(QuestItemData.from(itemProp));
        }
        return items;
    }

    public static List<QuestItemData> resolveChoiceItemData(WzProperty itemList) throws ProviderError {
        final List<QuestItemData> items = new ArrayList<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!(itemList.get(String.valueOf(i)) instanceof WzProperty itemProp)) {
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

    private static QuestItemData from(WzProperty itemProp) throws ProviderError {
        return new QuestItemData(
                WzProvider.getInteger(itemProp.get("id")),
                WzProvider.getInteger(itemProp.get("count"), 0),
                WzProvider.getInteger(itemProp.get("prop"), 0),
                WzProvider.getInteger(itemProp.get("gender"), 2),
                WzProvider.getInteger(itemProp.get("job"), 0),
                WzProvider.getInteger(itemProp.get("jobEx"), 0),
                WzProvider.getInteger(itemProp.get("resignRemove"), 0) != 0
        );
    }
}
