package kinoko.provider.quest;

import java.util.HashMap;
import java.util.Map;

public enum QuestActType {
    item,
    money,
    exp,
    npc,
    job,
    skill,
    lvmin,
    interval,
    quest;

    private static final Map<String, QuestActType> nameMap = new HashMap<>();

    static {
        for (QuestActType type : values()) {
            nameMap.put(type.name(), type);
        }
    }

    public static boolean isIgnored(String name) {
        return !nameMap.containsKey(name);
    }

    public static QuestActType fromName(String name) {
        return nameMap.get(name);
    }
}
