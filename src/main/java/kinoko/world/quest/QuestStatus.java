package kinoko.world.quest;

public enum QuestStatus {
    NOT_STARTED(0),
    STARTED(1),
    COMPLETED(2),
    EX(3);

    private final int value;

    QuestStatus(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
