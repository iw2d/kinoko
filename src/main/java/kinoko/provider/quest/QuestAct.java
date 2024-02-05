package kinoko.provider.quest;

public abstract class QuestAct {
    private final QuestActionType type;

    public QuestAct(QuestActionType type) {
        this.type = type;
    }
}
