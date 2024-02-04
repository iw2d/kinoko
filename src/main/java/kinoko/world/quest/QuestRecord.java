package kinoko.world.quest;

import kinoko.util.FileTime;

public final class QuestRecord {
    private final int questId;
    private QuestState questState;
    private String questInfo;
    private FileTime completedTime;

    public QuestRecord(int questId) {
        this.questId = questId;
    }

    public int getQuestId() {
        return questId;
    }

    public QuestState getQuestState() {
        return questState;
    }

    public void setQuestState(QuestState questState) {
        this.questState = questState;
    }

    public String getQuestInfo() {
        return questInfo;
    }

    public void setQuestInfo(String questInfo) {
        this.questInfo = questInfo;
    }

    public FileTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(FileTime completedTime) {
        this.completedTime = completedTime;
    }
}
