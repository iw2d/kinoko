package kinoko.world.quest;

import kinoko.util.FileTime;

public final class QuestRecord {
    private final int questId;
    private QuestStatus questStatus;
    private String questInfo;
    private FileTime completedTime;


    public QuestRecord(int questId) {
        this.questId = questId;
    }

    public int getQuestId() {
        return questId;
    }

    public QuestStatus getQuestStatus() {
        return questStatus;
    }

    public void setQuestStatus(QuestStatus questStatus) {
        this.questStatus = questStatus;
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
