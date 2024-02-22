package kinoko.world.quest;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class QuestManager {
    private final Map<Integer, QuestRecord> questRecords = new ConcurrentHashMap<>();

    public Set<QuestRecord> getStartedQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getState() == QuestState.PERFORM)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<QuestRecord> getCompletedQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getState() == QuestState.COMPLETE)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<QuestRecord> getExQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getState() == QuestState.PARTYQUEST)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void addQuestRecord(QuestRecord questRecord) {
        questRecords.put(questRecord.getQuestId(), questRecord);
    }

    public boolean removeQuestRecord(QuestRecord questRecord) {
        return questRecords.remove(questRecord.getQuestId(), questRecord);
    }

    public Optional<QuestRecord> getQuestRecord(int questId) {
        return Optional.ofNullable(questRecords.get(questId));
    }

    public Set<QuestRecord> getQuestRecords() {
        return questRecords.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public boolean hasQuestStarted(int questId) {
        final QuestRecord qr = questRecords.get(questId);
        return qr != null && qr.getState() == QuestState.PERFORM;
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
        qr.setCompletedTime(Instant.now());
        addQuestRecord(qr);
        return qr;
    }
}
