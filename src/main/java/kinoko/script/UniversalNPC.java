package kinoko.script;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

import java.util.Map;

/**
 * Universal NPCs that appear across multiple towns
 * Includes event NPCs, seasonal NPCs, and special functionality NPCs
 */
public final class UniversalNPC extends ScriptHandler {

    @Script("cny")
    public static void cny(ScriptManager sm) {
        // Chinese New Year Event NPC
        // Appears in various towns during CNY event
        sm.sayNext("Happy Chinese New Year! I'm here to celebrate with you!");
        sm.sayBoth("Unfortunately, the Chinese New Year event is not currently active.");
        sm.sayBoth("Please check back during the event period for special rewards and festivities!");
    }

    @Script("Event00")
    public static void Event00(ScriptManager sm) {
        // Generic Event NPC (9010038)
        // Used for various seasonal events
        final int answer = sm.askMenu("Hello! I'm an event coordinator. What would you like to know about?", Map.of(
                0, "What events are currently active?",
                1, "Tell me about past events",
                2, "Never mind"
        ));

        if (answer == 0) {
            sm.sayNext("Currently, there are no special events running.");
            sm.sayBoth("Please check back later for new events and special activities!");
        } else if (answer == 1) {
            sm.sayNext("We've had many exciting events in the past!");
            sm.sayBoth("From holiday celebrations to special boss battles, there's always something fun happening in MapleStory.");
            sm.sayBoth("Keep an eye out for announcements about upcoming events!");
        } else {
            sm.sayOk("Feel free to come back anytime if you have questions about events!");
        }
    }

    @Script("GachaponEvent")
    public static void GachaponEvent(ScriptManager sm) {
        // Gachapon Event NPC (9010000)
        // Special gachapon with event-exclusive rewards
        sm.sayNext("Welcome to the special Gachapon event!");

        final int answer = sm.askMenu("What would you like to know?", Map.of(
                0, "What is Gachapon?",
                1, "Are there any special Gachapon events?",
                2, "Where can I find Gachapon machines?"
        ));

        if (answer == 0) {
            sm.sayNext("#bGachapon#k is a special machine where you can insert a #bGachapon Ticket#k and receive a random item!");
            sm.sayBoth("The items you can get range from common equipment to rare scrolls and special prizes.");
            sm.sayBoth("Each town's Gachapon has different items, so try them all!");
        } else if (answer == 1) {
            sm.sayNext("Currently, there are no special Gachapon events running.");
            sm.sayBoth("During events, Gachapon machines may have increased rates for rare items or exclusive event prizes!");
            sm.sayBoth("Check back during special occasions for limited-time rewards.");
        } else if (answer == 2) {
            sm.sayNext("Gachapon machines can be found in most major towns!");
            sm.sayBoth("Look for the colorful Gachapon machines in:");
            sm.sayBoth("#b- Henesys\r\n- Ellinia\r\n- Perion\r\n- Kerning City\r\n- Sleepywood\r\n- Orbis\r\n- El Nath\r\n- Ludibrium\r\n- Aquarium\r\n- Leafre\r\n- Mu Lung\r\n- Herb Town\r\n- Omega Sector\r\n- Korean Folk Town\r\n- Shrine\r\n- Showa#k");
            sm.sayBoth("Each location has different prize pools, so explore them all!");
        }
    }
}
