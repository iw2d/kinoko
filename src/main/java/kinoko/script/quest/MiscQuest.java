package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Miscellaneous Quest System
 * Covers various quest ranges (4xxx, 6xxx, 8xxx, 40xxx, 51xxx)
 */
public final class MiscQuest extends ScriptHandler {


    @Script("q40001s")
    public static void q40001s(ScriptManager sm) {
        // Quest 40001 - hohoho (START)
        // NPC: Unknown
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(40001);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q40002s")
    public static void q40002s(ScriptManager sm) {
        // Quest 40002 - hohoho (START)
        // NPC: 11000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(40002);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q40003s")
    public static void q40003s(ScriptManager sm) {
        // Quest 40003 - victor (START)
        // NPC: Unknown
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(40003);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q40005s")
    public static void q40005s(ScriptManager sm) {
        // Quest 40005 - victor5 (START)
        // NPC: Unknown
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(40005);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q40006s")
    public static void q40006s(ScriptManager sm) {
        // Quest 40006 - victor6 (START)
        // NPC: Unknown
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(40006);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q40007e")
    public static void q40007e(ScriptManager sm) {
        // Quest 40007 - victor7 (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(40007);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q40007s")
    public static void q40007s(ScriptManager sm) {
        // Quest 40007 - victor7 (START)
        // NPC: Unknown
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(40007);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q4482e")
    public static void q4482e(ScriptManager sm) {
        // Quest 4482 - Socks Actual Creation Action (END)
        // NPC: 9250051

        final int QUEST_ITEM_4220022 = 4220022;

        if (!sm.hasItem(QUEST_ITEM_4220022, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4220022, 1);
            sm.forceCompleteQuest(4482);
            sm.addExp(5000); // EXP reward
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q4482s")
    public static void q4482s(ScriptManager sm) {
        // Quest 4482 - Socks Actual Creation Action (START)
        // NPC: 9250051
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(4482);
            sm.addItem(4161039, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q4483e")
    public static void q4483e(ScriptManager sm) {
        // Quest 4483 - Socks Hanging (END)
        // NPC: 9250053

        final int QUEST_ITEM_4031885 = 4031885;

        if (!sm.hasItem(QUEST_ITEM_4031885, 4)) {
            sm.sayOk("You need 4 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031885, 4);
            sm.forceCompleteQuest(4483);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q4490e")
    public static void q4490e(ScriptManager sm) {
        // Quest 4490 - Bicho's snowman (END)
        // NPC: 9250054
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(4490);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q4490s")
    public static void q4490s(ScriptManager sm) {
        // Quest 4490 - Bicho's snowman (START)
        // NPC: 9250054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(4490);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q4647e")
    public static void q4647e(ScriptManager sm) {
        // Quest 4647 - The Secret Method (END)
        // NPC: 1012006
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(4647);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q4659e")
    public static void q4659e(ScriptManager sm) {
        // Quest 4659 - Robo Upgrade! (END)
        // NPC: Unknown

        final int QUEST_ITEM_5380000 = 5380000;
        final int QUEST_ITEM_4000111 = 4000111;

        if (!sm.hasItem(QUEST_ITEM_5380000, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4000111, 50)) {
            sm.sayOk("You need 50 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_5380000, 1);
            sm.removeItem(QUEST_ITEM_4000111, 50);
            sm.forceCompleteQuest(4659);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51180e")
    public static void q51180e(ScriptManager sm) {
        // Quest 51180 - Building a Time Machine (END)
        // NPC: 9250125

        final int QUEST_ITEM_4032791 = 4032791;
        final int QUEST_ITEM_4032792 = 4032792;
        final int QUEST_ITEM_4032793 = 4032793;
        final int QUEST_ITEM_4032794 = 4032794;
        final int QUEST_ITEM_4032795 = 4032795;
        final int QUEST_ITEM_4032796 = 4032796;

        if (!sm.hasItem(QUEST_ITEM_4032791, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032792, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032793, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032794, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032795, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032796, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032791, 1);
            sm.removeItem(QUEST_ITEM_4032792, 1);
            sm.removeItem(QUEST_ITEM_4032793, 1);
            sm.removeItem(QUEST_ITEM_4032794, 1);
            sm.removeItem(QUEST_ITEM_4032795, 1);
            sm.removeItem(QUEST_ITEM_4032796, 1);
            sm.forceCompleteQuest(51180);
            sm.addExp(1); // EXP reward
            sm.addItem(4032707, 1); // Reward item
            sm.addItem(4032791, 1); // Reward item
            sm.addItem(4032792, 1); // Reward item
            sm.addItem(4032793, 1); // Reward item
            sm.addItem(4032794, 1); // Reward item
            sm.addItem(4032795, 1); // Reward item
            sm.addItem(4032796, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51199e")
    public static void q51199e(ScriptManager sm) {
        // Quest 51199 - Many a Mickle Makes a Muckle (END)
        // NPC: 9010000

        final int QUEST_ITEM_3994199 = 3994199;

        if (!sm.hasItem(QUEST_ITEM_3994199, 9)) {
            sm.sayOk("You need 9 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3994199, 9);
            sm.forceCompleteQuest(51199);
            sm.addItem(3994200, 1); // Reward item
            sm.addItem(3994201, 1); // Reward item
            sm.addItem(3994202, 1); // Reward item
            sm.addItem(3994203, 1); // Reward item
            sm.addItem(3994204, 1); // Reward item
            sm.addItem(3994205, 1); // Reward item
            sm.addItem(3994206, 1); // Reward item
            sm.addItem(3994207, 1); // Reward item
            sm.addItem(3994208, 1); // Reward item
            sm.addItem(3994199, -20); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51207e")
    public static void q51207e(ScriptManager sm) {
        // Quest 51207 - OSSS Mission 1: Alien Elimination (END)
        // NPC: 9250133
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(51207);
            sm.addItem(4032714, 1); // Reward item
            sm.addItem(4032718, 1); // Reward item
            sm.addItem(4310004, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51208e")
    public static void q51208e(ScriptManager sm) {
        // Quest 51208 - OSSS Mission 2: Protecting the Scientists (END)
        // NPC: 9250133
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(51208);
            sm.addItem(4032715, 1); // Reward item
            sm.addItem(4032719, 1); // Reward item
            sm.addItem(4310004, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51209e")
    public static void q51209e(ScriptManager sm) {
        // Quest 51209 - OSSS Mission 3:  Alien Photo (END)
        // NPC: 9250133

        final int QUEST_ITEM_4032713 = 4032713;

        if (!sm.hasItem(QUEST_ITEM_4032713, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032713, 3);
            sm.forceCompleteQuest(51209);
            sm.addItem(4032716, 1); // Reward item
            sm.addItem(4032720, 1); // Reward item
            sm.addItem(4032713, 1); // Reward item
            sm.addItem(4310004, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51210e")
    public static void q51210e(ScriptManager sm) {
        // Quest 51210 - OSSS Mission 4: Retrieving Alien Chips and Spy Cameras (END)
        // NPC: 9250133

        final int QUEST_ITEM_4032711 = 4032711;
        final int QUEST_ITEM_4032712 = 4032712;

        if (!sm.hasItem(QUEST_ITEM_4032711, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032712, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032711, 30);
            sm.removeItem(QUEST_ITEM_4032712, 30);
            sm.forceCompleteQuest(51210);
            sm.addItem(4032717, 1); // Reward item
            sm.addItem(4032721, 1); // Reward item
            sm.addItem(4032711, 1); // Reward item
            sm.addItem(4032712, 1); // Reward item
            sm.addItem(4310004, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51211e")
    public static void q51211e(ScriptManager sm) {
        // Quest 51211 - Talk to Principal Researcher Bass (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(51211);
            sm.addExp(1); // EXP reward
            sm.addItem(4310004, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51212e")
    public static void q51212e(ScriptManager sm) {
        // Quest 51212 - United Against Alien Scum! (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(51212);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51212s")
    public static void q51212s(ScriptManager sm) {
        // Quest 51212 - United Against Alien Scum! (START)
        // NPC: 9250134
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(51212);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q51214e")
    public static void q51214e(ScriptManager sm) {
        // Quest 51214 - OSSS Mission Issued (END)
        // NPC: 9250133

        final int QUEST_ITEM_4032718 = 4032718;
        final int QUEST_ITEM_4032719 = 4032719;
        final int QUEST_ITEM_4032720 = 4032720;
        final int QUEST_ITEM_4032721 = 4032721;

        if (!sm.hasItem(QUEST_ITEM_4032718, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032719, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032720, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032721, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032718, 1);
            sm.removeItem(QUEST_ITEM_4032719, 1);
            sm.removeItem(QUEST_ITEM_4032720, 1);
            sm.removeItem(QUEST_ITEM_4032721, 1);
            sm.forceCompleteQuest(51214);
            sm.addItem(4032718, 1); // Reward item
            sm.addItem(4032719, 1); // Reward item
            sm.addItem(4032720, 1); // Reward item
            sm.addItem(4032721, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51216e")
    public static void q51216e(ScriptManager sm) {
        // Quest 51216 - Clearing Party Quests (END)
        // NPC: 9250134
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(51216);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51216s")
    public static void q51216s(ScriptManager sm) {
        // Quest 51216 - Clearing Party Quests (START)
        // NPC: 9250134
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(51216);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q51239e")
    public static void q51239e(ScriptManager sm) {
        // Quest 51239 - OSSS Mission 5: Infiltrating the Mothership (END)
        // NPC: 9250146
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(51239);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51240e")
    public static void q51240e(ScriptManager sm) {
        // Quest 51240 - OSSS Mission 5: Infiltrating the Mothership (END)
        // NPC: 9250146
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(51240);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q51241e")
    public static void q51241e(ScriptManager sm) {
        // Quest 51241 - OSSS Mission 5: Infiltrating the Mothership (END)
        // NPC: 9250146
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(51241);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q6030e")
    public static void q6030e(ScriptManager sm) {
        // Quest 6030 - Carson's Fundamentals of Alchemy (END)
        // NPC: 2111000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(6030);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q6031e")
    public static void q6031e(ScriptManager sm) {
        // Quest 6031 - Hughes the Fuse's Basic Theory of Science (END)
        // NPC: 2012017
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(6031);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q6032e")
    public static void q6032e(ScriptManager sm) {
        // Quest 6032 - Moren's Class on the Actual Practice (END)
        // NPC: 2110004
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(6032);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q6033e")
    public static void q6033e(ScriptManager sm) {
        // Quest 6033 - Moren's Second Round of Teaching (END)
        // NPC: 2110004

        final int QUEST_ITEM_4260003 = 4260003;

        if (!sm.hasItem(QUEST_ITEM_4260003, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4260003, 1);
            sm.forceCompleteQuest(6033);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q6036e")
    public static void q6036e(ScriptManager sm) {
        // Quest 6036 - A Surprise Outcome (END)
        // NPC: 2110004

        final int QUEST_ITEM_4031980 = 4031980;

        if (!sm.hasItem(QUEST_ITEM_4031980, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031980, 1);
            sm.forceCompleteQuest(6036);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q6700e")
    public static void q6700e(ScriptManager sm) {
        // Quest 6700 - The Bowman's Road (END)
        // NPC: 1012100
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(6700);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8185e")
    public static void q8185e(ScriptManager sm) {
        // Quest 8185 - Pet's Evolution2 (END)
        // NPC: Unknown

        final int QUEST_ITEM_5380000 = 5380000;

        if (!sm.hasItem(QUEST_ITEM_5380000, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_5380000, 1);
            sm.forceCompleteQuest(8185);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8189e")
    public static void q8189e(ScriptManager sm) {
        // Quest 8189 - Pet's Re-Evolution (END)
        // NPC: Unknown

        final int QUEST_ITEM_5380000 = 5380000;

        if (!sm.hasItem(QUEST_ITEM_5380000, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_5380000, 1);
            sm.forceCompleteQuest(8189);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8219e")
    public static void q8219e(ScriptManager sm) {
        // Quest 8219 - Finding Jack (END)
        // NPC: 9201096
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(8219);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8219s")
    public static void q8219s(ScriptManager sm) {
        // Quest 8219 - Finding Jack (START)
        // NPC: 9201051
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8219);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8221s")
    public static void q8221s(ScriptManager sm) {
        // Quest 8221 - The Mark of Heroism (START)
        // NPC: 9201051
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8221);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8222e")
    public static void q8222e(ScriptManager sm) {
        // Quest 8222 - The Brewing Storm (END)
        // NPC: 9201098

        final int QUEST_ITEM_4032006 = 4032006;

        if (!sm.hasItem(QUEST_ITEM_4032006, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032006, 10);
            sm.forceCompleteQuest(8222);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8222s")
    public static void q8222s(ScriptManager sm) {
        // Quest 8222 - The Brewing Storm (START)
        // NPC: 9201098
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8222);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8223s")
    public static void q8223s(ScriptManager sm) {
        // Quest 8223 - Storming the Castle (START)
        // NPC: 9201098
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8223);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8224s")
    public static void q8224s(ScriptManager sm) {
        // Quest 8224 - The Fallen Woods (START)
        // NPC: 9201100
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8224);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8225s")
    public static void q8225s(ScriptManager sm) {
        // Quest 8225 - The Right Path (START)
        // NPC: 9201100
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8225);
            sm.addItem(3992040, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8226s")
    public static void q8226s(ScriptManager sm) {
        // Quest 8226 - The Fallen Warriors (START)
        // NPC: 9201100
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8226);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8227s")
    public static void q8227s(ScriptManager sm) {
        // Quest 8227 - Lost in Translation 1 (START)
        // NPC: 9201096
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8227);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8228e")
    public static void q8228e(ScriptManager sm) {
        // Quest 8228 - Lost in Translation 2 (END)
        // NPC: 9201055

        final int QUEST_ITEM_4032018 = 4032018;

        if (!sm.hasItem(QUEST_ITEM_4032018, 0)) {
            sm.sayOk("You need 0 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032018, 0);
            sm.forceCompleteQuest(8228);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8228s")
    public static void q8228s(ScriptManager sm) {
        // Quest 8228 - Lost in Translation 2 (START)
        // NPC: 9201051
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8228);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8229e")
    public static void q8229e(ScriptManager sm) {
        // Quest 8229 - Lost in Translation 3 (END)
        // NPC: 9201096
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(8229);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8229s")
    public static void q8229s(ScriptManager sm) {
        // Quest 8229 - Lost in Translation 3 (START)
        // NPC: 9201051
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8229);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8230e")
    public static void q8230e(ScriptManager sm) {
        // Quest 8230 - Stemming the Tide (END)
        // NPC: 9201096

        final int QUEST_ITEM_3992041 = 3992041;

        if (!sm.hasItem(QUEST_ITEM_3992041, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3992041, 1);
            sm.forceCompleteQuest(8230);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8230s")
    public static void q8230s(ScriptManager sm) {
        // Quest 8230 - Stemming the Tide (START)
        // NPC: 9201096
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8230);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8231s")
    public static void q8231s(ScriptManager sm) {
        // Quest 8231 - Fool's Gold (START)
        // NPC: 9201054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8231);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8232s")
    public static void q8232s(ScriptManager sm) {
        // Quest 8232 - Fool's Gold. (START)
        // NPC: 9201054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8232);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8233s")
    public static void q8233s(ScriptManager sm) {
        // Quest 8233 - Rags to Riches (START)
        // NPC: 9201054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8233);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8234s")
    public static void q8234s(ScriptManager sm) {
        // Quest 8234 - Rags to Riches. (START)
        // NPC: 9201054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8234);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8235s")
    public static void q8235s(ScriptManager sm) {
        // Quest 8235 - One Step A-Head (START)
        // NPC: 9201054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8235);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8236s")
    public static void q8236s(ScriptManager sm) {
        // Quest 8236 - One Step A-Head. (START)
        // NPC: 9201054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8236);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8237s")
    public static void q8237s(ScriptManager sm) {
        // Quest 8237 - Catch a Bigfoot by the Toe (START)
        // NPC: 9201054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8237);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8238s")
    public static void q8238s(ScriptManager sm) {
        // Quest 8238 - Catch a Bigfoot by the Toe. (START)
        // NPC: 9201054
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8238);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8247e")
    public static void q8247e(ScriptManager sm) {
        // Quest 8247 - Donate Your Notebook (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(8247);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8251e")
    public static void q8251e(ScriptManager sm) {
        // Quest 8251 - Helping the daughter of the poultry farm owner, Lazy Daisy (END)
        // NPC: 9209008
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(8251);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q8251s")
    public static void q8251s(ScriptManager sm) {
        // Quest 8251 - Helping the daughter of the poultry farm owner, Lazy Daisy (START)
        // NPC: 9209007
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8251);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8255s")
    public static void q8255s(ScriptManager sm) {
        // Quest 8255 - Lost Spirits (START)
        // NPC: 9201106
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8255);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q8256s")
    public static void q8256s(ScriptManager sm) {
        // Quest 8256 - Lost Spirits. (START)
        // NPC: 9201106
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(8256);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

}
