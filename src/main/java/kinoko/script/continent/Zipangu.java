package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

import java.util.Map;

public class Zipangu extends ScriptHandler {
    @Script("con1")
    public static void con1(ScriptManager sm) {
        // Konpei (9120015)
        //   Zipangu : Showa Town (801000000)
        final int answer = sm.askMenu("What do you want from me?", Map.of(
                0, "Gather up some information on the hideout.",
                1, "Take me to the hideout.",
                2, "Nothing."
        ));

        switch (answer) {
            case 0 -> {
                sm.sayNext("I can take you to the hideout, but the place is infested with thugs looking for trouble. You'll need to be both incredibly strong and brave to enter the premise. At the hideaway, you'll find the Boss that controls all the other bosses around this area. It's easy to get to the hideout, but the room on the top floor of the place can only be entered ONCE a day. The Boss's Room is not a place to mess around. I suggest you don't stay there for too long; you'll need to swiftly take care of the business once inside. The boss himself is a difficult foe, but you'll run into some incredibly powerful enemies on your way to meeting the boss! It ain't going to be easy.");
            }
            case 1 -> {
                sm.sayNext("Oh, the brave one. I've been awaiting your arrival. If these thugs are left unchecked, there's no telling what going to happen in this neighborhood. Before that happens, I hope you take care of all of them and beat the boss, who resides on the 5th floor. You'll need to be on alert at all times, since the boss is too tough for even the wisemen to handle. Looking at your eyes, however, I can see that eye of the tiger, the eyes that tell me you can do this. Let's go!");
                sm.warp(801040000);
            }
            case 2 -> {
                sm.sayOk("I'm a busy person! Leave me alone if that's all you need!");
            }
        }
    }

    @Script("con2")
    public static void con2(ScriptManager sm) {
        // Konpei (9120200)
        //   Zipangu : Near the Hideout (801040000)
        if (!sm.askYesNo("Here you are, right in front of the hideout! What? You want to return to Showa Town?")) {
            sm.sayOk("If you want to return to Showa Town, then talk to me.");
            return;
        }

        sm.warp(801000000);
    }

    @Script("con3")
    public static void con3(ScriptManager sm) {
        // Konpei (9120202)
        //   Zipangu : The Nightmarish Last Days (801040100)
        if (!sm.hasItem(4000141)) {
            if (!sm.askYesNo("Once you eliminate the boss, you'll have to show me the boss's flashlight as evidence. I won't believe it until you show me the flashlight! What? You want to leave this room?")) {
                sm.sayOk("I really admire your toughness! Well, if you decide to return to Showa Town, let me know~!");
                return;
            }

            sm.warp(801040000);
        } else {
            sm.message("Boss not implemented yet.");
        }
    }
}
