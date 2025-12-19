package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;

import java.util.List;
import java.util.Map;

public class ElNath extends ScriptHandler {
    @Script("refine_elnath")
    public static void refineElNath(ScriptManager sm) {
        // Vogen
        if (sm.askYesNo("Looks like you have quite a bit of ores and jewels with you. For a small service fee, I can refine them into the materials needed to create shields or weapons. I've been doing this for 50 years, so it's a piece of cake! What do you think? You want me to do it?")) {
            final int answer = sm.askMenu("Good decision! Give me the ores and the service fee, and I can refine them so that they'll be of some use. Before doing so, don't forget to check your etc. inventory to make sure that you have enough free space for the new items. Let's see, what would you like me to do?", Map.of(
                    0, "Refine the ore of a mineral",
                    1, "Refine the ore of a jewel",
                    2, "Refine a rare gem",
                    3, "Refine a crystal",
                    4, "Create materials",
                    5, "Create arrows"
            ));

            switch (answer) {
                case 0:
                    final int answer0 = sm.askMenu("Which of these minerals would you like to make?", Map.of(
                            0, "#t4011000#",
                            1, "#t4011001#",
                            2, "#t4011002#",
                            3, "#t4011003#",
                            4, "#t4011004#",
                            5, "#t4011005#",
                            6, "#t4011006#"
                    ));

                    switch (answer0) {
                        case 0:
                            handleRefineOres(sm, 1, "#t4011000#", "#v4010000#", "#t4010000#s", 300);
                            break;
                        case 1:
                            handleRefineOres(sm, 2, "#t4011001#", "#v4010001#", "#t4010001#s", 300);
                            break;
                        case 2:
                            handleRefineOres(sm, 3, "#t4011002#", "#v4010002#", "#t4010002#s", 300);
                            break;
                        case 3:
                            handleRefineOres(sm, 4, "#t4011003#", "#v4010003#", "#t4010003#s", 500);
                            break;
                        case 4:
                            handleRefineOres(sm, 5, "#t4011004#", "#v4010004#", "#t4010004#s", 500);
                            break;
                        case 5:
                            handleRefineOres(sm, 6, "#t4011005#", "#v4010005#", "#t4010005#s", 500);
                            break;
                        case 6:
                            handleRefineOres(sm, 7, "#t4011006#", "#v4010006#", "#t4010006#s", 800);
                            break;
                    }
                    break;
                case 1:
                    final int answer1 = sm.askMenu("Which jewel would you like to refine?", Map.of(
                            0, "#t4021000#",
                            1, "#t4021001#",
                            2, "#t4021002#",
                            3, "#t4021003#",
                            4, "#t4021004#",
                            5, "#t4021005#",
                            6, "#t4021006#",
                            7, "#t4021007#",
                            8, "#t4021008#"
                    ));

                    switch(answer1) {
                        case 0:
                            handleRefineOres(sm, 100, "#t4021000#", "#v4020000#", "#t4020000#s", 500);
                            break;
                        case 1:
                            handleRefineOres(sm, 101, "#t4021001#", "#v4020001#", "#t4020001#s", 500);
                            break;
                        case 2:
                            handleRefineOres(sm, 102, "#t4021002#", "#v4020002#", "#t4020002#s", 500);
                            break;
                        case 3:
                            handleRefineOres(sm, 103, "#t4021003#", "#v4020003#", "#t4020003#s", 500);
                            break;
                        case 4:
                            handleRefineOres(sm, 104, "#t4021004#", "#v4020004#", "#t4020004#s", 500);
                            break;
                        case 5:
                            handleRefineOres(sm, 105, "#t4021005#", "#v4020005#", "#t4020005#s", 500);
                            break;
                        case 6:
                            handleRefineOres(sm, 106, "#t4021006#", "#v4020006#", "#t4020006#s", 500);
                            break;
                        case 7:
                            handleRefineOres(sm, 107, "#t4021007#", "#v4020007#", "#t4020007#s", 1000);
                            break;
                        case 8:
                            handleRefineOres(sm, 108, "#t4021008#", "#v4020008#", "#t4020008#s", 3000);
                            break;
                    }
                    break;
                case 2:
                    final int answer2 = sm.askMenu("Yes, I can refine even rare gems. I may need a lot of material to do this, but it is possible. Which gem would you like to refine?", Map.of(
                            0, "#t4011007#",
                            1, "#t4021009#"
                    ));

                    switch(answer2) {
                        case 0:
                            handleRefineRareGem(sm, 200, "#t4011007#", "refined #t4011000#, #t4011001#, #t4011002#, #t4011003#, #t4011004#, #t4011005#, #t4011006#", 10000);
                            break;
                        case 1:
                            handleRefineRareGem(sm, 201, "#t4021009#", "refined #t4021000#, #t4021001#, #t4021002#, #t4021003#, #t4021004#, #t4021005#, #t4021006#, #t4021007#, #t4021008#", 15000);
                            break;
                    }
                    break;
                case 3:
                    final int answer3 = sm.askMenu("Hmmm... Do you really have a crystal? I haven't seen one of them in a while, so I don't really believe you, but if you really have one I can refine it and turn it into something useful. So, which crystal would you like to refine?", Map.of(
                            0, "#t4005000#",
                            1, "#t4005001#",
                            2, "#t4005002#",
                            3, "#t4005003#",
                            4, "#t4005004#"
                    ));

                    switch (answer3) {
                        case 0:
                            handleRefineOres(sm, 300, "#t4005000#", "#v4004000#", "#t4004000#", 5000);
                            break;
                        case 1:
                            handleRefineOres(sm, 301, "#t4005001#", "#v4004001#", "#t4004001#", 5000);
                            break;
                        case 2:
                            handleRefineOres(sm, 302, "#t4005002#", "#v4004002#", "#t4004002#", 5000);
                            break;
                        case 3:
                            handleRefineOres(sm, 303, "#t4005003#", "#v4004003#", "#t4004003#", 5000);
                            break;
                        case 4:
                            handleRefineOres(sm, 304, "#t4005004#", "#v4004004#", "#t4004004#", 100000);
                            break;
                    }
                    break;
                case 4:
                    final int answer4 = sm.askMenu("So, you want to create some materials! Let's see, what type of material would you like to make?", Map.of(
                            0, "#bCreate #t4003001# with #t4000003#es#k",
                            1, "#bCreate #t4003001# with #t4000018#s#k",
                            2, "#bCreate #t4003000#s#k"
                    ));

                    switch (answer4) {
                        case 0:
                            handleCreateMaterials(sm, 1, "#t4003001#", "#t4000003#", 10, 1);
                            break;
                        case 1:
                            handleCreateMaterials(sm, 2, "#t4003001#", "#t4000018#s", 5, 1);
                            break;
                        case 2:
                            handleCreateMaterials(sm, 3, "#t4003000#s", "#t4011001#(s) and #t4011000#(s) each", 1, 15);
                            break;
                    }
                    break;
                case 5:
                    final int answer5 = sm.askMenu("So, you want to create arrows! With a strong arrow, you will have a better advantage in battle. Let's see, what kind of arrow would you like me to create?", Map.of(
                            0, "#b#t2060000##k",
                            1, "#b#t2061000##k",
                            2, "#b#t2060001##k",
                            3, "#b#t2061001##k",
                            4, "#b#t2060002##k",
                            5, "#b#t2061002##k"
                    ));

                    sm.message("Creating arrows is not implemented yet.");
            }
        } else {
            sm.sayOk("I understand. Is the service fee too high for you? But understand that I'll be in this town for a long time, so if you ever want to refine anything just bring it to me.");
        }
    }

    public static void handleRefineOres(ScriptManager sm, int index, String makeItem, String needItemIcon, String needItemString, int unitPrice) {
        if (index == 200 || index == 201) {
            final int numOfItems = sm.askNumber("Very good, very good ... how many #b" + makeItem + "s#k would you like to make?", 1, 1, 100);
            final int nPrice = unitPrice * numOfItems;
            if(!sm.askYesNo("Alright, you wanna create #b" + numOfItems + " " + makeItem + "#k(s)?? For that you will need #r" + nPrice + "mesos and " + needItemIcon + " " + numOfItems + " " + needItemString + "#k each. What do you think? Do you really want to do it?")) {
                sm.sayOk("I understand. Is the service fee too high for you? But understand that I'll be in this town for a long time, so if you ever want to refine anything just bring it to me.");
                return;
            }

            // a rare jewel
            if (index == 200) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4011000, numOfItems),
                        Tuple.of(4011001, numOfItems),
                        Tuple.of(4011002, numOfItems),
                        Tuple.of(4011003, numOfItems),
                        Tuple.of(4011004, numOfItems),
                        Tuple.of(4011005, numOfItems),
                        Tuple.of(4011006, numOfItems)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4011007)) return;
            } else {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4021000, numOfItems),
                        Tuple.of(4021001, numOfItems),
                        Tuple.of(4021002, numOfItems),
                        Tuple.of(4021003, numOfItems),
                        Tuple.of(4021004, numOfItems),
                        Tuple.of(4021005, numOfItems),
                        Tuple.of(4021006, numOfItems),
                        Tuple.of(4021007, numOfItems),
                        Tuple.of(4021008, numOfItems)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4021009)) return;
            }

            sm.sayOk("Here! Take #b" + numOfItems + " " + makeItem + "#k(s). It's been 50 years, but I still have my skills. If you need my help in the near future, feel free to drop by.\"");
        } else {
            final int numOfItems = sm.askNumber("To make a " + makeItem + ", I will need the following materials. How many would you like to make?\r\n\r\n#b" + needItemIcon + " 10 " + needItemString + "\r\n" + unitPrice + " mesos#k", 1, 1, 100);
            final int nPrice = unitPrice * numOfItems;
            final int nAllNum = numOfItems * 10;
            if (!sm.askYesNo("You want to make #b" + numOfItems + " " + makeItem + "(s)#k?? Then you will need #r" + nPrice + " mesos and " + needItemIcon + " " + nAllNum + " " + needItemString + "#k(s). What do you think? You wanna do it?")) {
                sm.sayOk("I understand... Is the service fee too high for you? Know that I will be in this town for a long time, so if you ever want to refine anything just bring it to me.");
                return;
            }

            // mineral
            if (index >= 1 && index <= 7) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4010000 + (index - 1), nAllNum)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4011000 + (index - 1))) return;
            }

            // jewel
            if (index >= 100 && index <= 108) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4020000 + (index - 100), nAllNum)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4021000 + (index - 100))) return;
            }

            // crystal
            if (index >= 300 && index <= 304) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4004000 + (index - 300), nAllNum)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4005000 + (index - 300))) return;
            }

            sm.sayOk("Here! Take #b" + numOfItems + " " + makeItem + "(s)#k. It's been 50 years, but I still have my skills. If you need my help in the near future, feel free to drop by.");
        }
    }

    public static void handleRefineRareGem(ScriptManager sm, int index, String makeItem, String needItem, int unitPrice) {
        if (index == 200 || index == 201) {
            final int numOfItems = sm.askNumber("Very good, very good ... how many #b" + makeItem + "s#k would you like to make?", 1, 1, 100);
            final int nPrice = unitPrice * numOfItems;
            if(!sm.askYesNo("Alright, you wanna create #b" + numOfItems + " " + makeItem + "#k(s)?? For that you will need #r" + nPrice + " mesos and " + numOfItems + " " + needItem + "#k each. What do you think? Do you really want to do it?")) {
                sm.sayOk("I understand. Is the service fee too high for you? But understand that I'll be in this town for a long time, so if you ever want to refine anything just bring it to me.");
                return;
            }

            if (index == 200) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4011000, numOfItems),
                        Tuple.of(4011001, numOfItems),
                        Tuple.of(4011002, numOfItems),
                        Tuple.of(4011003, numOfItems),
                        Tuple.of(4011004, numOfItems),
                        Tuple.of(4011005, numOfItems),
                        Tuple.of(4011006, numOfItems)
                );
                if(!handleTransaction(sm, numOfItems, 10000 * numOfItems, removeItems, 4011007)) return;
            } else {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4021000, numOfItems),
                        Tuple.of(4021001, numOfItems),
                        Tuple.of(4021002, numOfItems),
                        Tuple.of(4021003, numOfItems),
                        Tuple.of(4021004, numOfItems),
                        Tuple.of(4021005, numOfItems),
                        Tuple.of(4021006, numOfItems),
                        Tuple.of(4021007, numOfItems),
                        Tuple.of(4021008, numOfItems)
                );
                if(!handleTransaction(sm, numOfItems, 15000 * numOfItems, removeItems, 4021009)) return;
            }
        }
    }

    private static void handleCreateMaterials(ScriptManager sm, int index, String makeItem, String needItem, int needNumber, int itemNumber) {
        var numOfItems = sm.askNumber("I can make #b" + itemNumber + " " + makeItem + "(s) with " + needNumber + " " + needItem + "#k. This one is free, as long as you have the necessary materials, then you're good to go. What do you think? How many would you like to make?", 1, 1, 100);
        var nNeedNum = numOfItems * needNumber;
        var nAllNum = numOfItems * itemNumber;
        if(!sm.askYesNo("Alright, you want to make #b" + makeItem + "#k " + numOfItems + " times? I'm going to need #r" + nNeedNum + " " + needItem + "#k to do it. Do you still want me to make them?")) {
            sm.sayOk("Don't have the materials? You can get something by eliminating the monsters in this area, so work hard on this task...");
            return;
        }

        List<Tuple<Integer, Integer>> removeItems;
        switch (index) {
            case 1:
                removeItems = List.of(
                        Tuple.of(4000003, numOfItems)
                );
                if(!handleTransaction(sm, nAllNum, 0, removeItems, 4003001)) return;
                break;
            case 2:
                removeItems = List.of(
                        Tuple.of(4000018, numOfItems)
                );
                if(!handleTransaction(sm, nAllNum, 0, removeItems, 4003001)) return;
                break;
            case 3:
                removeItems = List.of(
                        Tuple.of(4011001, numOfItems),
                        Tuple.of(4011000, numOfItems)
                );
                if(!handleTransaction(sm, nAllNum, 0, removeItems, 4003000)) return;
                break;
        }
    }

    private static boolean handleTransaction(ScriptManager sm, int numOfItems, int mesos, List<Tuple<Integer, Integer>> removeItems, int itemToGive) {
        if(!sm.hasItems(removeItems) || !sm.canAddMoney(-mesos) || !sm.canAddItem(itemToGive, numOfItems)) {
            sm.sayOk("Hmm... Please make sure you have all the necessary materials, and that you have some free space in your inventory...");
            return false;
        }

        sm.addMoney(-mesos);
        for (var item : removeItems) {
            sm.removeItem(item.getLeft(), item.getRight());
        }
        sm.addItem(itemToGive, numOfItems);
        return true;
    }

    @Script("goDungeon")
    public static void goDungeoun(ScriptManager sm) {
        sm.sayNext("Hey, looks like you want to go a lot further from here. There, however, you'll find monsters on all sides, aggressive, dangerous, and even if you think you're ready, be careful. A long time ago, some brave heroes from our town went to eliminate those who threatened it, but they never returned...");
        if (sm.getLevel() >= 50) {
            if (sm.askYesNo("If you're thinking of entering, I suggest you change your mind. But if you really want to enter... Only those strong enough to stay alive inside will be allowed. I don't want to see anyone else die. Let's see... Hmm...! You look quite strong. Okay, do you wish to enter?")) {
                sm.warp(211040300, "under00");
            } else {
                sm.sayOk("Even though your level is high, it's difficult to get in there. But if you change your mind, talk to me. After all, my duty is to protect this place.");
            }
        } else {
            sm.sayOk("\"If you're thinking of entering, I suggest you change your mind. But if you really want to enter... Only those strong enough to stay alive inside will be allowed. I don't want to see anyone else die. Let's see... Hmmm... you haven't reached level 50 yet. I can't let you in, forget it.");
        }
    }

    @Script("Zakumgo")
    public static void zakumgo(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(211042300);
    }


    @Script("enterRider")
    public static void enterRider(ScriptManager sm) {
        if (sm.hasQuestStarted(21610) && sm.hasItem(4001193, 1)) {
            sm.playPortalSE();
            sm.warpInstance(921110000, "sp", 211050000, 3 * 60);
        } else {
            sm.message("Only attendants of the 2nd Wolf Riding quest may enter this field.");
        }
    }
}
