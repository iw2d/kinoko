package kinoko.world.quest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QuestManager {
    private final Map<Integer, QuestRecord> startedQuests = new ConcurrentHashMap<>();
    private final Map<Integer, QuestRecord> completedQuests = new ConcurrentHashMap<>();
    private final Map<Integer, QuestRecord> exQuests = new ConcurrentHashMap<>();

    public Map<Integer, QuestRecord> getStartedQuests() {
        return startedQuests;
    }

    public Map<Integer, QuestRecord> getCompletedQuests() {
        return completedQuests;
    }

    public Map<Integer, QuestRecord> getExQuests() {
        return exQuests;
    }
}
