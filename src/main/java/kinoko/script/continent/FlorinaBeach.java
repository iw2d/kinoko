package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

import java.util.Map;

public class FlorinaBeach extends ScriptHandler {
    @Script("florina1")
    public static void florina1(ScriptManager sm) {
        // Pison : Tour Guide (1081001)
        //   Florina Beach : A Look-Out Shed Around the Beach (120030000)
        sm.sayNext("So you want to leave #b#m120030000##k? If you want, I can take you back to #b#m120020400##k.");
        if (!sm.askYesNo("Are you sure you want to return to #b#m120020400##k? Alright, we'll have to get going fast. Do you want to head back to #m120020400# now?")) {
            sm.sayOk("You must have some business to take care of here. It's not a bad idea to take some rest at #m120020400# Look at me; I love it here so much that I wound up living here. Hahaha anyway, talk to me when you feel like going back.");
            return;
        }

        sm.warp(120020400, "sp");
    }

    @Script("florina2")
    public static void florina2(ScriptManager sm) {
        // Pason : Tour Guide (1002002)
        //   Beach : White Wave Harbor (120020400)
        // Shuri : Tour Guide (2010005)
        // Nara : Tour Guide (2040048)
        final int answer = sm.askMenu("Have you heard of the beach with a spectacular view of the ocean called #bFlorina Beach#k, located near Lith Harbor? I can take you there right now for either #b1500 mesos#k, or if you have a #bVIP Ticket to Florina Beach#k with you, in which case you'll be there for free.", Map.of(
                0, "#bI'll pay 1500 mesos.",
                1, "I have a VIP Ticket to Florina Beach.",
                2, "What is a VIP Ticket to Florina Beach#k"
        ));

        switch (answer) {
            case 0:
                if(!sm.canAddMoney(-1500)) {
                    sm.sayOk("I think you're lacking mesos. There are many ways to gather up some money, you know, like... selling your armor... defeating monsters... doing quests... you know what I'm talking about.");
                } else {
                    sm.addMoney(-1500);
                    sm.warp(120030000, "st00");
                }
                break;
            case 1:
                if (!sm.askYesNo("So you have a #bVIP Ticket to Florina Beach#k? You can always head over to Florina Beach with that. Alright then, but just be aware that you may be running into some monsters there too. Okay, would you like to head over to Florina Beach right now?")) {
                    sm.sayOk("You must have some business to take care of here. You must be tired from all that traveling and hunting. Go take some rest, and if you feel like changing your mind, then come talk to me.");
                    return;
                }

                if(!sm.hasItem(4031134, 1)) {
                    sm.sayOk("Hmmm, so where exactly is your #bVIP Ticket to Florina Beach#k? Are you sure you have one? Please double-check.");
                    return;
                }

                sm.warp(120030000, "st00");
                break;
            case 2:
                sm.sayNext("You must be curious about a #bVIP Ticket to Florina Beach#k. Haha, that's very understandable. A VIP Ticket to Florina Beach is an item where as long as you have in possession, you may make your way to Florina Beach for free. It's such a rare item that even we had to buy those, but unfortunately I lost mine a few weeks ago during my precious summer break.");
                sm.sayPrev("I came back without it, and it just feels awful not having it. Hopefully someone picked it up and put it somewhere safe. Anyway, this is my story and who knows, you may be able to pick it up and put it to good use. If you have any questions, feel free to ask.");
                break;
        }
    }
}
