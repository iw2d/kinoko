package kinoko.provider.quest;

public abstract class QuestAction {
    private final QuestActionType type;

    public QuestAction(QuestActionType type) {
        this.type = type;
    }
}
