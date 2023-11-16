package kinoko.provider.quest;

import kinoko.provider.wz.property.WzListProperty;

public record QuestInfo(int id) {
    public static QuestInfo from(int questId, WzListProperty questProp) {
        return new QuestInfo(questId);
    }
}
