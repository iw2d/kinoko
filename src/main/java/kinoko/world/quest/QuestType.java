package kinoko.world.quest;

public enum QuestType {
    NOT_STARTED(0),
    STARTED(1),
    COMPLETED(2),
    EX(3);

    private final int value;

    QuestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
