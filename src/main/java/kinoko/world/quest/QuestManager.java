package kinoko.world.quest;

import kinoko.provider.QuestProvider;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.quest.act.QuestAct;
import kinoko.provider.quest.check.QuestCheck;
import kinoko.util.Locked;
import kinoko.world.user.User;

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

    public Optional<QuestRecord> getQuestRecord(int questId) {
        return Optional.ofNullable(questRecords.get(questId));
    }

    public Set<QuestRecord> getQuestRecords() {
        return questRecords.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Optional<QuestRecord> startQuest(Locked<User> locked, int questId) {
        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            return Optional.empty();
        }
        final QuestInfo qi = questInfoResult.get();

        // Check that the quest can be started
        for (QuestCheck startCheck : qi.getStartChecks()) {
            if (!startCheck.check(locked)) {
                return Optional.empty();
            }
        }
        for (QuestAct startAct : qi.getStartActs()) {
            if (!startAct.canAct(locked)) {
                return Optional.empty();
            }
        }

        // Perform start acts
        for (QuestAct startAct : qi.getStartActs()) {
            if (!startAct.doAct(locked)) {
                throw new IllegalStateException("Failed to perform quest start act");
            }
        }

        // Add quest record and return
        final QuestRecord qr = new QuestRecord(qi.getQuestId());
        qr.setState(QuestState.PERFORM);
        addQuestRecord(qr);
        return Optional.of(qr);
    }

    public Optional<QuestRecord> completeQuest(Locked<User> locked, int questId) {
        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            return Optional.empty();
        }
        final QuestInfo qi = questInfoResult.get();

        // Check that the quest has been started
        final QuestRecord qr = questRecords.get(questId);
        if (qr == null || qr.getState() != QuestState.PERFORM) {
            return Optional.empty();
        }

        // Check that the quest can be completed
        for (QuestCheck completeCheck : qi.getCompleteChecks()) {
            if (!completeCheck.check(locked)) {
                return Optional.empty();
            }
        }
        for (QuestAct completeAct : qi.getCompleteActs()) {
            if (!completeAct.canAct(locked)) {
                return Optional.empty();
            }
        }

        // Perform complete acts
        for (QuestAct completeAct : qi.getCompleteActs()) {
            if (!completeAct.doAct(locked)) {
                throw new IllegalStateException("Failed to perform quest complete act");
            }
        }

        // Mark as completed and return
        qr.setState(QuestState.COMPLETE);
        qr.setCompletedTime(Instant.now());
        return Optional.of(qr);
    }

    public Optional<QuestRecord> resignQuest(int questId) {
        final QuestRecord questRecord = questRecords.get(questId);
        if (questRecord == null || questRecord.getState() != QuestState.PERFORM) {
            return Optional.empty();
        }
        // TODO: resignRemove
        questRecords.remove(questId);
        questRecord.setState(QuestState.NONE);
        return Optional.of(questRecord);
    }
}
