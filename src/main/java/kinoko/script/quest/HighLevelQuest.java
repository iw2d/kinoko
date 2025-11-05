package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * High-Level Quest System
 * Covers quests 28000-28999 range for high-level quests
 */
public final class HighLevelQuest extends ScriptHandler {


    @Script("q28004s")
    public static void q28004s(ScriptManager sm) {
        // Quest 28004 - Save the Snowman! (START)
        // NPC: 9105003
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28004);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28117s")
    public static void q28117s(ScriptManager sm) {
        // Quest 28117 - 4 Candles (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28117);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28118s")
    public static void q28118s(ScriptManager sm) {
        // Quest 28118 - 4 Year Anniversary Cake (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28118);
            sm.addItem(4220074, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28131e")
    public static void q28131e(ScriptManager sm) {
        // Quest 28131 - Soft and Cozy Chief's Chair (END)
        // NPC: 9201116
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28131);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28137e")
    public static void q28137e(ScriptManager sm) {
        // Quest 28137 - Spirit Week Event - September 29th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28137);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28138e")
    public static void q28138e(ScriptManager sm) {
        // Quest 28138 - Spirit Week Event - October 6th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28138);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28139e")
    public static void q28139e(ScriptManager sm) {
        // Quest 28139 - Spirit Week Event - October 13th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28139);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28140e")
    public static void q28140e(ScriptManager sm) {
        // Quest 28140 - Spirit Week Event - October 20th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28140);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28141s")
    public static void q28141s(ScriptManager sm) {
        // Quest 28141 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28141);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28142s")
    public static void q28142s(ScriptManager sm) {
        // Quest 28142 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28142);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28143s")
    public static void q28143s(ScriptManager sm) {
        // Quest 28143 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28143);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28144s")
    public static void q28144s(ScriptManager sm) {
        // Quest 28144 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28144);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28145e")
    public static void q28145e(ScriptManager sm) {
        // Quest 28145 - Spirit Week Event - September 24th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28145);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28146e")
    public static void q28146e(ScriptManager sm) {
        // Quest 28146 - Spirit Week Event - October 1st (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28146);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28147e")
    public static void q28147e(ScriptManager sm) {
        // Quest 28147 - Spirit Week Event - October 8th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28147);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28148e")
    public static void q28148e(ScriptManager sm) {
        // Quest 28148 - Spirit Week Event - October 15th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28148);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28149s")
    public static void q28149s(ScriptManager sm) {
        // Quest 28149 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28149);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28150s")
    public static void q28150s(ScriptManager sm) {
        // Quest 28150 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28150);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28151s")
    public static void q28151s(ScriptManager sm) {
        // Quest 28151 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28151);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28152s")
    public static void q28152s(ScriptManager sm) {
        // Quest 28152 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28152);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28153e")
    public static void q28153e(ScriptManager sm) {
        // Quest 28153 - Spirit Week Event - October 27th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28153);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28154s")
    public static void q28154s(ScriptManager sm) {
        // Quest 28154 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28154);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28155e")
    public static void q28155e(ScriptManager sm) {
        // Quest 28155 - Spirit Week Event - October 22nd (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28155);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28156s")
    public static void q28156s(ScriptManager sm) {
        // Quest 28156 - Spirit Week Event - Cassandra's Candle  (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28156);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28157e")
    public static void q28157e(ScriptManager sm) {
        // Quest 28157 - Spirit Week Event - September 28th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28157);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28158e")
    public static void q28158e(ScriptManager sm) {
        // Quest 28158 - Spirit Week Event - October 5th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28158);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28159e")
    public static void q28159e(ScriptManager sm) {
        // Quest 28159 - Spirit Week Event - October 12th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28159);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28160e")
    public static void q28160e(ScriptManager sm) {
        // Quest 28160 - Spirit Week Event - October 19th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28160);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28161e")
    public static void q28161e(ScriptManager sm) {
        // Quest 28161 - Spirit Week Event - October 26th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28161);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28284e")
    public static void q28284e(ScriptManager sm) {
        // Quest 28284 - Cassandra's Trick or Treat (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032444 = 4032444;

        if (!sm.hasItem(QUEST_ITEM_4032444, 31)) {
            sm.sayOk("You need 31 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032444, 31);
            sm.forceCompleteQuest(28284);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28285e")
    public static void q28285e(ScriptManager sm) {
        // Quest 28285 - Cassandra's Trick or Treat (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032445 = 4032445;

        if (!sm.hasItem(QUEST_ITEM_4032445, 31)) {
            sm.sayOk("You need 31 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032445, 31);
            sm.forceCompleteQuest(28285);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28286e")
    public static void q28286e(ScriptManager sm) {
        // Quest 28286 - Cassandra's Trick or Treat (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032446 = 4032446;

        if (!sm.hasItem(QUEST_ITEM_4032446, 31)) {
            sm.sayOk("You need 31 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032446, 31);
            sm.forceCompleteQuest(28286);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28304e")
    public static void q28304e(ScriptManager sm) {
        // Quest 28304 - Olivia: "Bring My Daddy" (END)
        // NPC: 9201137

        final int QUEST_ITEM_4032441 = 4032441;

        if (!sm.hasItem(QUEST_ITEM_4032441, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032441, 1);
            sm.forceCompleteQuest(28304);
            sm.addItem(4032441, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28311e")
    public static void q28311e(ScriptManager sm) {
        // Quest 28311 - Olivia: "Bring My Daddy" (END)
        // NPC: 9201137

        final int QUEST_ITEM_4032442 = 4032442;

        if (!sm.hasItem(QUEST_ITEM_4032442, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032442, 1);
            sm.forceCompleteQuest(28311);
            sm.addItem(4032442, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28318e")
    public static void q28318e(ScriptManager sm) {
        // Quest 28318 - Olivia: "Bring My Daddy" (END)
        // NPC: 9201137

        final int QUEST_ITEM_4032443 = 4032443;

        if (!sm.hasItem(QUEST_ITEM_4032443, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032443, 1);
            sm.forceCompleteQuest(28318);
            sm.addItem(4032443, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28326e")
    public static void q28326e(ScriptManager sm) {
        // Quest 28326 - Yellow Turkey Egg Hunt (END)
        // NPC: 9200000

        final int QUEST_ITEM_4032522 = 4032522;

        if (!sm.hasItem(QUEST_ITEM_4032522, 5)) {
            sm.sayOk("You need 5 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032522, 5);
            sm.forceCompleteQuest(28326);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28327e")
    public static void q28327e(ScriptManager sm) {
        // Quest 28327 - Green Turkey Egg Hunt (END)
        // NPC: 9200000

        final int QUEST_ITEM_4032523 = 4032523;

        if (!sm.hasItem(QUEST_ITEM_4032523, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032523, 10);
            sm.forceCompleteQuest(28327);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28328e")
    public static void q28328e(ScriptManager sm) {
        // Quest 28328 - Blue Turkey Egg Hunt (END)
        // NPC: 9200000

        final int QUEST_ITEM_4032524 = 4032524;

        if (!sm.hasItem(QUEST_ITEM_4032524, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032524, 20);
            sm.forceCompleteQuest(28328);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28333s")
    public static void q28333s(ScriptManager sm) {
        // Quest 28333 - New Year's Presents from Cassandra (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28333);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28337e")
    public static void q28337e(ScriptManager sm) {
        // Quest 28337 - Cupid's Lost Arrows (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28337);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28353s")
    public static void q28353s(ScriptManager sm) {
        // Quest 28353 - The Shadow Knight in Dragon's Nest (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28353);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28354e")
    public static void q28354e(ScriptManager sm) {
        // Quest 28354 - The Shadow Knight's Request for Help (END)
        // NPC: 9201144

        final int QUEST_ITEM_4032639 = 4032639;

        if (!sm.hasItem(QUEST_ITEM_4032639, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032639, 1);
            sm.forceCompleteQuest(28354);
            sm.addItem(4032639, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28354s")
    public static void q28354s(ScriptManager sm) {
        // Quest 28354 - The Shadow Knight's Request for Help (START)
        // NPC: 9201144
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28354);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28361e")
    public static void q28361e(ScriptManager sm) {
        // Quest 28361 - First Evan Launch Gift from the Admin (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28361);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28362e")
    public static void q28362e(ScriptManager sm) {
        // Quest 28362 - Second Evan Launch Gift from the Admin (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28362);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28363e")
    public static void q28363e(ScriptManager sm) {
        // Quest 28363 - Third Evan Launch Gift from the Admin (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28363);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28364e")
    public static void q28364e(ScriptManager sm) {
        // Quest 28364 - Fourth Evan Launch Gift from the Admin (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28364);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28365e")
    public static void q28365e(ScriptManager sm) {
        // Quest 28365 - Fifth Evan Launch Gift from the Admin (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28365);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28368e")
    public static void q28368e(ScriptManager sm) {
        // Quest 28368 - I Need to Prove Myself to My Daughter  (END)
        // NPC: 1012111
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28368);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28369e")
    public static void q28369e(ScriptManager sm) {
        // Quest 28369 - I Need to Prove Myself to My Daughter   (END)
        // NPC: 1012111
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28369);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28370e")
    public static void q28370e(ScriptManager sm) {
        // Quest 28370 - Welcome Back to New Leaf City Day 1 (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28370);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28371e")
    public static void q28371e(ScriptManager sm) {
        // Quest 28371 - Welcome Back to New Leaf City Day 2 (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28371);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28372e")
    public static void q28372e(ScriptManager sm) {
        // Quest 28372 - Welcome Back to New Leaf City Day 3 (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28372);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28373e")
    public static void q28373e(ScriptManager sm) {
        // Quest 28373 - Welcome Back to New Leaf City Day 4 (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28373);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28374e")
    public static void q28374e(ScriptManager sm) {
        // Quest 28374 - Welcome Back to New Leaf City Day 5 (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28374);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28375e")
    public static void q28375e(ScriptManager sm) {
        // Quest 28375 - Welcome Back to New Leaf City Day 6 (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28375);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28376e")
    public static void q28376e(ScriptManager sm) {
        // Quest 28376 - Nautilus' Secret Entree (END)
        // NPC: 1092000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28376);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28377e")
    public static void q28377e(ScriptManager sm) {
        // Quest 28377 - Welcome Back to New Leaf City Day 7 (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28377);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28378e")
    public static void q28378e(ScriptManager sm) {
        // Quest 28378 - Nautilus' Secret Entree (END)
        // NPC: 1092000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28378);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28379e")
    public static void q28379e(ScriptManager sm) {
        // Quest 28379 - Path of a True Mapler (END)
        // NPC: 1101002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28379);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28380e")
    public static void q28380e(ScriptManager sm) {
        // Quest 28380 - Path of a True Mapler (END)
        // NPC: 1101002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28380);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28381e")
    public static void q28381e(ScriptManager sm) {
        // Quest 28381 - Path of a True Mapler (END)
        // NPC: 1101002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28381);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28382e")
    public static void q28382e(ScriptManager sm) {
        // Quest 28382 - Path of a True Mapler (END)
        // NPC: 1101002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28382);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28383e")
    public static void q28383e(ScriptManager sm) {
        // Quest 28383 - I Need to Prove Myself to My Daughter (END)
        // NPC: 1012111
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28383);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28384e")
    public static void q28384e(ScriptManager sm) {
        // Quest 28384 - I Need to Prove Myself to My Daughter    (END)
        // NPC: 1012111
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28384);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28385e")
    public static void q28385e(ScriptManager sm) {
        // Quest 28385 - Welcome Back to New Leaf City Day 1  (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28385);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28386e")
    public static void q28386e(ScriptManager sm) {
        // Quest 28386 - Welcome Back to New Leaf City Day 2  (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28386);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28387e")
    public static void q28387e(ScriptManager sm) {
        // Quest 28387 - Welcome Back to New Leaf City Day 3  (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28387);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28388e")
    public static void q28388e(ScriptManager sm) {
        // Quest 28388 - Welcome Back to New Leaf City Day 4  (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28388);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28389e")
    public static void q28389e(ScriptManager sm) {
        // Quest 28389 - Welcome Back to New Leaf City Day 5  (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28389);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28390e")
    public static void q28390e(ScriptManager sm) {
        // Quest 28390 - Welcome Back to New Leaf City Day 6  (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28390);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28391e")
    public static void q28391e(ScriptManager sm) {
        // Quest 28391 - Nautilus' Secret Entree (END)
        // NPC: 1092000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28391);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28392e")
    public static void q28392e(ScriptManager sm) {
        // Quest 28392 - Welcome Back to New Leaf City Day 7  (END)
        // NPC: 9201050
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28392);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28393e")
    public static void q28393e(ScriptManager sm) {
        // Quest 28393 - Nautilus' Secret Entree (END)
        // NPC: 1092000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28393);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28394e")
    public static void q28394e(ScriptManager sm) {
        // Quest 28394 - Path of a True Mapler (END)
        // NPC: 1101002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28394);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28395e")
    public static void q28395e(ScriptManager sm) {
        // Quest 28395 - Path of a True Mapler (END)
        // NPC: 1101002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28395);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28396e")
    public static void q28396e(ScriptManager sm) {
        // Quest 28396 - Path of a True Mapler (END)
        // NPC: 1101002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28396);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28397e")
    public static void q28397e(ScriptManager sm) {
        // Quest 28397 - Path of a True Mapler (END)
        // NPC: 1101002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28397);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28399e")
    public static void q28399e(ScriptManager sm) {
        // Quest 28399 - Cakes or Pies? (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28399);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28422s")
    public static void q28422s(ScriptManager sm) {
        // Quest 28422 - What day is today? (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28422);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28423s")
    public static void q28423s(ScriptManager sm) {
        // Quest 28423 - What day is today?? (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28423);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28431e")
    public static void q28431e(ScriptManager sm) {
        // Quest 28431 - Lucky Gift  (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(28431);
            sm.addItem(2450000, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q28433s")
    public static void q28433s(ScriptManager sm) {
        // Quest 28433 - How to find friends (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28433);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q28436s")
    public static void q28436s(ScriptManager sm) {
        // Quest 28436 - How to find friends (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(28436);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

}
