package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.quest.QuestRecordType;

import java.util.Map;

public final class AquaRoad extends ScriptHandler {
    @Script("aqua_taxi")
    public static void aqua_taxi(ScriptManager sm) {
        // Dolphin (2060009)
        //   Aquarium : Aquarium (230000000)
        //   Herb Town : Pier on the Beach (251000100)
        if (sm.getFieldId() == 230000000) {
            // Aquarium : Aquarium
            final int answer = sm.askMenu("Oceans are all connected to each other. Place you can't reach by foot can easily reached oversea. How about taking #bDolphin Taxi#k with us today?", Map.of(
                    0, "Go to the Sharp Unknown.",
                    1, "Go to Herb Town.",
                    2, "Go to the Sea of Fog"
            ));
            if (answer == 0) {
                if (sm.askYesNo("There is a fee of 1000 mesos. Would you like to go there now?")) {
                    if (sm.addMoney(-1000)) {
                        sm.warp(230030200); // Aqua Road : The Sharp Unknown
                    } else {
                        sm.sayNext("I don't think you have enough money...");
                    }
                } else {
                    sm.sayOk("OK. If you ever change your mind, please let me know.");
                }
            } else if (answer == 1) {
                if (sm.askYesNo("There is a fee of 10000 mesos. Would you like to go there now?")) {
                    if (sm.addMoney(-10000)) {
                        sm.warp(251000100); // // Herb Town : Pier on the Beach
                    } else {
                        sm.sayNext("I don't think you have enough money...");
                    }
                } else {
                    sm.sayOk("OK. If you ever change your mind, please let me know.");
                }
            } else if (answer == 2) {
                if (sm.askYesNo("Umm... You want to go to the Sea of Fog? I really don't think you should... Well... What I mean is... Do you want to go now?")) {
                    sm.warp(923020000); // Sea of Fog : Shipwrecked Ghost Ship
                    sm.setQRValue(QuestRecordType.UnityPortal, "");
                } else {
                    sm.sayOk("OK. If you ever change your mind, please let me know.");
                }
            }
        } else if (sm.getFieldId() == 251000100) {
            // Herb Town : Pier on the Beach
            final int answer = sm.askMenu("Oceans are all connected to each other. Place you can't reach by foot can easily reached oversea. How about taking #bDolphin Taxi#k with us today?", Map.of(
                    0, "Go to Aquarium."
            ));
            if (answer == 0) {
                if (sm.askYesNo("There is a fee of 10000 mesos. Would you like to go there now?")) {
                    if (sm.addMoney(-10000)) {
                        sm.warp(230000000); // Aquarium : Aquarium
                    } else {
                        sm.sayNext("I don't think you have enough money...");
                    }
                } else {
                    sm.sayOk("OK. If you ever change your mind, please let me know.");
                }
            }
        }
    }

    @Script("Pianus")
    public static void Pianus(ScriptManager sm) {
        // Aqua Road : The Dangerous Cave (230040410)
        //   boss00 (1090, 46)
        sm.playPortalSE();
        sm.warp(230040420, "out00"); // Aqua Road : The Cave of Pianus
    }
}