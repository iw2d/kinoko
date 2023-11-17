package kinoko.provider.quest;

import kinoko.provider.wz.property.WzListProperty;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record QuestInfo(int id, boolean autoStart, boolean autoComplete, Set<QuestAct> startActs, Set<QuestAct> completeActs,
                        Set<QuestCheck> startChecks, Set<QuestCheck> completeChecks) {
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
        return new QuestInfo(
                questId,
                autoStart,
                autoComplete,
                Collections.unmodifiableSet(resolveQuestActs(questAct.get("0"))),
                Collections.unmodifiableSet(resolveQuestActs(questAct.get("1"))),
                Collections.unmodifiableSet(resolveQuestChecks(questAct.get("0"))),
                Collections.unmodifiableSet(resolveQuestChecks(questAct.get("1")))
        );
    }

    private static Set<QuestAct> resolveQuestActs(WzListProperty actProps) {
        final Set<QuestAct> questActs = new HashSet<>();
        for (var entry : actProps.getItems().entrySet()) {
            // TODO
        }
        return questActs;
    }

    private static Set<QuestCheck> resolveQuestChecks(WzListProperty checkProps) {
        final Set<QuestCheck> questChecks = new HashSet<>();
        for (var entry : checkProps.getItems().entrySet()) {
            // TODO
        }
        return questChecks;
    }
}
