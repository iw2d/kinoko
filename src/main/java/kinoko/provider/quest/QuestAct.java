package kinoko.provider.quest;

public abstract class QuestAct {
    private final QuestActType type;

    public QuestAct(QuestActType type) {
        this.type = type;
    }
}
