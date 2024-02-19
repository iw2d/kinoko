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
                .filter(qr -> qr.getQuestState() == QuestState.PERFORM)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<QuestRecord> getCompletedQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getQuestState() == QuestState.COMPLETE)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<QuestRecord> getExQuests() {
        return questRecords.values().stream()
                .filter(qr -> qr.getQuestState() == QuestState.PARTYQUEST)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void addQuestRecord(QuestRecord questRecord) {
        questRecords.put(questRecord.getQuestId(), questRecord);
    }

    public Optional<QuestRecord> getQuestRecord(int questId) {
        return Optional.ofNullable(questRecords.get(questId));
    }

    public Set<QuestRecord> getQuestRecords() {
        return questRecords.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Optional<QuestRecord> startQuest(int questId) {
        final Optional<QuestRecord> questRecordResult = QuestRecord.createById(questId);
        if (questRecordResult.isEmpty()) {
            return Optional.empty();
        }
        addQuestRecord(questRecordResult.get());
        return questRecordResult;
    }

    public Optional<QuestRecord> completeQuest(int questId) {
        final QuestRecord questRecord = questRecords.get(questId);
        if (questRecord == null) {
            return Optional.empty();
        }
        questRecord.setQuestState(QuestState.COMPLETE);
        questRecord.setCompletedTime(Instant.now());
        return Optional.of(questRecord);
    }

    public Optional<QuestRecord> resignQuest(int questId) {
        final QuestRecord questRecord = questRecords.get(questId);
        if (questRecord == null || questRecord.getQuestState() != QuestState.PERFORM) {
            return Optional.empty();
        }
        questRecords.remove(questId);
        questRecord.setQuestState(QuestState.NONE);
        return Optional.of(questRecord);
    }
}
