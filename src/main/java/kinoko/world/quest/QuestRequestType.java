package kinoko.world.quest;

public enum QuestRequestType {
    // QuestReq
    LOST_ITEM(0),
    ACCEPT_QUEST(1),
    COMPLETE_QUEST(2),
    RESIGN_QUEST(3),
    OPENING_SCRIPT(4),
    COMPLETE_SCRIPT(5);

    private final int value;

    QuestRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static QuestRequestType getByValue(int value) {
        for (QuestRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
