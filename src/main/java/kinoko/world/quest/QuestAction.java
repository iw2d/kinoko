package kinoko.world.quest;

public enum QuestAction {
    // QuestReq
    LOST_ITEM(0),
    ACCEPT_QUEST(1),
    COMPLETE_QUEST(2),
    RESIGN_QUEST(3),
    OPENING_SCRIPT(4),
    COMPLETE_SCRIPT(5),

    // QuestRes
    START_QUEST_TIMER(6),
    END_QUEST_TIMER(7),
    START_TIME_KEEP_QUEST_TIMER(8),
    END_TIME_KEEP_QUEST_TIMER(9),

    // QuestRes_Act
    SUCCESS(10),
    FAILED_UNKNOWN(11),
    FAILED_INVENTORY(11),
    FAILED_MESO(13),
    FAILED_EQUIPPED(15),
    FAILED_ONLY_ITEM(16),
    FAILED_TIME_OVER(17),
    RESET_QUEST_TIMER(18);

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
