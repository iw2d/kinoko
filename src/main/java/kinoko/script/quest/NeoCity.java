package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

import java.util.List;
import java.util.Map;

public final class NeoCity extends ScriptHandler {
    @Script("TD_NC_title")
    public static void TD_NC_title(ScriptManager sm) {
        // Tera Forest   : Tera Forest Time Gate (240070000)
        // Neo City : <Year 2021> Average Town Entrance (240070100)
        // Neo City : <Year 2099> Midnight Harbor Entrance (240070200)
        // Neo City : <Year 2215> Bombed City Center Retail District (240070300)
        // Neo City : <Year 2216> Ruined City Intersection (240070400)
        // Neo City : <Year 2230> Dangerous Tower Lobby (240070500)
        // Neo City : <Year 2503> Air Battleship Bow (240070600)
        if (sm.getFieldId() == 240070000) {
            // Tera Forest : Tera Forest Time Gate
            sm.screenEffect("temaD/enter/teraForest");
        } else if (sm.getFieldId() == 240070100) {
            // Neo City : <Year 2012> Average Town Entrance
            sm.screenEffect("temaD/enter/neoCity1");
        } else if (sm.getFieldId() == 240070200) {
            // Neo City : <Year 2099> Midnight Harbor Entrance
            sm.screenEffect("temaD/enter/neoCity2");
        } else if (sm.getFieldId() == 240070300) {
            // Neo City : <Year 2215> Bombed City Center Retail District
            sm.screenEffect("temaD/enter/neoCity3");
        } else if (sm.getFieldId() == 240070400) {
            // Neo City : <Year 2216> Ruined City Intersection
            sm.screenEffect("temaD/enter/neoCity4");
        } else if (sm.getFieldId() == 240070500) {
            // Neo City : <Year 2230> Dangerous Tower Lobby
            sm.screenEffect("temaD/enter/neoCity5");
        } else if (sm.getFieldId() == 240070600) {
            // Neo City : <Year 2503> Air Battleship Bow
            sm.screenEffect("temaD/enter/neoCity6");
        }
    }

    @Script("TD_neoCity_enter")
    public static void TD_neoCity_enter(ScriptManager sm) {
        // Time Gate (2083006)
        //   Tera Forest   : Tera Forest Time Gate (240070000)
        final List<Integer> destinations = List.of(
                240070100, // Neo City : <Year 2012> Average Town Entrance
                240070200, // Neo City : <Year 2099> Midnight Harbor Entrance
                240070300, // Neo City : <Year 2215> Bombed City Center Retail District
                240070400, // Neo City : <Year 2216> Ruined City Intersection
                240070500, // Neo City : <Year 2230> Dangerous Tower Lobby
                240070600 // Neo City : <Year 2503> Air Battleship Bow
        );
        final Map<Integer, String> options = createOptions(destinations, ScriptHandler::mapName);
        final int answer = sm.askSlideMenu(1, options);
        if (answer >= 0 && answer < destinations.size()) {
            sm.warp(destinations.get(answer), "left00");
        }
    }

    @Script("TD_chat_enter")
    public static void TD_chat_enter(ScriptManager sm) {
        // Tera Forest   : Tera Forest Time Gate (240070000)
        //   TD_neo (491, 151)
        TD_neoCity_enter(sm);
    }
}
