package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Special Quest System
 * Covers quests 9000-9999 range for special/event quests
 */
public final class SpecialQuest extends ScriptHandler {


    @Script("q9432e")
    public static void q9432e(ScriptManager sm) {
        // Quest 9432 - 북극곰 포치의 부탁 (END)
        // NPC: 9001002

        final int QUEST_ITEM_4031322 = 4031322;
        final int QUEST_ITEM_4031323 = 4031323;
        final int QUEST_ITEM_4000216 = 4000216;

        if (!sm.hasItem(QUEST_ITEM_4031322, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4031323, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4000216, 50)) {
            sm.sayOk("You need 50 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031322, 1);
            sm.removeItem(QUEST_ITEM_4031323, 1);
            sm.removeItem(QUEST_ITEM_4000216, 50);
            sm.forceCompleteQuest(9432);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9633s")
    public static void q9633s(ScriptManager sm) {
        // Quest 9633 - Teo's Nostalgic Reminiscing II (START)
        // NPC: 1002001
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9633);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9682e")
    public static void q9682e(ScriptManager sm) {
        // Quest 9682 - Spirit Week Sept 6th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9682);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9683e")
    public static void q9683e(ScriptManager sm) {
        // Quest 9683 - Spirit Week Sept 13th (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9683);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9730e")
    public static void q9730e(ScriptManager sm) {
        // Quest 9730 - To Fool a Liar (END)
        // NPC: 9010011

        final int QUEST_ITEM_4031583 = 4031583;

        if (!sm.hasItem(QUEST_ITEM_4031583, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031583, 10);
            sm.forceCompleteQuest(9730);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9731e")
    public static void q9731e(ScriptManager sm) {
        // Quest 9731 - To Fool a Liar (END)
        // NPC: 9010012

        final int QUEST_ITEM_4031584 = 4031584;

        if (!sm.hasItem(QUEST_ITEM_4031584, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031584, 10);
            sm.forceCompleteQuest(9731);
            sm.addExp(8500); // EXP reward
            sm.addItem(4031584, -10); // Reward item
            sm.addItem(1012058, 1); // Reward item
            sm.addItem(1012059, 1); // Reward item
            sm.addItem(1012060, 1); // Reward item
            sm.addItem(1012061, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9732e")
    public static void q9732e(ScriptManager sm) {
        // Quest 9732 - To Fool a Liar (END)
        // NPC: 9010013

        final int QUEST_ITEM_4031585 = 4031585;

        if (!sm.hasItem(QUEST_ITEM_4031585, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031585, 10);
            sm.forceCompleteQuest(9732);
            sm.addExp(17400); // EXP reward
            sm.addItem(4031585, -10); // Reward item
            sm.addItem(1012059, 1); // Reward item
            sm.addItem(1012060, 1); // Reward item
            sm.addItem(1012061, 1); // Reward item
            sm.addItem(1012058, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9733e")
    public static void q9733e(ScriptManager sm) {
        // Quest 9733 - Help out Gordon! (END)
        // NPC: 9010010

        final int QUEST_ITEM_4031933 = 4031933;

        if (!sm.hasItem(QUEST_ITEM_4031933, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031933, 30);
            sm.forceCompleteQuest(9733);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9734e")
    public static void q9734e(ScriptManager sm) {
        // Quest 9734 - Help out Gordon!! (END)
        // NPC: 9010010

        final int QUEST_ITEM_4031934 = 4031934;

        if (!sm.hasItem(QUEST_ITEM_4031934, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031934, 30);
            sm.forceCompleteQuest(9734);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9735e")
    public static void q9735e(ScriptManager sm) {
        // Quest 9735 - Help out Gordon!!! (END)
        // NPC: 9010010

        final int QUEST_ITEM_4031935 = 4031935;

        if (!sm.hasItem(QUEST_ITEM_4031935, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031935, 30);
            sm.forceCompleteQuest(9735);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9745e")
    public static void q9745e(ScriptManager sm) {
        // Quest 9745 - 커플을 위한 다크 초콜릿 (END)
        // NPC: 1012108

        final int QUEST_ITEM_4031938 = 4031938;

        if (!sm.hasItem(QUEST_ITEM_4031938, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031938, 10);
            sm.forceCompleteQuest(9745);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9746e")
    public static void q9746e(ScriptManager sm) {
        // Quest 9746 - 커플을 위한 다크 초콜릿! (END)
        // NPC: 1022002

        final int QUEST_ITEM_4031939 = 4031939;

        if (!sm.hasItem(QUEST_ITEM_4031939, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031939, 10);
            sm.forceCompleteQuest(9746);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9747e")
    public static void q9747e(ScriptManager sm) {
        // Quest 9747 - 커플을 위한 다크 초콜릿!! (END)
        // NPC: 2020006

        final int QUEST_ITEM_4031940 = 4031940;

        if (!sm.hasItem(QUEST_ITEM_4031940, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031940, 10);
            sm.forceCompleteQuest(9747);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9851s")
    public static void q9851s(ScriptManager sm) {
        // Quest 9851 - Snake Pit in the Swamp (START)
        // NPC: 1052000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9851);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9875e")
    public static void q9875e(ScriptManager sm) {
        // Quest 9875 - Growing a Sprout (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9875);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9875s")
    public static void q9875s(ScriptManager sm) {
        // Quest 9875 - Growing a Sprout (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9875);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9878e")
    public static void q9878e(ScriptManager sm) {
        // Quest 9878 - Magatia, the City of Alchemy (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9878);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9880e")
    public static void q9880e(ScriptManager sm) {
        // Quest 9880 - Wanted: Mano (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9880);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9880s")
    public static void q9880s(ScriptManager sm) {
        // Quest 9880 - Wanted: Mano (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9880);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9881e")
    public static void q9881e(ScriptManager sm) {
        // Quest 9881 - Wanted: Stumpy (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9881);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9881s")
    public static void q9881s(ScriptManager sm) {
        // Quest 9881 - Wanted: Stumpy (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9881);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9882e")
    public static void q9882e(ScriptManager sm) {
        // Quest 9882 - Wanted: King Clang (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9882);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9882s")
    public static void q9882s(ScriptManager sm) {
        // Quest 9882 - Wanted: King Clang (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9882);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9883e")
    public static void q9883e(ScriptManager sm) {
        // Quest 9883 - Wanted: Tae Roon (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9883);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9883s")
    public static void q9883s(ScriptManager sm) {
        // Quest 9883 - Wanted: Tae Roon (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9883);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9884e")
    public static void q9884e(ScriptManager sm) {
        // Quest 9884 - Wanted: Eliza (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9884);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9884s")
    public static void q9884s(ScriptManager sm) {
        // Quest 9884 - Wanted: Eliza (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9884);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9885e")
    public static void q9885e(ScriptManager sm) {
        // Quest 9885 - Wanted: Snowman (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9885);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9885s")
    public static void q9885s(ScriptManager sm) {
        // Quest 9885 - Wanted: Snowman (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9885);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9902e")
    public static void q9902e(ScriptManager sm) {
        // Quest 9902 - 마노 퇴치하기 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9902);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9903e")
    public static void q9903e(ScriptManager sm) {
        // Quest 9903 - 스텀피 퇴치하기 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9903);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9904e")
    public static void q9904e(ScriptManager sm) {
        // Quest 9904 - 킹크랑 퇴치하기 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9904);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9905e")
    public static void q9905e(ScriptManager sm) {
        // Quest 9905 - 구미호 퇴치하기 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9905);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9906e")
    public static void q9906e(ScriptManager sm) {
        // Quest 9906 - 태륜 퇴치하기 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9906);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9907e")
    public static void q9907e(ScriptManager sm) {
        // Quest 9907 - 요괴선사 퇴치하기 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9907);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9908e")
    public static void q9908e(ScriptManager sm) {
        // Quest 9908 - 엘리쟈 퇴치하기 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9908);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9909e")
    public static void q9909e(ScriptManager sm) {
        // Quest 9909 - 스노우맨 퇴치하기 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9909);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9920e")
    public static void q9920e(ScriptManager sm) {
        // Quest 9920 - 가면신사의 초대 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9920);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9922e")
    public static void q9922e(ScriptManager sm) {
        // Quest 9922 - 고양이의 충고 (END)
        // NPC: 2121000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9922);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9922s")
    public static void q9922s(ScriptManager sm) {
        // Quest 9922 - 고양이의 충고 (START)
        // NPC: 2121000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9922);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9924s")
    public static void q9924s(ScriptManager sm) {
        // Quest 9924 - 사탕주지 않으면 장난칠 테야! (START)
        // NPC: 2121012
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9924);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9925s")
    public static void q9925s(ScriptManager sm) {
        // Quest 9925 - 사탕주지 않으면 장난칠 테야!! (START)
        // NPC: 2121012
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9925);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9926s")
    public static void q9926s(ScriptManager sm) {
        // Quest 9926 - 사탕주지 않으면 장난칠 테야!!! (START)
        // NPC: 2121012
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9926);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9927e")
    public static void q9927e(ScriptManager sm) {
        // Quest 9927 - 가면신사와의 대화 (END)
        // NPC: 2120000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9927);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9928e")
    public static void q9928e(ScriptManager sm) {
        // Quest 9928 - 이상한 소녀 (END)
        // NPC: 2120005

        final int QUEST_ITEM_2022256 = 2022256;

        if (!sm.hasItem(QUEST_ITEM_2022256, 5)) {
            sm.sayOk("You need 5 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022256, 5);
            sm.forceCompleteQuest(9928);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9929e")
    public static void q9929e(ScriptManager sm) {
        // Quest 9929 - 인형장인 조나스 (END)
        // NPC: 2120004

        final int QUEST_ITEM_4220021 = 4220021;

        if (!sm.hasItem(QUEST_ITEM_4220021, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4220021, 1);
            sm.forceCompleteQuest(9929);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9929s")
    public static void q9929s(ScriptManager sm) {
        // Quest 9929 - 인형장인 조나스 (START)
        // NPC: 2120004
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9929);
            sm.addItem(4220021, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9930e")
    public static void q9930e(ScriptManager sm) {
        // Quest 9930 - 완벽한 연장 (END)
        // NPC: 2120004

        final int QUEST_ITEM_4031834 = 4031834;

        if (!sm.hasItem(QUEST_ITEM_4031834, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031834, 1);
            sm.forceCompleteQuest(9930);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9931e")
    public static void q9931e(ScriptManager sm) {
        // Quest 9931 - 소필리아의 초상화 (END)
        // NPC: 2120004

        final int QUEST_ITEM_4031832 = 4031832;

        if (!sm.hasItem(QUEST_ITEM_4031832, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031832, 1);
            sm.forceCompleteQuest(9931);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9932e")
    public static void q9932e(ScriptManager sm) {
        // Quest 9932 - 루드밀라의 귀고리 (END)
        // NPC: 2120006

        final int QUEST_ITEM_4031835 = 4031835;

        if (!sm.hasItem(QUEST_ITEM_4031835, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031835, 1);
            sm.forceCompleteQuest(9932);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9933e")
    public static void q9933e(ScriptManager sm) {
        // Quest 9933 - 좋은 음악 (END)
        // NPC: 2120006
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9933);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9934e")
    public static void q9934e(ScriptManager sm) {
        // Quest 9934 - 향기로운 음식 (END)
        // NPC: 2120006

        final int QUEST_ITEM_4031833 = 4031833;

        if (!sm.hasItem(QUEST_ITEM_4031833, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031833, 1);
            sm.forceCompleteQuest(9934);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9935e")
    public static void q9935e(ScriptManager sm) {
        // Quest 9935 - 소필리아의 눈물 (END)
        // NPC: 2120005
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9935);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9936e")
    public static void q9936e(ScriptManager sm) {
        // Quest 9936 - 새로운 기회 (END)
        // NPC: 2120000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9936);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9937s")
    public static void q9937s(ScriptManager sm) {
        // Quest 9937 - 다른 쪽으로 가고 싶어졌어 (START)
        // NPC: 2120005
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9937);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9938s")
    public static void q9938s(ScriptManager sm) {
        // Quest 9938 - 결정을 번복하고 싶다  (START)
        // NPC: 2120004
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9938);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9939s")
    public static void q9939s(ScriptManager sm) {
        // Quest 9939 - 잘못 대답한 것 같다 (START)
        // NPC: 2120006
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9939);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9943e")
    public static void q9943e(ScriptManager sm) {
        // Quest 9943 - 잊혀진 이름 (END)
        // NPC: 2120007
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9943);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9947s")
    public static void q9947s(ScriptManager sm) {
        // Quest 9947 - 인형을 돌려드리겠어요. (START)
        // NPC: 2120004
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9947);
            sm.addItem(4220021, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9950e")
    public static void q9950e(ScriptManager sm) {
        // Quest 9950 - 해적 레벨업 이벤트  (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9950);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9951e")
    public static void q9951e(ScriptManager sm) {
        // Quest 9951 - ...Pirates!? (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9951);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9961e")
    public static void q9961e(ScriptManager sm) {
        // Quest 9961 - 새해 운세보기 (END)
        // NPC: 9010010

        final int QUEST_ITEM_4220023 = 4220023;

        if (!sm.hasItem(QUEST_ITEM_4220023, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4220023, 1);
            sm.forceCompleteQuest(9961);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9961s")
    public static void q9961s(ScriptManager sm) {
        // Quest 9961 - 새해 운세보기 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9961);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9965s")
    public static void q9965s(ScriptManager sm) {
        // Quest 9965 - 별도장 받기 (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9965);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9970e")
    public static void q9970e(ScriptManager sm) {
        // Quest 9970 - 페티트의 수리부품 (END)
        // NPC: 9010010

        final int QUEST_ITEM_4031923 = 4031923;

        if (!sm.hasItem(QUEST_ITEM_4031923, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031923, 30);
            sm.forceCompleteQuest(9970);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9980s")
    public static void q9980s(ScriptManager sm) {
        // Quest 9980 - Tienk's Monster Card (START)
        // NPC: 2006
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9980);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9981e")
    public static void q9981e(ScriptManager sm) {
        // Quest 9981 - 4 Year Anniversary Level Up event (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(9981);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9982s")
    public static void q9982s(ScriptManager sm) {
        // Quest 9982 - 5개의 촛불 (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9982);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9983s")
    public static void q9983s(ScriptManager sm) {
        // Quest 9983 - 5주년 축하 케이크 (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9983);
            sm.addItem(4220045, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9984e")
    public static void q9984e(ScriptManager sm) {
        // Quest 9984 - Putting the Maple Leaf in my mouth... (END)
        // NPC: 9010010

        final int QUEST_ITEM_4001126 = 4001126;

        if (!sm.hasItem(QUEST_ITEM_4001126, 50)) {
            sm.sayOk("You need 50 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001126, 50);
            sm.forceCompleteQuest(9984);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9985e")
    public static void q9985e(ScriptManager sm) {
        // Quest 9985 - Making Maple Syrup (END)
        // NPC: 9010010

        final int QUEST_ITEM_4001126 = 4001126;

        if (!sm.hasItem(QUEST_ITEM_4001126, 25)) {
            sm.sayOk("You need 25 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001126, 25);
            sm.forceCompleteQuest(9985);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q9990s")
    public static void q9990s(ScriptManager sm) {
        // Quest 9990 - Gaga's Maple Leaf (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9990);
            sm.addItem(4001168, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9991s")
    public static void q9991s(ScriptManager sm) {
        // Quest 9991 - Speed Quiz (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9991);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q9997s")
    public static void q9997s(ScriptManager sm) {
        // Quest 9997 - Operation MS-07 (START)
        // NPC: 9000032
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(9997);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q99999s")
    public static void q99999s(ScriptManager sm) {
        // Quest 99999 - 123 (START)
        // NPC: 10000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(99999);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

}
