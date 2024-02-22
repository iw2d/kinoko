package kinoko.world.quest;

import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
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

    public boolean startQuest(Locked<User> locked, int questId, int templateId) {
        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            return false;
        }
        final QuestInfo qi = questInfoResult.get();

        // Check that the quest can be started
        for (QuestCheck startCheck : qi.getStartChecks()) {
            if (!startCheck.check(locked)) {
                return false;
            }
        }
        for (QuestAct startAct : qi.getStartActs()) {
            if (!startAct.canAct(locked)) {
                return false;
            }
        }

        // Perform start acts
        for (QuestAct startAct : qi.getStartActs()) {
            if (!startAct.doAct(locked)) {
                throw new IllegalStateException("Failed to perform quest start act");
            }
        }

        // Add quest record and update client
        final QuestRecord qr = new QuestRecord(qi.getQuestId());
        qr.setState(QuestState.PERFORM);
        addQuestRecord(qr);
        final User user = locked.get();
        user.write(WvsContext.message(Message.questRecord(qr)));
        user.write(UserLocal.questResult(QuestResult.success(questId, templateId, 0)));
        return true;
    }

    public QuestRecord forceStartQuest(int questId) {
        final QuestRecord qr = new QuestRecord(questId);
        qr.setState(QuestState.PERFORM);
        addQuestRecord(qr);
        return qr;
    }

    public boolean completeQuest(Locked<User> locked, int questId, int templateId) {
        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            return false;
        }
        final QuestInfo qi = questInfoResult.get();

        // Check that the quest has been started
        final QuestRecord qr = questRecords.get(questId);
        if (qr == null || qr.getState() != QuestState.PERFORM) {
            return false;
        }

        // Check that the quest can be completed
        for (QuestCheck completeCheck : qi.getCompleteChecks()) {
            if (!completeCheck.check(locked)) {
                return false;
            }
        }
        for (QuestAct completeAct : qi.getCompleteActs()) {
            if (!completeAct.canAct(locked)) {
                return false;
            }
        }

        // Perform complete acts
        for (QuestAct completeAct : qi.getCompleteActs()) {
            if (!completeAct.doAct(locked)) {
                throw new IllegalStateException("Failed to perform quest complete act");
            }
        }

        // Mark as completed and update client
        qr.setState(QuestState.COMPLETE);
        qr.setCompletedTime(Instant.now());
        final User user = locked.get();
        user.write(WvsContext.message(Message.questRecord(qr)));
        user.write(UserLocal.questResult(QuestResult.success(questId, templateId, qi.getNextQuest())));
        // Quest complete effect
        user.write(UserLocal.effect(Effect.questComplete()));
        user.getField().broadcastPacket(UserRemote.effect(user, Effect.questComplete()), user);
        return true;
    }

    public QuestRecord forceCompleteQuest(int questId) {
        final QuestRecord qr = new QuestRecord(questId);
        qr.setState(QuestState.COMPLETE);
        qr.setCompletedTime(Instant.now());
        addQuestRecord(qr);
        return qr;
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
