package kinoko.world.quest;

public enum QuestResultType {
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

    QuestResultType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }

    public static QuestResultType getByValue(int value) {
        for (QuestResultType action : values()) {
            if (action.getValue() == value) {
                return action;
            }
        }
        return null;
    }
}
