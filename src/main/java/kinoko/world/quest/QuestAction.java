package kinoko.world.quest;

public enum QuestAction {
    LOST_ITEM(0),
    ACCEPT_QUEST(1),
    COMPLETE_QUEST(2),
    RESIGN_QUEST(3),
    START_SCRIPT(4),
    COMPLETE_SCRIPT(5);

    private final byte value;

    QuestAction(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }

    public static QuestAction getByValue(int value) {
        for (QuestAction action : values()) {
            if (action.getValue() == value) {
                return action;
            }
        }
        return null;
    }
}
