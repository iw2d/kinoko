package kinoko.script.continent;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;

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
}
