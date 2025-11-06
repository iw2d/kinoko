package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.quest.QuestRecordType;

import java.util.List;
import java.util.Map;

public class Orbis extends ScriptHandler {
    @Script("oldBook1")
    public static void oldBook1(ScriptManager sm) {
        if (sm.hasQuestCompleted(QuestRecordType.AlcasterAndTheDarkCrystal.getQuestId())) {
            final int answer = sm.askMenu("Thanks to you, #b#t4031056##k is safely sealed. As a result, I used up about half of the power I have accumulated over the last 800 years...but can now die in peace. Would you happen to be looking for rare items by any chance? As a sign of appreciation for your hard work, I'll sell some items in my possession to you and ONLY you. Pick out the one you want!", Map.of(
                    0, "#t2050003# (Price : 600 mesos)",
                    1, "#t2050004# (Price : 800 mesos)",
                    2, "#t4006000# (Price : 5,500 mesos)",
                    3, "#t4006001# (Price : 5,500 mesos)"
            ));

            switch (answer) {
                case 0:
                    sellItem(sm,2050003, 300, "The item that cures the state of being sealed and cursed.");
                    break;
                case 1:
                    sellItem(sm, 2050004, 400, "The item that cures everything.");
                    break;
                case 2:
                    sellItem(sm, 4006000, 5000, "The item of magical power used for high level skills.");
                    break;
                case 3:
                    sellItem(sm, 4006001, 5000, "The item of summoning power used for high level skills.");
                    break;
            }
        } else if(sm.getLevel() > 54) {
            sm.sayOk("If you decide to help me, in return I will put items up for sale.");
        } else {
            sm.sayOk("I am Alcaster the Sorcerer, resident of this city for over 300 years, where I have worked on many charms and spells.");
        }
    }

    private static void sellItem(ScriptManager sm, int itemId, int unitPrice, String description) {
        final int nRetNum = sm.askNumber("So the item you need is #b#t" + itemId + "##k, right? That's " + description + " It's not an easy item to get, but for you, I'll sell it for cheap. It'll cost you #b" + unitPrice + " mesos #k per. How many would you like to buy?", 1, 1, 100);
        final int nPrice = unitPrice * nRetNum;
        if(!sm.askYesNo("Do you really want to buy #r" + nRetNum + " #t" + itemId + "#(s)#k? It'll cost you " + unitPrice + " mesos per #t" + itemId + "#, which is #r" + nPrice + "#k mesos in total.")) {
            sm.sayOk("I understand. You see, I have many different items here. Take a look. I am selling these items just for you. So I won't rob you at all.");
            return;
        }

        if(!sm.canAddMoney(-nPrice) || !sm.canAddItem(itemId, nRetNum)) {
            sm.sayOk("Are you sure you have enough mesos? Please check if your use or etc. inventory is full and that you have at least #r" + nPrice + "#k mesos.");
            return;
        }

        sm.addItem(itemId, nRetNum);
        sm.sayOk("Thank you. If some other day you are in need of items, stop by. I may have gotten old with time, but I can still make magic items easily.");
    }

    @Script("oldBook2")
    public static void oldBook2(ScriptManager sm) {
        // Lisa (2012012)
        //   Orbis : Orbis (200000000)
        if (!sm.hasQuestStarted(QuestRecordType.WheresHella.getQuestId())) {
            sm.sayOk("Are you looking for #bHella#k? Technically she lives here, but you won't be able to find her these days. A few months ago, she left town suddenly and never came back. It won't do much good to stop by her house, but at least the housekeeper should be there. How about talking to her?");
        } else if(!sm.hasQuestStarted(QuestRecordType.TheSmallGraveThatsHidden.getQuestId())) {
            sm.sayOk("Where has #bHella#k gone... what? You know that she's alright? Hmmm... I don't know if I should trust a stranger's word, but if it's true, that's great. Of course you already warned Jade, right? Out of everyone, he is the most worried about her.");
        } else {
            sm.sayOk("Monsters have been a lot more evil and cruel lately. And what if they come here?? I hope that never happens, right? Right?");
        }
    }


    @Script("enterNepenthes")
    public static void enterNepenthes(ScriptManager sm) {
        if (sm.hasQuestStarted(21739)) {
            sm.playPortalSE();
            sm.warpInstance(List.of(920030000, 920030001), "sp", 200060000, 60 * 15);

        } else {
            sm.playPortalSE();
            sm.warp(200060001);
        }
    }

    @Script("sealGarden")
    public static void sealGarden(ScriptManager sm) {
        if (sm.hasQuestStarted(21739)) {
            sm.spawnMob(9300348, MobAppearType.NORMAL, 591, -34, false);
        }
    }
}
