package kinoko.provider.quest.check;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Locked;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;
import kinoko.world.user.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class QuestExCheck implements QuestCheck {
    private final int questId;
    private final Set<String> allowedValues;

    public QuestExCheck(int questId, Set<String> allowedValues) {
        this.questId = questId;
        this.allowedValues = allowedValues;
    }

    public int getQuestId() {
        return questId;
    }

    public Set<String> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public boolean check(Locked<User> locked) {
        final Optional<QuestRecord> questRecordResult = locked.get().getQuestManager().getQuestRecord(getQuestId());
        if (questRecordResult.isEmpty()) {
            return false;
        }
        final QuestRecord questRecord = questRecordResult.get();
        return questRecord.getState() == QuestState.PERFORM && getAllowedValues().contains(questRecord.getValue());
    }

    public static QuestCheck from(int questId, WzListProperty exList) throws ProviderError {
        final Set<String> allowedValues = new HashSet<>();
        for (var exEntry : exList.getItems().entrySet()) {
            if (!(exEntry.getValue() instanceof WzListProperty exProp)) {
                throw new ProviderError("Failed to resolve quest ex check value");
            }
            allowedValues.add(WzProvider.getString(exProp.get("value")));
        }
        return new QuestExCheck(questId, Collections.unmodifiableSet(allowedValues));
    }
}
