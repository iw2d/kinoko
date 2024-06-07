package kinoko.world.quest;

public enum QuestResultType {
    // QuestRes
    Start_QuestTimer(6),
    End_QuestTimer(7),
    Start_TimeKeepQuestTimer(8),
    End_TimeKeepQuestTimer(9),

    // QuestRes_Act
    Success(10),
    Failed_Unknown(11),
    Failed_Inventory(12),
    Failed_Meso(13),
    Failed_Pet(14),
    Failed_Euipped(15),
    Failed_OnlyItem(16),
    Failed_TimeOver(17),
    Reset_QuestTimer(18);

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
