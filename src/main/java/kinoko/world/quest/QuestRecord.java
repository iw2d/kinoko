package kinoko.world.quest;

import kinoko.provider.QuestProvider;
import kinoko.provider.quest.QuestInfo;

import java.time.Instant;
import java.util.Optional;

public final class QuestRecord {
    private final int questId;
    private QuestState questState;
    private String questInfo;
    private Instant completedTime;

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

    public Instant getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Instant completedTime) {
        this.completedTime = completedTime;
    }

    public static Optional<QuestRecord> createById(int questId) {
        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            return Optional.empty();
        }
        final QuestInfo qi = questInfoResult.get();
        final QuestRecord qr = new QuestRecord(questId);
        qr.setQuestState(QuestState.PERFORM);
        return Optional.of(qr);
    }
}
