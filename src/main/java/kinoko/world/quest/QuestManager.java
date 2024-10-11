package kinoko.world.quest;

import kinoko.provider.EtcProvider;
import kinoko.util.TimeUtil;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class QuestManager {
    private final Map<Integer, QuestRecord> questRecords = new HashMap<>();

    public List<QuestRecord> getQuestRecords() {
        return questRecords.values().stream().toList();
    }

    public List<QuestRecord> getStartedQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getState() == QuestState.PERFORM)
                .toList();
    }

    public List<QuestRecord> getCompletedQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getState() == QuestState.COMPLETE)
                .toList();
    }

    public List<QuestRecord> getTitleQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getState() == QuestState.COMPLETE && EtcProvider.isTitleQuest(qr.getQuestId()))
                .toList();
    }

    public List<QuestRecord> getExQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getState() == QuestState.PARTYQUEST)
                .toList();
    }

    public void addQuestRecord(QuestRecord questRecord) {
        questRecords.put(questRecord.getQuestId(), questRecord);
    }

    public Optional<QuestRecord> removeQuestRecord(int questId) {
        return Optional.ofNullable(questRecords.remove(questId));
    }

    public Optional<QuestRecord> getQuestRecord(int questId) {
        return Optional.ofNullable(questRecords.get(questId));
    }

    public boolean hasQuestStarted(int questId) {
        final QuestRecord qr = questRecords.get(questId);
        return qr != null && qr.getState() == QuestState.PERFORM;
    }

    public boolean hasQuestCompleted(int questId) {
        final QuestRecord qr = questRecords.get(questId);
        return qr != null && qr.getState() == QuestState.COMPLETE;
    }

    public QuestRecord forceStartQuest(int questId) {
        final QuestRecord qr = new QuestRecord(questId);
        qr.setState(QuestState.PERFORM);
        addQuestRecord(qr);
        return qr;
    }

    public QuestRecord forceCompleteQuest(int questId) {
        final QuestRecord qr = questRecords.getOrDefault(questId, new QuestRecord(questId));
        qr.setState(QuestState.COMPLETE);
        qr.setCompletedTime(TimeUtil.getCurrentTime());
        addQuestRecord(qr);
        return qr;
    }

    public QuestRecord setQuestInfoEx(int questId, String value) {
        final QuestRecord qr = new QuestRecord(questId);
        qr.setState(QuestState.PERFORM);
        qr.setValue(value);
        addQuestRecord(qr);
        return qr;
    }
}
