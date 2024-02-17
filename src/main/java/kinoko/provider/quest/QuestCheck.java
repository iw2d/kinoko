package kinoko.provider.quest;

public abstract class QuestCheck {
    private final QuestCheckType type;

    public QuestCheck(QuestCheckType type) {
        this.type = type;
    }
}
