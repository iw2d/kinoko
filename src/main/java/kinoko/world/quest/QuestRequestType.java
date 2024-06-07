package kinoko.world.quest;

public enum QuestRequestType {
    // QuestReq
    LostItem(0),
    AcceptQuest(1),
    CompleteQuest(2),
    ResignQuest(3),
    OpeningScript(4),
    CompleteScript(5);

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
