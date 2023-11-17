package kinoko.packet.script;

public enum QuestResultType {
    ADD_QUEST_TIMER(6),
    REMOVE_QUEST_TIMER(7),
    ADD_TIME_KEEP_QUEST_TIMER(8),
    REMOVE_TIME_KEEP_QUEST_TIMER(9),

    QUEST_SUCCESS(10),
    QUEST_FAILED_UNKNOWN(11),
    QUEST_FAILED_ITEM(11),
    QUEST_FAILED_MESO(13),
    QUEST_FAILED_EQUIPPED(15),
    QUEST_FAILED_ONLY_ITEM(16),
    QUEST_FAILED_TIME_OVER(17),

    RESET_QUEST_TIMER(18);

    private final byte value;

    QuestResultType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }
}
