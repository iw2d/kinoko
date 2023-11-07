package kinoko.world.quest;

import lombok.Data;

import java.util.Map;

@Data
public final class QuestManager {
    private Map<Integer, QuestRecord> startedQuests;
    private Map<Integer, QuestRecord> completedQuests;
    private Map<Integer, QuestRecord> exQuests;
}
