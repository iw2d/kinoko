package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Event Quest System
 * Covers quests 10000-10999 range for event quests
 */
public final class EventQuest extends ScriptHandler {


    @Script("q10002e")
    public static void q10002e(ScriptManager sm) {
        // Quest 10002 - Number of Special Agent Badges  (END)
        // NPC: 9000034

        final int QUEST_ITEM_4031988 = 4031988;

        if (!sm.hasItem(QUEST_ITEM_4031988, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031988, 1);
            sm.forceCompleteQuest(10002);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10008s")
    public static void q10008s(ScriptManager sm) {
        // Quest 10008 - Information on Master M (START)
        // NPC: 9000033
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10008);
            sm.addItem(4001192, 5); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10010e")
    public static void q10010e(ScriptManager sm) {
        // Quest 10010 - Special Order: Find Master M's Orders! (END)
        // NPC: 9000036

        final int QUEST_ITEM_4031999 = 4031999;

        if (!sm.hasItem(QUEST_ITEM_4031999, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031999, 20);
            sm.forceCompleteQuest(10010);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10011e")
    public static void q10011e(ScriptManager sm) {
        // Quest 10011 - Special Order: Find Master M's Orders! (END)
        // NPC: 9000036

        final int QUEST_ITEM_4032000 = 4032000;

        if (!sm.hasItem(QUEST_ITEM_4032000, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032000, 20);
            sm.forceCompleteQuest(10011);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10012e")
    public static void q10012e(ScriptManager sm) {
        // Quest 10012 - Special Order: Find Master M's Orders! (END)
        // NPC: 9000036

        final int QUEST_ITEM_4032001 = 4032001;

        if (!sm.hasItem(QUEST_ITEM_4032001, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032001, 20);
            sm.forceCompleteQuest(10012);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10014s")
    public static void q10014s(ScriptManager sm) {
        // Quest 10014 - Today's Mission! (START)
        // NPC: 9000036
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10014);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10034s")
    public static void q10034s(ScriptManager sm) {
        // Quest 10034 - 추석맞이 달나라 여행 (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10034);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10036e")
    public static void q10036e(ScriptManager sm) {
        // Quest 10036 - 달은 어떻게 생겼나요 (END)
        // NPC: 9001102

        final int QUEST_ITEM_4220067 = 4220067;

        if (!sm.hasItem(QUEST_ITEM_4220067, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4220067, 1);
            sm.forceCompleteQuest(10036);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10036s")
    public static void q10036s(ScriptManager sm) {
        // Quest 10036 - 달은 어떻게 생겼나요 (START)
        // NPC: 9001102
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10036);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10037e")
    public static void q10037e(ScriptManager sm) {
        // Quest 10037 - 카산드라의 도움 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10037);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10039e")
    public static void q10039e(ScriptManager sm) {
        // Quest 10039 - Surprise Event: Special Alphabet (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10039);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10043s")
    public static void q10043s(ScriptManager sm) {
        // Quest 10043 - 달꽃떡 받기 (START)
        // NPC: 9001101
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10043);
            sm.addItem(4032036, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10046s")
    public static void q10046s(ScriptManager sm) {
        // Quest 10046 - Taking Care of Baby Bird (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10046);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10050e")
    public static void q10050e(ScriptManager sm) {
        // Quest 10050 - Baby Bird Feather (END)
        // NPC: 9000042

        final int QUEST_ITEM_4032066 = 4032066;

        if (!sm.hasItem(QUEST_ITEM_4032066, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032066, 1);
            sm.forceCompleteQuest(10050);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10052s")
    public static void q10052s(ScriptManager sm) {
        // Quest 10052 - 메이플 2000일의 이야기 (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10052);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10059s")
    public static void q10059s(ScriptManager sm) {
        // Quest 10059 - Gaga's Favorite Song (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10059);
            sm.addItem(4001202, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10066e")
    public static void q10066e(ScriptManager sm) {
        // Quest 10066 - 과거의 기억 (END)
        // NPC: 2120005

        final int QUEST_ITEM_2022256 = 2022256;

        if (!sm.hasItem(QUEST_ITEM_2022256, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022256, 20);
            sm.forceCompleteQuest(10066);
            sm.addItem(2022256, -20); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10069e")
    public static void q10069e(ScriptManager sm) {
        // Quest 10069 - 유령 T는 누구일까? (END)
        // NPC: 2120008
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10069);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10074s")
    public static void q10074s(ScriptManager sm) {
        // Quest 10074 - 페토 변신 (START)
        // NPC: 2120000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10074);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10075e")
    public static void q10075e(ScriptManager sm) {
        // Quest 10075 - 조나스의 뉘우침 (END)
        // NPC: 2120004
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10075);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10076e")
    public static void q10076e(ScriptManager sm) {
        // Quest 10076 - 소필리아의 뉘우침 (END)
        // NPC: 2120005

        final int QUEST_ITEM_4032084 = 4032084;

        if (!sm.hasItem(QUEST_ITEM_4032084, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032084, 1);
            sm.forceCompleteQuest(10076);
            sm.addItem(4032084, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10077e")
    public static void q10077e(ScriptManager sm) {
        // Quest 10077 - 루드밀라의 뉘우침 (END)
        // NPC: 2120006

        final int QUEST_ITEM_4032088 = 4032088;

        if (!sm.hasItem(QUEST_ITEM_4032088, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032088, 1);
            sm.forceCompleteQuest(10077);
            sm.addItem(4032088, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10078e")
    public static void q10078e(ScriptManager sm) {
        // Quest 10078 - 집사의 뉘우침 (END)
        // NPC: 2120002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10078);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10079e")
    public static void q10079e(ScriptManager sm) {
        // Quest 10079 - 조이의 뉘우침 (END)
        // NPC: 2120007
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10079);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10082s")
    public static void q10082s(ScriptManager sm) {
        // Quest 10082 - 메이플스토리 그린 캠페인 (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10082);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10083e")
    public static void q10083e(ScriptManager sm) {
        // Quest 10083 - 장로스탄의 응원 (END)
        // NPC: 1012003
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10083);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10084e")
    public static void q10084e(ScriptManager sm) {
        // Quest 10084 - 피아의 응원 (END)
        // NPC: 1012102
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10084);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10085e")
    public static void q10085e(ScriptManager sm) {
        // Quest 10085 - 에스텔의 응원 (END)
        // NPC: 1032105
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10085);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10086e")
    public static void q10086e(ScriptManager sm) {
        // Quest 10086 - 요정 윙의 응원 (END)
        // NPC: 1032106
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10086);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10087e")
    public static void q10087e(ScriptManager sm) {
        // Quest 10087 - 천지의 응원 (END)
        // NPC: 9000007
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10087);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10088e")
    public static void q10088e(ScriptManager sm) {
        // Quest 10088 - 이카루스의 응원 (END)
        // NPC: 1052106
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10088);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10089e")
    public static void q10089e(ScriptManager sm) {
        // Quest 10089 - 만지의 응원 (END)
        // NPC: 1022002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10089);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10090e")
    public static void q10090e(ScriptManager sm) {
        // Quest 10090 - 돼지와 함께 춤을의 응원 (END)
        // NPC: 1020000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10090);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10091e")
    public static void q10091e(ScriptManager sm) {
        // Quest 10091 - 리드의 응원 (END)
        // NPC: 1092009
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10091);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10092e")
    public static void q10092e(ScriptManager sm) {
        // Quest 10092 - 베인의 응원 (END)
        // NPC: 1092002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10092);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10093e")
    public static void q10093e(ScriptManager sm) {
        // Quest 10093 - 기억하고 있는 자의 응원 (END)
        // NPC: 1061011
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10093);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10094e")
    public static void q10094e(ScriptManager sm) {
        // Quest 10094 - 찰리중사의 응원 (END)
        // NPC: 2010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10094);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10095e")
    public static void q10095e(ScriptManager sm) {
        // Quest 10095 - 스카두르의 응원 (END)
        // NPC: 2020007
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10095);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10096e")
    public static void q10096e(ScriptManager sm) {
        // Quest 10096 - 티건의 응원 (END)
        // NPC: 2101004
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10096);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10097e")
    public static void q10097e(ScriptManager sm) {
        // Quest 10097 - 세쟌의 응원 (END)
        // NPC: 2101011
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10097);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10098e")
    public static void q10098e(ScriptManager sm) {
        // Quest 10098 - 필리아의 응원 (END)
        // NPC: 2111004
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10098);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10099e")
    public static void q10099e(ScriptManager sm) {
        // Quest 10099 - 노공의 응원 (END)
        // NPC: 2091000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10099);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10100e")
    public static void q10100e(ScriptManager sm) {
        // Quest 10100 - 도공의 응원 (END)
        // NPC: 2091001
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10100);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10101e")
    public static void q10101e(ScriptManager sm) {
        // Quest 10101 - 구영감의 응원 (END)
        // NPC: 2092000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10101);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10102e")
    public static void q10102e(ScriptManager sm) {
        // Quest 10102 - 촌장 타타모의 응원 (END)
        // NPC: 2081000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10102);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10103e")
    public static void q10103e(ScriptManager sm) {
        // Quest 10103 - 지니의 응원 (END)
        // NPC: 9000014
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10103);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10104e")
    public static void q10104e(ScriptManager sm) {
        // Quest 10104 - Book of Cygnus Vol. 1 (END)
        // NPC: 9010010

        final int QUEST_ITEM_2430000 = 2430000;

        if (!sm.hasItem(QUEST_ITEM_2430000, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2430000, 20);
            sm.forceCompleteQuest(10104);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10105e")
    public static void q10105e(ScriptManager sm) {
        // Quest 10105 - Book of Cygnus Vol. 2 (END)
        // NPC: 9010010

        final int QUEST_ITEM_2430001 = 2430001;

        if (!sm.hasItem(QUEST_ITEM_2430001, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2430001, 20);
            sm.forceCompleteQuest(10105);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10106e")
    public static void q10106e(ScriptManager sm) {
        // Quest 10106 - Book of Cygnus Vol. 3 (END)
        // NPC: 9010010

        final int QUEST_ITEM_2430002 = 2430002;

        if (!sm.hasItem(QUEST_ITEM_2430002, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2430002, 20);
            sm.forceCompleteQuest(10106);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10108s")
    public static void q10108s(ScriptManager sm) {
        // Quest 10108 - 그린 캠페인 훈장 바꾸기 (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10108);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10110e")
    public static void q10110e(ScriptManager sm) {
        // Quest 10110 - 골드리치의 초대 (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10110);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10206e")
    public static void q10206e(ScriptManager sm) {
        // Quest 10206 - Remnants of Black Mage (END)
        // NPC: 9010000

        final int QUEST_ITEM_4001237 = 4001237;

        if (!sm.hasItem(QUEST_ITEM_4001237, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001237, 20);
            sm.forceCompleteQuest(10206);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10206s")
    public static void q10206s(ScriptManager sm) {
        // Quest 10206 - Remnants of Black Mage (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10206);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10207e")
    public static void q10207e(ScriptManager sm) {
        // Quest 10207 - Remnants of Black Mage (END)
        // NPC: 9010000

        final int QUEST_ITEM_4001238 = 4001238;

        if (!sm.hasItem(QUEST_ITEM_4001238, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001238, 20);
            sm.forceCompleteQuest(10207);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10207s")
    public static void q10207s(ScriptManager sm) {
        // Quest 10207 - Remnants of Black Mage (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10207);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10208e")
    public static void q10208e(ScriptManager sm) {
        // Quest 10208 - Remnants of Black Mage (END)
        // NPC: 9010000

        final int QUEST_ITEM_4001239 = 4001239;

        if (!sm.hasItem(QUEST_ITEM_4001239, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001239, 20);
            sm.forceCompleteQuest(10208);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10208s")
    public static void q10208s(ScriptManager sm) {
        // Quest 10208 - Remnants of Black Mage (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10208);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10209e")
    public static void q10209e(ScriptManager sm) {
        // Quest 10209 - Remnants of Black Mage (END)
        // NPC: 9010000

        final int QUEST_ITEM_4001240 = 4001240;

        if (!sm.hasItem(QUEST_ITEM_4001240, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001240, 20);
            sm.forceCompleteQuest(10209);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10209s")
    public static void q10209s(ScriptManager sm) {
        // Quest 10209 - Remnants of Black Mage (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10209);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10210s")
    public static void q10210s(ScriptManager sm) {
        // Quest 10210 - Gaga's Analysis (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10210);
            sm.addItem(4001237, 20); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10211s")
    public static void q10211s(ScriptManager sm) {
        // Quest 10211 - Gaga's Analysis (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10211);
            sm.addItem(4001238, 20); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10212s")
    public static void q10212s(ScriptManager sm) {
        // Quest 10212 - Gaga's Analysis (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10212);
            sm.addItem(4001239, 20); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10213s")
    public static void q10213s(ScriptManager sm) {
        // Quest 10213 - Gaga's Analysis (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10213);
            sm.addItem(4001240, 20); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10214s")
    public static void q10214s(ScriptManager sm) {
        // Quest 10214 - Cassandra's Analysis (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10214);
            sm.addItem(4001237, 20); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10215s")
    public static void q10215s(ScriptManager sm) {
        // Quest 10215 - Gaga's Analysis (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10215);
            sm.addItem(4001238, 20); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10216s")
    public static void q10216s(ScriptManager sm) {
        // Quest 10216 - Gaga's Analysis (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10216);
            sm.addItem(4001239, 20); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10217s")
    public static void q10217s(ScriptManager sm) {
        // Quest 10217 - Gaga's Analysis (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10217);
            sm.addItem(4001240, 20); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10218e")
    public static void q10218e(ScriptManager sm) {
        // Quest 10218 - 가가의 크리스마스 추억 (END)
        // NPC: 9000021
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10218);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10218s")
    public static void q10218s(ScriptManager sm) {
        // Quest 10218 - 가가의 크리스마스 추억 (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10218);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10219s")
    public static void q10219s(ScriptManager sm) {
        // Quest 10219 - 카산드라의 두번째 새해선물 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10219);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10220s")
    public static void q10220s(ScriptManager sm) {
        // Quest 10220 - 스피드 퀴즈 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10220);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10222s")
    public static void q10222s(ScriptManager sm) {
        // Quest 10222 - 두근두근 메이플!두근두근 나의 펫! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10222);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10224s")
    public static void q10224s(ScriptManager sm) {
        // Quest 10224 - Starlight Festival (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10224);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10231s")
    public static void q10231s(ScriptManager sm) {
        // Quest 10231 - Golden Pig's Egg (START)
        // NPC: 2084002
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10231);
            sm.addItem(4001255, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10240e")
    public static void q10240e(ScriptManager sm) {
        // Quest 10240 - 카산드라의 봄꽃축제 (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032264 = 4032264;
        final int QUEST_ITEM_4032266 = 4032266;

        if (!sm.hasItem(QUEST_ITEM_4032264, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032266, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032264, 10);
            sm.removeItem(QUEST_ITEM_4032266, 10);
            sm.forceCompleteQuest(10240);
            sm.addItem(4032264, -10); // Reward item
            sm.addItem(4032266, -10); // Reward item
            sm.addItem(2022526, 1); // Reward item
            sm.addItem(2022527, 1); // Reward item
            sm.addItem(2022528, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10241e")
    public static void q10241e(ScriptManager sm) {
        // Quest 10241 - 카산드라의 봄꽃축제 (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032265 = 4032265;
        final int QUEST_ITEM_4032270 = 4032270;

        if (!sm.hasItem(QUEST_ITEM_4032265, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032270, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032265, 10);
            sm.removeItem(QUEST_ITEM_4032270, 10);
            sm.forceCompleteQuest(10241);
            sm.addItem(4032265, -10); // Reward item
            sm.addItem(4032270, -10); // Reward item
            sm.addItem(2022526, 1); // Reward item
            sm.addItem(2022527, 1); // Reward item
            sm.addItem(2022528, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10242e")
    public static void q10242e(ScriptManager sm) {
        // Quest 10242 - 베티의 봄꽃연구1 (END)
        // NPC: 1032104

        final int QUEST_ITEM_2022526 = 2022526;

        if (!sm.hasItem(QUEST_ITEM_2022526, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022526, 3);
            sm.forceCompleteQuest(10242);
            sm.addItem(2022526, -3); // Reward item
            sm.addItem(1012139, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10243e")
    public static void q10243e(ScriptManager sm) {
        // Quest 10243 - 베티의 봄꽃연구2 (END)
        // NPC: 1032104

        final int QUEST_ITEM_2022527 = 2022527;

        if (!sm.hasItem(QUEST_ITEM_2022527, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022527, 3);
            sm.forceCompleteQuest(10243);
            sm.addItem(2022527, -3); // Reward item
            sm.addItem(1012140, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10244e")
    public static void q10244e(ScriptManager sm) {
        // Quest 10244 - 베티의 봄꽃연구3 (END)
        // NPC: 1032104

        final int QUEST_ITEM_2022528 = 2022528;

        if (!sm.hasItem(QUEST_ITEM_2022528, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022528, 3);
            sm.forceCompleteQuest(10244);
            sm.addItem(2022528, -3); // Reward item
            sm.addItem(1012141, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10245e")
    public static void q10245e(ScriptManager sm) {
        // Quest 10245 - 리사의 봄꽃연구1 (END)
        // NPC: 2012012

        final int QUEST_ITEM_2022526 = 2022526;

        if (!sm.hasItem(QUEST_ITEM_2022526, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022526, 3);
            sm.forceCompleteQuest(10245);
            sm.addItem(2022526, -3); // Reward item
            sm.addItem(1012139, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10246e")
    public static void q10246e(ScriptManager sm) {
        // Quest 10246 - 리사의 봄꽃연구2 (END)
        // NPC: 2012012

        final int QUEST_ITEM_2022527 = 2022527;

        if (!sm.hasItem(QUEST_ITEM_2022527, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022527, 3);
            sm.forceCompleteQuest(10246);
            sm.addItem(2022527, -3); // Reward item
            sm.addItem(1012140, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10247e")
    public static void q10247e(ScriptManager sm) {
        // Quest 10247 - 리사의 봄꽃연구3 (END)
        // NPC: 2012012

        final int QUEST_ITEM_2022528 = 2022528;

        if (!sm.hasItem(QUEST_ITEM_2022528, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022528, 3);
            sm.forceCompleteQuest(10247);
            sm.addItem(2022528, -3); // Reward item
            sm.addItem(1012141, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10248e")
    public static void q10248e(ScriptManager sm) {
        // Quest 10248 - 콩쥐의 봄꽃연구1 (END)
        // NPC: 2071004

        final int QUEST_ITEM_2022526 = 2022526;

        if (!sm.hasItem(QUEST_ITEM_2022526, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022526, 3);
            sm.forceCompleteQuest(10248);
            sm.addItem(2022526, -3); // Reward item
            sm.addItem(1012139, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10249e")
    public static void q10249e(ScriptManager sm) {
        // Quest 10249 - 콩쥐의 봄꽃연구2 (END)
        // NPC: 2071004

        final int QUEST_ITEM_2022527 = 2022527;

        if (!sm.hasItem(QUEST_ITEM_2022527, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022527, 3);
            sm.forceCompleteQuest(10249);
            sm.addItem(2022527, -3); // Reward item
            sm.addItem(1012140, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10250e")
    public static void q10250e(ScriptManager sm) {
        // Quest 10250 - 콩쥐의 봄꽃연구3 (END)
        // NPC: 2071004

        final int QUEST_ITEM_2022528 = 2022528;

        if (!sm.hasItem(QUEST_ITEM_2022528, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022528, 3);
            sm.forceCompleteQuest(10250);
            sm.addItem(2022528, -3); // Reward item
            sm.addItem(1012141, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10251e")
    public static void q10251e(ScriptManager sm) {
        // Quest 10251 - 키니의 봄꽃연구1 (END)
        // NPC: 2111005

        final int QUEST_ITEM_2022526 = 2022526;

        if (!sm.hasItem(QUEST_ITEM_2022526, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022526, 3);
            sm.forceCompleteQuest(10251);
            sm.addItem(2022526, -3); // Reward item
            sm.addItem(1012139, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10252e")
    public static void q10252e(ScriptManager sm) {
        // Quest 10252 - 키니의 봄꽃연구2 (END)
        // NPC: 2111005

        final int QUEST_ITEM_2022527 = 2022527;

        if (!sm.hasItem(QUEST_ITEM_2022527, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022527, 3);
            sm.forceCompleteQuest(10252);
            sm.addItem(2022527, -3); // Reward item
            sm.addItem(1012140, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10253e")
    public static void q10253e(ScriptManager sm) {
        // Quest 10253 - 키니의 봄꽃연구3 (END)
        // NPC: 2111005

        final int QUEST_ITEM_2022528 = 2022528;

        if (!sm.hasItem(QUEST_ITEM_2022528, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022528, 3);
            sm.forceCompleteQuest(10253);
            sm.addItem(2022528, -3); // Reward item
            sm.addItem(1012141, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10254e")
    public static void q10254e(ScriptManager sm) {
        // Quest 10254 - 촌장 타타모의 봄꽃연구1 (END)
        // NPC: 2081000

        final int QUEST_ITEM_2022526 = 2022526;

        if (!sm.hasItem(QUEST_ITEM_2022526, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022526, 3);
            sm.forceCompleteQuest(10254);
            sm.addItem(2022526, -3); // Reward item
            sm.addItem(1012139, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10255e")
    public static void q10255e(ScriptManager sm) {
        // Quest 10255 - 촌장 타타모의 봄꽃연구2 (END)
        // NPC: 2081000

        final int QUEST_ITEM_2022527 = 2022527;

        if (!sm.hasItem(QUEST_ITEM_2022527, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022527, 3);
            sm.forceCompleteQuest(10255);
            sm.addItem(2022527, -3); // Reward item
            sm.addItem(1012140, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10256e")
    public static void q10256e(ScriptManager sm) {
        // Quest 10256 - 촌장 타타모의 봄꽃연구3 (END)
        // NPC: 2081000

        final int QUEST_ITEM_2022528 = 2022528;

        if (!sm.hasItem(QUEST_ITEM_2022528, 3)) {
            sm.sayOk("You need 3 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022528, 3);
            sm.forceCompleteQuest(10256);
            sm.addItem(2022528, -3); // Reward item
            sm.addItem(1012140, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10260e")
    public static void q10260e(ScriptManager sm) {
        // Quest 10260 - Making Pure Perfume 1 (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032271 = 4032271;

        if (!sm.hasItem(QUEST_ITEM_4032271, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032271, 30);
            sm.forceCompleteQuest(10260);
            sm.addItem(2430009, 1); // Reward item
            sm.addItem(4032271, -30); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10261e")
    public static void q10261e(ScriptManager sm) {
        // Quest 10261 - Making Pure Perfume 2 (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032275 = 4032275;

        if (!sm.hasItem(QUEST_ITEM_4032275, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032275, 30);
            sm.forceCompleteQuest(10261);
            sm.addItem(4032275, -30); // Reward item
            sm.addItem(2430009, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10262e")
    public static void q10262e(ScriptManager sm) {
        // Quest 10262 - Making Pure Perfume 3 (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032276 = 4032276;

        if (!sm.hasItem(QUEST_ITEM_4032276, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032276, 30);
            sm.forceCompleteQuest(10262);
            sm.addItem(4032276, -30); // Reward item
            sm.addItem(2430009, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10263e")
    public static void q10263e(ScriptManager sm) {
        // Quest 10263 - Making Pure Perfume 4 (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032277 = 4032277;

        if (!sm.hasItem(QUEST_ITEM_4032277, 30)) {
            sm.sayOk("You need 30 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032277, 30);
            sm.forceCompleteQuest(10263);
            sm.addItem(4032277, -30); // Reward item
            sm.addItem(2430009, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10264e")
    public static void q10264e(ScriptManager sm) {
        // Quest 10264 - Defeating the Hidden Monster (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032284 = 4032284;

        if (!sm.hasItem(QUEST_ITEM_4032284, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032284, 1);
            sm.forceCompleteQuest(10264);
            sm.addItem(1012146, 1); // Reward item
            sm.addItem(4032284, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10264s")
    public static void q10264s(ScriptManager sm) {
        // Quest 10264 - Defeating the Hidden Monster (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10264);
            sm.addItem(2430009, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10265e")
    public static void q10265e(ScriptManager sm) {
        // Quest 10265 - Defeating the Hidden Monster (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032285 = 4032285;

        if (!sm.hasItem(QUEST_ITEM_4032285, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032285, 1);
            sm.forceCompleteQuest(10265);
            sm.addItem(1012146, 1); // Reward item
            sm.addItem(4032285, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10265s")
    public static void q10265s(ScriptManager sm) {
        // Quest 10265 - Defeating the Hidden Monster (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10265);
            sm.addItem(2430009, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10266e")
    public static void q10266e(ScriptManager sm) {
        // Quest 10266 - Defeating the Hidden Monster (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032286 = 4032286;

        if (!sm.hasItem(QUEST_ITEM_4032286, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032286, 1);
            sm.forceCompleteQuest(10266);
            sm.addItem(1012146, 1); // Reward item
            sm.addItem(4032286, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10266s")
    public static void q10266s(ScriptManager sm) {
        // Quest 10266 - Defeating the Hidden Monster (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10266);
            sm.addItem(2430009, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10267e")
    public static void q10267e(ScriptManager sm) {
        // Quest 10267 - Defeating the Hidden Monster (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032287 = 4032287;

        if (!sm.hasItem(QUEST_ITEM_4032287, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032287, 1);
            sm.forceCompleteQuest(10267);
            sm.addItem(1012146, 1); // Reward item
            sm.addItem(4032287, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10267s")
    public static void q10267s(ScriptManager sm) {
        // Quest 10267 - Defeating the Hidden Monster (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10267);
            sm.addItem(2430009, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10268e")
    public static void q10268e(ScriptManager sm) {
        // Quest 10268 - Gaga's Love Letter 1 (END)
        // NPC: 9000021

        final int QUEST_ITEM_4032272 = 4032272;
        final int QUEST_ITEM_4032273 = 4032273;

        if (!sm.hasItem(QUEST_ITEM_4032272, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032273, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032272, 10);
            sm.removeItem(QUEST_ITEM_4032273, 10);
            sm.forceCompleteQuest(10268);
            sm.addItem(4032272, -10); // Reward item
            sm.addItem(4032273, -10); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10270e")
    public static void q10270e(ScriptManager sm) {
        // Quest 10270 - Gaga's Love Letter 2 (END)
        // NPC: 9000021

        final int QUEST_ITEM_4032278 = 4032278;
        final int QUEST_ITEM_4032281 = 4032281;

        if (!sm.hasItem(QUEST_ITEM_4032278, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032281, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032278, 10);
            sm.removeItem(QUEST_ITEM_4032281, 10);
            sm.forceCompleteQuest(10270);
            sm.addItem(4032281, -10); // Reward item
            sm.addItem(4032278, -10); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10271e")
    public static void q10271e(ScriptManager sm) {
        // Quest 10271 - Gaga's Love Letter 3 (END)
        // NPC: 9000021

        final int QUEST_ITEM_4032279 = 4032279;
        final int QUEST_ITEM_4032282 = 4032282;

        if (!sm.hasItem(QUEST_ITEM_4032279, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032282, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032279, 10);
            sm.removeItem(QUEST_ITEM_4032282, 10);
            sm.forceCompleteQuest(10271);
            sm.addItem(4032282, -10); // Reward item
            sm.addItem(4032279, -10); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10272e")
    public static void q10272e(ScriptManager sm) {
        // Quest 10272 - Gaga's Love Letter 4 (END)
        // NPC: 9000021

        final int QUEST_ITEM_4032280 = 4032280;
        final int QUEST_ITEM_4032283 = 4032283;

        if (!sm.hasItem(QUEST_ITEM_4032280, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_4032283, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032280, 10);
            sm.removeItem(QUEST_ITEM_4032283, 10);
            sm.forceCompleteQuest(10272);
            sm.addItem(4032283, -10); // Reward item
            sm.addItem(4032280, -10); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10274e")
    public static void q10274e(ScriptManager sm) {
        // Quest 10274 - MapleStory 5th Anniversary Party Preparation. (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032298 = 4032298;

        if (!sm.hasItem(QUEST_ITEM_4032298, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032298, 20);
            sm.forceCompleteQuest(10274);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10275e")
    public static void q10275e(ScriptManager sm) {
        // Quest 10275 - MapleStory 5th Anniversary Party Preparation! (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032299 = 4032299;

        if (!sm.hasItem(QUEST_ITEM_4032299, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032299, 20);
            sm.forceCompleteQuest(10275);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10276e")
    public static void q10276e(ScriptManager sm) {
        // Quest 10276 - MapleStory 5th Anniversary Party Preparation!! (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032300 = 4032300;

        if (!sm.hasItem(QUEST_ITEM_4032300, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032300, 20);
            sm.forceCompleteQuest(10276);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10277e")
    public static void q10277e(ScriptManager sm) {
        // Quest 10277 - MapleStory 5th Anniversary Party Preparation!!! (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032301 = 4032301;

        if (!sm.hasItem(QUEST_ITEM_4032301, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032301, 20);
            sm.forceCompleteQuest(10277);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10278e")
    public static void q10278e(ScriptManager sm) {
        // Quest 10278 - Happy 5th Anniversary (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10278);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10279e")
    public static void q10279e(ScriptManager sm) {
        // Quest 10279 - Happy 5th Anniversary! (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10279);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10280e")
    public static void q10280e(ScriptManager sm) {
        // Quest 10280 - Happy 5th Anniversary!! (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10280);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10281e")
    public static void q10281e(ScriptManager sm) {
        // Quest 10281 - Happy 5th Anniversary!!! (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10281);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10282e")
    public static void q10282e(ScriptManager sm) {
        // Quest 10282 - Portrait of a Popular Monster: Orange Mushroom (END)
        // NPC: 9010010

        final int QUEST_ITEM_4220148 = 4220148;

        if (!sm.hasItem(QUEST_ITEM_4220148, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4220148, 1);
            sm.forceCompleteQuest(10282);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10282s")
    public static void q10282s(ScriptManager sm) {
        // Quest 10282 - Portrait of a Popular Monster: Orange Mushroom (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10282);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10283e")
    public static void q10283e(ScriptManager sm) {
        // Quest 10283 - Portrait of a Popular Monster: Octopus (END)
        // NPC: 9010010

        final int QUEST_ITEM_4220149 = 4220149;

        if (!sm.hasItem(QUEST_ITEM_4220149, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4220149, 1);
            sm.forceCompleteQuest(10283);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10283s")
    public static void q10283s(ScriptManager sm) {
        // Quest 10283 - Portrait of a Popular Monster: Octopus (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10283);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10284e")
    public static void q10284e(ScriptManager sm) {
        // Quest 10284 - Portrait of a Popular Monster: Yeti (END)
        // NPC: 9010010

        final int QUEST_ITEM_4220150 = 4220150;

        if (!sm.hasItem(QUEST_ITEM_4220150, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4220150, 1);
            sm.forceCompleteQuest(10284);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10284s")
    public static void q10284s(ScriptManager sm) {
        // Quest 10284 - Portrait of a Popular Monster: Yeti (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10284);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10287s")
    public static void q10287s(ScriptManager sm) {
        // Quest 10287 - Aramia's Golden Maple Leaf (START)
        // NPC: 9000055
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10287);
            sm.addItem(4001168, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10300e")
    public static void q10300e(ScriptManager sm) {
        // Quest 10300 - Making the Witch's Secure Broomstick (END)
        // NPC: 9010020

        final int QUEST_ITEM_4032348 = 4032348;

        if (!sm.hasItem(QUEST_ITEM_4032348, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032348, 20);
            sm.forceCompleteQuest(10300);
            sm.addItem(4032348, -20); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10301e")
    public static void q10301e(ScriptManager sm) {
        // Quest 10301 - Making the Witch's Solid Broomstick (END)
        // NPC: 9010020

        final int QUEST_ITEM_4032349 = 4032349;

        if (!sm.hasItem(QUEST_ITEM_4032349, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032349, 20);
            sm.forceCompleteQuest(10301);
            sm.addItem(4032349, -20); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10302e")
    public static void q10302e(ScriptManager sm) {
        // Quest 10302 - Making the Witch's Sturdy Broomstick (END)
        // NPC: 9010020

        final int QUEST_ITEM_4032350 = 4032350;

        if (!sm.hasItem(QUEST_ITEM_4032350, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032350, 20);
            sm.forceCompleteQuest(10302);
            sm.addItem(4032350, -20); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10311e")
    public static void q10311e(ScriptManager sm) {
        // Quest 10311 - A Petrified Mouse (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10311);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10312e")
    public static void q10312e(ScriptManager sm) {
        // Quest 10312 - Gaga's Glasses (END)
        // NPC: 9000021
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10312);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10313e")
    public static void q10313e(ScriptManager sm) {
        // Quest 10313 - Cassandra's Crystal Ball (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10313);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10314e")
    public static void q10314e(ScriptManager sm) {
        // Quest 10314 - A Shiny Watch (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10314);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10315e")
    public static void q10315e(ScriptManager sm) {
        // Quest 10315 - A Nice Cleat (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10315);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10316e")
    public static void q10316e(ScriptManager sm) {
        // Quest 10316 - Quest Completion Book (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10316);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10317e")
    public static void q10317e(ScriptManager sm) {
        // Quest 10317 - A Pretty Hair-Tie (END)
        // NPC: 1022002
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10317);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10318e")
    public static void q10318e(ScriptManager sm) {
        // Quest 10318 - An Old Shoe (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10318);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10319e")
    public static void q10319e(ScriptManager sm) {
        // Quest 10319 - Artifact Hunt 1000 points acquired! (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10319);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10319s")
    public static void q10319s(ScriptManager sm) {
        // Quest 10319 - Artifact Hunt 1000 points acquired! (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10319);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10320e")
    public static void q10320e(ScriptManager sm) {
        // Quest 10320 - Artifact Hunt 2500 points acquired! (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10320);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10320s")
    public static void q10320s(ScriptManager sm) {
        // Quest 10320 - Artifact Hunt 2500 points acquired! (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10320);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10321e")
    public static void q10321e(ScriptManager sm) {
        // Quest 10321 - Artifact Hunt 4000 points acquired! (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10321);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10321s")
    public static void q10321s(ScriptManager sm) {
        // Quest 10321 - Artifact Hunt 4000 points acquired! (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10321);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10324e")
    public static void q10324e(ScriptManager sm) {
        // Quest 10324 - Vaughn Lee's Cleat (END)
        // NPC: 2110003

        final int QUEST_ITEM_4001307 = 4001307;

        if (!sm.hasItem(QUEST_ITEM_4001307, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001307, 1);
            sm.forceCompleteQuest(10324);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10325e")
    public static void q10325e(ScriptManager sm) {
        // Quest 10325 - Stan's Cleat (END)
        // NPC: 2110004

        final int QUEST_ITEM_4001307 = 4001307;

        if (!sm.hasItem(QUEST_ITEM_4001307, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001307, 1);
            sm.forceCompleteQuest(10325);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10326e")
    public static void q10326e(ScriptManager sm) {
        // Quest 10326 - Louie's Cleat (END)
        // NPC: 2110002

        final int QUEST_ITEM_4001307 = 4001307;

        if (!sm.hasItem(QUEST_ITEM_4001307, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001307, 1);
            sm.forceCompleteQuest(10326);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10327e")
    public static void q10327e(ScriptManager sm) {
        // Quest 10327 - Corba's Watch (END)
        // NPC: 2082003

        final int QUEST_ITEM_4001306 = 4001306;

        if (!sm.hasItem(QUEST_ITEM_4001306, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001306, 1);
            sm.forceCompleteQuest(10327);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10329s")
    public static void q10329s(ScriptManager sm) {
        // Quest 10329 - Start the Artifact Hunt (START)
        // NPC: 9000021
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10329);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10330e")
    public static void q10330e(ScriptManager sm) {
        // Quest 10330 - Challenge! Honorable Mesoranger (END)
        // NPC: 9000062
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10330);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10331e")
    public static void q10331e(ScriptManager sm) {
        // Quest 10331 - Special Order! Find Agent E! (END)
        // NPC: 9000063
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10331);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10331s")
    public static void q10331s(ScriptManager sm) {
        // Quest 10331 - Special Order! Find Agent E! (START)
        // NPC: 9000063
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10331);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10332e")
    public static void q10332e(ScriptManager sm) {
        // Quest 10332 - Special Order! Find Agent S! (END)
        // NPC: 9000064
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10332);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10332s")
    public static void q10332s(ScriptManager sm) {
        // Quest 10332 - Special Order! Find Agent S! (START)
        // NPC: 9000064
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10332);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10333s")
    public static void q10333s(ScriptManager sm) {
        // Quest 10333 - Special Order! Find Agent O! (START)
        // NPC: 9000065
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10333);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10340s")
    public static void q10340s(ScriptManager sm) {
        // Quest 10340 - They're Coming Back! (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10340);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10341s")
    public static void q10341s(ScriptManager sm) {
        // Quest 10341 - The Revival of the Arans (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10341);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10342e")
    public static void q10342e(ScriptManager sm) {
        // Quest 10342 - Vague Aran Memories (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032366 = 4032366;

        if (!sm.hasItem(QUEST_ITEM_4032366, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032366, 20);
            sm.forceCompleteQuest(10342);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10344e")
    public static void q10344e(ScriptManager sm) {
        // Quest 10344 - Dim Aran Memories (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032367 = 4032367;

        if (!sm.hasItem(QUEST_ITEM_4032367, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032367, 20);
            sm.forceCompleteQuest(10344);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10345e")
    public static void q10345e(ScriptManager sm) {
        // Quest 10345 - Signs of Their Revival (END)
        // NPC: 9000068
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10345);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10346e")
    public static void q10346e(ScriptManager sm) {
        // Quest 10346 - Faint Aran Memories (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032368 = 4032368;

        if (!sm.hasItem(QUEST_ITEM_4032368, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032368, 20);
            sm.forceCompleteQuest(10346);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10347e")
    public static void q10347e(ScriptManager sm) {
        // Quest 10347 - Wolves Waiting for their Masters (END)
        // NPC: 9000067
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10347);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10348e")
    public static void q10348e(ScriptManager sm) {
        // Quest 10348 - Cloudy Aran Memories (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032369 = 4032369;

        if (!sm.hasItem(QUEST_ITEM_4032369, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032369, 20);
            sm.forceCompleteQuest(10348);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10349e")
    public static void q10349e(ScriptManager sm) {
        // Quest 10349 - Preparing for Their Arrival (END)
        // NPC: 9010010

        final int QUEST_ITEM_1442000 = 1442000;

        if (!sm.hasItem(QUEST_ITEM_1442000, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_1442000, 1);
            sm.forceCompleteQuest(10349);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10350e")
    public static void q10350e(ScriptManager sm) {
        // Quest 10350 - Lingering Aran Memories (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032370 = 4032370;

        if (!sm.hasItem(QUEST_ITEM_4032370, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032370, 20);
            sm.forceCompleteQuest(10350);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10351s")
    public static void q10351s(ScriptManager sm) {
        // Quest 10351 - The Memory of the Heroes (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10351);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10352e")
    public static void q10352e(ScriptManager sm) {
        // Quest 10352 - Flickering Aran Memories (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032371 = 4032371;

        if (!sm.hasItem(QUEST_ITEM_4032371, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032371, 20);
            sm.forceCompleteQuest(10352);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10353e")
    public static void q10353e(ScriptManager sm) {
        // Quest 10353 - The Hidden Meaning Behind the Memory Fragments (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10353);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10354s")
    public static void q10354s(ScriptManager sm) {
        // Quest 10354 - Who Deserves Cassandra's Album? (START)
        // NPC: 9040000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10354);
            sm.addItem(4001316, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10355e")
    public static void q10355e(ScriptManager sm) {
        // Quest 10355 - Delivering Cassandra's Album (END)
        // NPC: 9000021

        final int QUEST_ITEM_4001316 = 4001316;

        if (!sm.hasItem(QUEST_ITEM_4001316, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001316, 1);
            sm.forceCompleteQuest(10355);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10356e")
    public static void q10356e(ScriptManager sm) {
        // Quest 10356 - Delivering Cassandra's Album (END)
        // NPC: 9010010

        final int QUEST_ITEM_4001316 = 4001316;

        if (!sm.hasItem(QUEST_ITEM_4001316, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4001316, 1);
            sm.forceCompleteQuest(10356);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10360e")
    public static void q10360e(ScriptManager sm) {
        // Quest 10360 - Aran Welcome Celebration (END)
        // NPC: 9010010

        final int QUEST_ITEM_3994139 = 3994139;

        if (!sm.hasItem(QUEST_ITEM_3994139, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3994139, 1);
            sm.forceCompleteQuest(10360);
            sm.addItem(3994139, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10370e")
    public static void q10370e(ScriptManager sm) {
        // Quest 10370 - Find the Master of Combos (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10370);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10380s")
    public static void q10380s(ScriptManager sm) {
        // Quest 10380 - Aran's Return (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10380);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10394e")
    public static void q10394e(ScriptManager sm) {
        // Quest 10394 - Perfect Pitch - Level Up Event (END)
        // NPC: 9010000
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10394);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10395e")
    public static void q10395e(ScriptManager sm) {
        // Quest 10395 - 류호의 이주민 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10395);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10395s")
    public static void q10395s(ScriptManager sm) {
        // Quest 10395 - 류호의 이주민 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10395);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10396e")
    public static void q10396e(ScriptManager sm) {
        // Quest 10396 - 류호의 도전자 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10396);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10396s")
    public static void q10396s(ScriptManager sm) {
        // Quest 10396 - 류호의 도전자 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10396);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10397e")
    public static void q10397e(ScriptManager sm) {
        // Quest 10397 - 류호의 정착자 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10397);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10397s")
    public static void q10397s(ScriptManager sm) {
        // Quest 10397 - 류호의 정착자 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10397);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10398e")
    public static void q10398e(ScriptManager sm) {
        // Quest 10398 - 류호의 개척자 (END)
        // NPC: 9010010
        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(10398);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10398s")
    public static void q10398s(ScriptManager sm) {
        // Quest 10398 - 류호의 개척자 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10398);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10400s")
    public static void q10400s(ScriptManager sm) {
        // Quest 10400 - The 2010 Winter King / Winter Queen Event! (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10400);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10401s")
    public static void q10401s(ScriptManager sm) {
        // Quest 10401 - The True Winter King / Winter Queen Event of 2010! (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10401);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10415e")
    public static void q10415e(ScriptManager sm) {
        // Quest 10415 - Winter Bingo (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032418 = 4032418;

        if (!sm.hasItem(QUEST_ITEM_4032418, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032418, 20);
            sm.forceCompleteQuest(10415);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10417e")
    public static void q10417e(ScriptManager sm) {
        // Quest 10417 - Winter Bingo (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032419 = 4032419;

        if (!sm.hasItem(QUEST_ITEM_4032419, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032419, 20);
            sm.forceCompleteQuest(10417);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10418e")
    public static void q10418e(ScriptManager sm) {
        // Quest 10418 - Winter Bingo (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032420 = 4032420;

        if (!sm.hasItem(QUEST_ITEM_4032420, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032420, 20);
            sm.forceCompleteQuest(10418);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10419e")
    public static void q10419e(ScriptManager sm) {
        // Quest 10419 - Winter Bingo (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032421 = 4032421;

        if (!sm.hasItem(QUEST_ITEM_4032421, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032421, 20);
            sm.forceCompleteQuest(10419);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10420e")
    public static void q10420e(ScriptManager sm) {
        // Quest 10420 - Winter Bingo (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032422 = 4032422;

        if (!sm.hasItem(QUEST_ITEM_4032422, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032422, 20);
            sm.forceCompleteQuest(10420);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10450s")
    public static void q10450s(ScriptManager sm) {
        // Quest 10450 - Rainbow Week: Red Monday Magic (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10450);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10451s")
    public static void q10451s(ScriptManager sm) {
        // Quest 10451 - Rainbow Week: Red Monday Magic (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10451);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10452e")
    public static void q10452e(ScriptManager sm) {
        // Quest 10452 - Rainbow Week: Yellow Wednesday Magic (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032434 = 4032434;

        if (!sm.hasItem(QUEST_ITEM_4032434, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032434, 10);
            sm.forceCompleteQuest(10452);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10452s")
    public static void q10452s(ScriptManager sm) {
        // Quest 10452 - Rainbow Week: Yellow Wednesday Magic (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10452);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10453e")
    public static void q10453e(ScriptManager sm) {
        // Quest 10453 - Rainbow Week: Yellow Wednesday Magic (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032434 = 4032434;

        if (!sm.hasItem(QUEST_ITEM_4032434, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032434, 10);
            sm.forceCompleteQuest(10453);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10454e")
    public static void q10454e(ScriptManager sm) {
        // Quest 10454 - Rainbow Week: Yellow Wednesday Magic (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032434 = 4032434;

        if (!sm.hasItem(QUEST_ITEM_4032434, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032434, 10);
            sm.forceCompleteQuest(10454);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10455e")
    public static void q10455e(ScriptManager sm) {
        // Quest 10455 - Rainbow Week: Yellow Wednesday Magic (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032434 = 4032434;

        if (!sm.hasItem(QUEST_ITEM_4032434, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032434, 10);
            sm.forceCompleteQuest(10455);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10456e")
    public static void q10456e(ScriptManager sm) {
        // Quest 10456 - Rainbow Week: Yellow Wednesday Magic (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032434 = 4032434;

        if (!sm.hasItem(QUEST_ITEM_4032434, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032434, 10);
            sm.forceCompleteQuest(10456);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10457e")
    public static void q10457e(ScriptManager sm) {
        // Quest 10457 - Rainbow Week: Yellow Wednesday Magic (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032434 = 4032434;

        if (!sm.hasItem(QUEST_ITEM_4032434, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032434, 10);
            sm.forceCompleteQuest(10457);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10470s")
    public static void q10470s(ScriptManager sm) {
        // Quest 10470 - 별별 페스티발 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10470);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10480s")
    public static void q10480s(ScriptManager sm) {
        // Quest 10480 - The Birth of a New Hero (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10480);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10481s")
    public static void q10481s(ScriptManager sm) {
        // Quest 10481 - The Maple Administrator's Congratulations (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10481);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10490s")
    public static void q10490s(ScriptManager sm) {
        // Quest 10490 - 카산드라의 심술 (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10490);
            sm.addItem(3994184, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10491e")
    public static void q10491e(ScriptManager sm) {
        // Quest 10491 - 가가 골탕 먹이기 (END)
        // NPC: 9000021

        final int QUEST_ITEM_3994185 = 3994185;

        if (!sm.hasItem(QUEST_ITEM_3994185, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3994185, 1);
            sm.forceCompleteQuest(10491);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10492e")
    public static void q10492e(ScriptManager sm) {
        // Quest 10492 - 메이플운영자 골탕먹이기 (END)
        // NPC: 9010000

        final int QUEST_ITEM_3994185 = 3994185;

        if (!sm.hasItem(QUEST_ITEM_3994185, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3994185, 1);
            sm.forceCompleteQuest(10492);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10493e")
    public static void q10493e(ScriptManager sm) {
        // Quest 10493 - 클리프 골탕먹이기 (END)
        // NPC: 2001000

        final int QUEST_ITEM_3994185 = 3994185;

        if (!sm.hasItem(QUEST_ITEM_3994185, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3994185, 1);
            sm.forceCompleteQuest(10493);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10494e")
    public static void q10494e(ScriptManager sm) {
        // Quest 10494 - 토르 골탕먹이기 (END)
        // NPC: 2002002

        final int QUEST_ITEM_3994185 = 3994185;

        if (!sm.hasItem(QUEST_ITEM_3994185, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3994185, 1);
            sm.forceCompleteQuest(10494);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10497s")
    public static void q10497s(ScriptManager sm) {
        // Quest 10497 - 이상한 물약을 다시 만들어 주세요. (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10497);
            sm.addItem(3994184, 1); // Quest item
            sm.addItem(3994185, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10500s")
    public static void q10500s(ScriptManager sm) {
        // Quest 10500 - A Sign of the Dragon Master's Return (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10500);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10510e")
    public static void q10510e(ScriptManager sm) {
        // Quest 10510 - Evan Everyday Event (END)
        // NPC: 9010010

        final int QUEST_ITEM_3994187 = 3994187;

        if (!sm.hasItem(QUEST_ITEM_3994187, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3994187, 1);
            sm.forceCompleteQuest(10510);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10510s")
    public static void q10510s(ScriptManager sm) {
        // Quest 10510 - Evan Everyday Event (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10510);
            sm.addItem(3994187, 1); // Quest item
            sm.addItem(3994186, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10514s")
    public static void q10514s(ScriptManager sm) {
        // Quest 10514 - Evan Launch Commemoration 2PM Event (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10514);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10516s")
    public static void q10516s(ScriptManager sm) {
        // Quest 10516 - Evan Launch Commemoration 2PM Event (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10516);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10575e")
    public static void q10575e(ScriptManager sm) {
        // Quest 10575 - Explorer Level +30 Challenge (END)
        // NPC: 9010010

        final int QUEST_ITEM_1112427 = 1112427;
        final int QUEST_ITEM_1112428 = 1112428;
        final int QUEST_ITEM_1112429 = 1112429;

        if (!sm.hasItem(QUEST_ITEM_1112427, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_1112428, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }
        if (!sm.hasItem(QUEST_ITEM_1112429, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_1112427, 1);
            sm.removeItem(QUEST_ITEM_1112428, 1);
            sm.removeItem(QUEST_ITEM_1112429, 1);
            sm.forceCompleteQuest(10575);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10575s")
    public static void q10575s(ScriptManager sm) {
        // Quest 10575 - Explorer Level +30 Challenge (START)
        // NPC: 9010010
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10575);
            sm.addItem(1112427, 1); // Quest item
            sm.addItem(1112428, 1); // Quest item
            sm.addItem(1112429, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10579s")
    public static void q10579s(ScriptManager sm) {
        // Quest 10579 - New Function: Party Search (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10579);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10594e")
    public static void q10594e(ScriptManager sm) {
        // Quest 10594 - Secret Part-Time Job (END)
        // NPC: 9010010

        final int QUEST_ITEM_2430052 = 2430052;

        if (!sm.hasItem(QUEST_ITEM_2430052, 5)) {
            sm.sayOk("You need 5 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2430052, 5);
            sm.forceCompleteQuest(10594);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10610s")
    public static void q10610s(ScriptManager sm) {
        // Quest 10610 - Reach Dual Blade Lv. 20! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10610);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10611s")
    public static void q10611s(ScriptManager sm) {
        // Quest 10611 - Reach Dual Blade Lv. 30! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10611);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10612s")
    public static void q10612s(ScriptManager sm) {
        // Quest 10612 - Reach Dual Blade Lv. 40! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10612);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10613s")
    public static void q10613s(ScriptManager sm) {
        // Quest 10613 - Reach Dual Blade Lv. 50! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10613);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10614s")
    public static void q10614s(ScriptManager sm) {
        // Quest 10614 - Reach Dual Blade Lv. 60! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10614);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10615s")
    public static void q10615s(ScriptManager sm) {
        // Quest 10615 - Reach Dual Blade Lv. 70! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10615);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10616s")
    public static void q10616s(ScriptManager sm) {
        // Quest 10616 - Reach Dual Blade Lv. 80! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10616);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10617s")
    public static void q10617s(ScriptManager sm) {
        // Quest 10617 - Reach Dual Blade Lv. 90! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10617);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10618s")
    public static void q10618s(ScriptManager sm) {
        // Quest 10618 - Reach Dual Blade Lv. 100! (START)
        // NPC: 9010000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(10618);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q10619e")
    public static void q10619e(ScriptManager sm) {
        // Quest 10619 - Dual Blade - Everyday 2X Buff!! (END)
        // NPC: 9010000

        final int QUEST_ITEM_3994193 = 3994193;

        if (!sm.hasItem(QUEST_ITEM_3994193, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_3994193, 1);
            sm.forceCompleteQuest(10619);
            sm.addItem(3994193, -1); // Reward item
            sm.addItem(2022694, 1); // Reward item
            sm.addItem(2450018, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10620e")
    public static void q10620e(ScriptManager sm) {
        // Quest 10620 - Dual Blade: Top Secret (END)
        // NPC: 9010010

        final int QUEST_ITEM_4032630 = 4032630;

        if (!sm.hasItem(QUEST_ITEM_4032630, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032630, 20);
            sm.forceCompleteQuest(10620);
            sm.addItem(4032630, -20); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q10720s")
    public static void q10720s(ScriptManager sm) {
        // Quest 10720 - Event Quest (START)
        sm.forceStartQuest(10720);
    }


    @Script("q10818s")
    public static void q10818s(ScriptManager sm) {
        // Quest 10818 - Event Quest (START)
        sm.forceStartQuest(10818);
    }


    @Script("q10827e")
    public static void q10827e(ScriptManager sm) {
        // Quest 10827 - Event Quest (END)
        sm.forceCompleteQuest(10827);
    }


    @Script("q10828e")
    public static void q10828e(ScriptManager sm) {
        // Quest 10828 - Event Quest (END)
        sm.forceCompleteQuest(10828);
    }


    @Script("q10828s")
    public static void q10828s(ScriptManager sm) {
        // Quest 10828 - Event Quest (START)
        sm.forceStartQuest(10828);
    }


    @Script("q10829e")
    public static void q10829e(ScriptManager sm) {
        // Quest 10829 - Event Quest (END)
        sm.forceCompleteQuest(10829);
    }


    @Script("q10830e")
    public static void q10830e(ScriptManager sm) {
        // Quest 10830 - Event Quest (END)
        sm.forceCompleteQuest(10830);
    }


    @Script("q10831e")
    public static void q10831e(ScriptManager sm) {
        // Quest 10831 - Event Quest (END)
        sm.forceCompleteQuest(10831);
    }


    @Script("q10832e")
    public static void q10832e(ScriptManager sm) {
        // Quest 10832 - Event Quest (END)
        sm.forceCompleteQuest(10832);
    }


    @Script("q10833e")
    public static void q10833e(ScriptManager sm) {
        // Quest 10833 - Event Quest (END)
        sm.forceCompleteQuest(10833);
    }


    @Script("q10834e")
    public static void q10834e(ScriptManager sm) {
        // Quest 10834 - Event Quest (END)
        sm.forceCompleteQuest(10834);
    }


    @Script("q10835e")
    public static void q10835e(ScriptManager sm) {
        // Quest 10835 - Event Quest (END)
        sm.forceCompleteQuest(10835);
    }


    @Script("q10836e")
    public static void q10836e(ScriptManager sm) {
        // Quest 10836 - Event Quest (END)
        sm.forceCompleteQuest(10836);
    }


    @Script("q10837e")
    public static void q10837e(ScriptManager sm) {
        // Quest 10837 - Event Quest (END)
        sm.forceCompleteQuest(10837);
    }


    @Script("q10838e")
    public static void q10838e(ScriptManager sm) {
        // Quest 10838 - Event Quest (END)
        sm.forceCompleteQuest(10838);
    }


    @Script("q10839e")
    public static void q10839e(ScriptManager sm) {
        // Quest 10839 - Event Quest (END)
        sm.forceCompleteQuest(10839);
    }


    @Script("q10840e")
    public static void q10840e(ScriptManager sm) {
        // Quest 10840 - Event Quest (END)
        sm.forceCompleteQuest(10840);
    }


    @Script("q10841e")
    public static void q10841e(ScriptManager sm) {
        // Quest 10841 - Event Quest (END)
        sm.forceCompleteQuest(10841);
    }


    @Script("q10842e")
    public static void q10842e(ScriptManager sm) {
        // Quest 10842 - Event Quest (END)
        sm.forceCompleteQuest(10842);
    }


    @Script("q10842s")
    public static void q10842s(ScriptManager sm) {
        // Quest 10842 - Event Quest (START)
        sm.forceStartQuest(10842);
    }


    @Script("q10845e")
    public static void q10845e(ScriptManager sm) {
        // Quest 10845 - Event Quest (END)
        sm.forceCompleteQuest(10845);
    }


    @Script("q10846e")
    public static void q10846e(ScriptManager sm) {
        // Quest 10846 - Event Quest (END)
        sm.forceCompleteQuest(10846);
    }


    @Script("q10848e")
    public static void q10848e(ScriptManager sm) {
        // Quest 10848 - Event Quest (END)
        sm.forceCompleteQuest(10848);
    }


    @Script("q10848s")
    public static void q10848s(ScriptManager sm) {
        // Quest 10848 - Event Quest (START)
        sm.forceStartQuest(10848);
    }

}