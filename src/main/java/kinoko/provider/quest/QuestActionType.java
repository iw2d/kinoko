package kinoko.provider.quest;

import java.util.HashMap;
import java.util.Map;

public enum QuestActionType {
    item,
    money,
    exp,
    npc,
    job,
    skill,
    lvmin,
    interval,
    quest;

    private static final Map<String, QuestActionType> nameMap = new HashMap<>();

    static {
        for (QuestActionType type : values()) {
            nameMap.put(type.name(), type);
        }
    }

    public static boolean isIgnored(String name) {
        return !nameMap.containsKey(name);
    }

    public static QuestActionType fromName(String name) {
        return nameMap.get(name);
    }
}
