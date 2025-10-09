package kinoko.world.quest;

import java.time.Instant;

public final class QuestRecord {
    private final int questId;
    private QuestState state;
    private String value;
    private Instant completedTime;

    public QuestRecord(int questId) {
        this.questId = questId;
    }

    public QuestRecord(int questId, QuestState state, String value, Instant completedTime) {
        this.questId = questId;
        this.state = state;
        this.value = value;
        this.completedTime = completedTime;
    }


    public int getQuestId() {
        return questId;
    }

    public QuestState getState() {
        return state;
    }

    public void setState(QuestState state) {
        this.state = state;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Instant getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Instant completedTime) {
        this.completedTime = completedTime;
    }
}
