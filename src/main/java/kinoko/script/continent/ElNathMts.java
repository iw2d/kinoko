package kinoko.script.continent;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.quest.QuestRecordType;

import java.util.List;
import java.util.Map;

public final class ElNathMts extends ScriptHandler {
    @Script("getAboard")
    public static void getAboard(ScriptManager sm) {
        // Isa the Station Guide : Platform Usher (2012006)
        //   Orbis : Orbis Station Entrance (200000100)
        final List<Tuple<Integer, String>> platforms = List.of(
                Tuple.of(200000111, "Platform to Board a Ship to Victoria Island"),
                Tuple.of(200000121, "Platform to Board a Ship to Ludibrium"),
                Tuple.of(200000131, "Platform to Board a Ship to Leafre"),
                Tuple.of(200000141, "Platform to Ride a Crane to Mu Lung"),
                Tuple.of(200000151, "Platform to Ride a Genie to Ariant"),
                Tuple.of(200000161, "Platform to Board a Ship to Ereve"),
                Tuple.of(200000170, "Platform to Board a Ship to Edelstein")
        );
        final Map<Integer, String> options = createOptions(platforms, Tuple::getRight);
        final int answer = sm.askMenu("There are many Platforms at the Orbis Station. You must find the correct Platform for your destination. Which Platform would you like to go to?", options);
        if (answer >= 0 && answer < platforms.size()) {
            final int mapId = platforms.get(answer).getLeft();
            final String platform = platforms.get(answer).getRight();
            if (sm.askYesNo(String.format("Even if you took the wrong passage you can get back here using the portal, so no worries. Will you move to the #b%s#k?", platform))) {
                sm.warp(mapId, "west00");
            }
        }
    }

    @Script("station_in")
    public static void station_in(ScriptManager sm) {
        // Orbis : Orbis Station Entrance (200000100)
        //   east00 (1219, 86)
        getAboard(sm);
    }

    @Script("oBoxItem0")
    public static void oBoxItem0(ScriptManager sm) {
        // oBoxItem0 (2002000)
        //   Orbis : Orbis (200000000)
        //   Orbis : Orbis Park (200000200)
        sm.dropRewards(List.of(
                Reward.money(20, 20, 0.7),
                Reward.item(2000000, 1, 1, 0.1), // Red Potion
                Reward.item(2000001, 1, 1, 0.1), // Orange Potion
                Reward.item(2010000, 1, 1, 0.1), // Apple
                Reward.item(4031198, 1, 1, 0.8, 3043) // Empty Potion Bottle
        ));
    }


    // AQUA ROAD SCRIPTS -----------------------------------------------------------------------------------------------

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

    @Script("aquaItem3")
    public static void aquaItem3(ScriptManager sm) {
        // aquaItem3 (2302006)
        //   Aqua Road : Ocean I.C (230010000)
        //   Aqua Road : Crystal Gorge (230010100)
        //   Aqua Road : Red Coral Forest (230010200)
        //   Aqua Road : Turban Shell Hill (230010300)
        //   Aqua Road : Forked Road : West Sea (230010400)
        //   Aqua Road : Forked Road : East Sea (230020000)
        sm.dropRewards(List.of(
                Reward.item(4032476, 1, 1, 0.2, 22407) // Captain Alpha's Buckle
        ));
    }
}
