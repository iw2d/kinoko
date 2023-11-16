package kinoko.provider.quest;

import kinoko.provider.wz.property.WzListProperty;

public record QuestInfo(int id, boolean autoStart, boolean autoComplete) {
    public boolean isAutoAlert() {
        return autoStart || autoComplete;
    }

    public static QuestInfo from(int questId, WzListProperty questInfo, WzListProperty questAct, WzListProperty questCheck) {
        boolean autoStart = false;
        boolean autoComplete = false;
        for (var infoEntry : questInfo.getItems().entrySet()) {
            switch (infoEntry.getKey()) {
                case "autoStart" -> {
                    autoStart = (int) infoEntry.getValue() != 0;
                }
                case "autoComplete" -> {
                    autoComplete = (int) infoEntry.getValue() != 0;
                }
            }
        }
        return new QuestInfo(questId, autoStart, autoComplete);
    }
}
