package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Resistance Quest System - Additional Quests
 * Covers quests 19000-19999 range for Resistance-related quests
 */
public final class ResistanceQuest2 extends ScriptHandler {

    // RESISTANCE QUEST SCRIPTS
    // q19000-q19012 - Honorable Mesoranger and event quests

    @Script("q19000s")
    public static void q19000s(ScriptManager sm) {
        // Quest 19000 - Honorable Mesoranger (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(19000);
            sm.addItem(1142076, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q19001s")
    public static void q19001s(ScriptManager sm) {
        // Quest 19001 - Maple Nut (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(19001);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q19002s")
    public static void q19002s(ScriptManager sm) {
        // Quest 19002 - The 2nd Honorable Mesoranger (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(19002);
            sm.addItem(1142123, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q19005s")
    public static void q19005s(ScriptManager sm) {
        // Quest 19005 - Top 10 in Artifact Hunt (START)
        // NPC: 9000066
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(19005);
            sm.addItem(1142124, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q19006s")
    public static void q19006s(ScriptManager sm) {
        // Quest 19006 - Mystical Artifact Discoverer (START)
        // NPC: 9000066
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(19006);
            sm.addItem(1142125, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q19011s")
    public static void q19011s(ScriptManager sm) {
        // Quest 19011 - The 3rd Honorable Mesoranger (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(19011);
            sm.addItem(1142170, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q19012s")
    public static void q19012s(ScriptManager sm) {
        // Quest 19012 - Honorable Explorer (START)
        // NPC: 9000066
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(19012);
            sm.addItem(1142184, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }
}
