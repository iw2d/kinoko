package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Tutorial and beginner quest scripts
 * Covers quests 0-1099 range for tutorial and job recommendation quests
 */
public final class TutorialQuest extends ScriptHandler {

    // TUTORIAL QUEST SCRIPTS
    // q0 - Base tutorial quest
    // q1028-q1054 - Job recommendation and beginner quests

    @Script("q0e")
    public static void q0e(ScriptManager sm) {
        // Quest 0 - Tutorial (END)
        // Auto-complete tutorial quest
        sm.forceCompleteQuest(0);
    }

    @Script("q0s")
    public static void q0s(ScriptManager sm) {
        // Quest 0 - Tutorial (START)
        // Auto-start tutorial quest
        sm.forceStartQuest(0);
    }

    @Script("q1028s")
    public static void q1028s(ScriptManager sm) {
        // Quest 1028 - To Lith Harbor! (START)
        // NPC: 22000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(1028);
            sm.addItem(1042003, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q1048s")
    public static void q1048s(ScriptManager sm) {
        // Quest 1048 - Job Recommendation (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(1048);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q1049s")
    public static void q1049s(ScriptManager sm) {
        // Quest 1049 - Becoming a Warrior (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(1049);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q1050s")
    public static void q1050s(ScriptManager sm) {
        // Quest 1050 - Becoming a Magician (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(1050);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q1051s")
    public static void q1051s(ScriptManager sm) {
        // Quest 1051 - Becoming a Bowman (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(1051);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q1052s")
    public static void q1052s(ScriptManager sm) {
        // Quest 1052 - Becoming a Thief (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(1052);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q1053s")
    public static void q1053s(ScriptManager sm) {
        // Quest 1053 - Becoming a Pirate (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(1053);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q1054s")
    public static void q1054s(ScriptManager sm) {
        // Quest 1054 - Cygnus Knights (START)
        // NPC: 1101002
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(1054);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }
}
