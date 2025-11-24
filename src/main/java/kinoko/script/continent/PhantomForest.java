package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Util;
import kinoko.util.Tuple;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class PhantomForest extends ScriptHandler {
    @Script("Fallen_Woods")
    public static void Fallen_Woods(ScriptManager sm) {
        sm.sayOk("I see you there.  What do you want?");
    }


    @Script("Badge_Bounty")
    public static void Badge_Bounty(ScriptManager sm) {
        if (!sm.hasQuestCompleted(8225)) {
            sm.sayOk("Hey, I'm not a bandit, ok?");
            return;
        }

        List<Integer> itemList = List.of(4032007, 4032006, 4032009, 4032008, 4032007, 4032006, 4032009, 4032008);

        List<List<Tuple<Integer, Integer>>> prizes = List.of(
                List.of(
                        Tuple.of(1002801, 1),
                        Tuple.of(1462052, 1),
                        Tuple.of(1462006, 1),
                        Tuple.of(1462009, 1),
                        Tuple.of(1452012, 1),
                        Tuple.of(1472031, 1),
                        Tuple.of(2044701, 1),
                        Tuple.of(2044501, 1),
                        Tuple.of(3010041, 1),
                        Tuple.of(0, 750000)
                ),
                List.of(
                        Tuple.of(1332077, 1),
                        Tuple.of(1322062, 1),
                        Tuple.of(1302068, 1),
                        Tuple.of(4032016, 1),
                        Tuple.of(2043001, 1),
                        Tuple.of(2043201, 1),
                        Tuple.of(2044401, 1),
                        Tuple.of(2044301, 1),
                        Tuple.of(3010041, 1),
                        Tuple.of(0, 1250000)
                ),
                List.of(
                        Tuple.of(1472072, 1),
                        Tuple.of(1332077, 1),
                        Tuple.of(1402048, 1),
                        Tuple.of(1302068, 1),
                        Tuple.of(4032017, 1),
                        Tuple.of(4032015, 1),
                        Tuple.of(2043023, 1),
                        Tuple.of(2043101, 1),
                        Tuple.of(2043301, 1),
                        Tuple.of(3010040, 1),
                        Tuple.of(0, 2500000)
                ),
                List.of(
                        Tuple.of(1002801, 1),
                        Tuple.of(1382008, 1),
                        Tuple.of(1382006, 1),
                        Tuple.of(4032016, 1),
                        Tuple.of(4032015, 1),
                        Tuple.of(2043701, 1),
                        Tuple.of(2043801, 1),
                        Tuple.of(3010040, 1),
                        Tuple.of(0, 1750000)
                ),
                List.of(Tuple.of(0, 3500000)),
                List.of(Tuple.of(0, 3500000)),
                List.of(Tuple.of(0, 3500000)),
                List.of(Tuple.of(0, 3500000))
        );

        if (sm.getUser().getInventoryManager().getEtcInventory().getRemaining() == 0 || sm.getUser().getInventoryManager().getConsumeInventory().getRemaining() == 0) {
            sm.sayOk("Your use or etc. inventory seems to be full. You need the free spaces to trade with me! Make room, and then find me.");
            return;
        }

        int qnt = 0;
        int requiredItem = 0;
        int lastSelection = 0;

        sm.sayNext("Hey, got a little bit of time? Well, my job is to collect items here and sell them elsewhere, but these days the monsters have become much more hostile so it have been difficult to get good items... What do you think? Do you want to do some business with me?");
        if (!sm.askYesNo("The deal is simple. You get me something I need, I get you something you need. The problem is, I deal with a whole bunch of people, so the items I have to offer may change every time you see me. What do you think? Still want to do it?")) {
            sm.sayOk("Hmmm...it shouldn't be a bad deal for you. Come see me at the right time and you may get a much better item to be offered. Anyway, let me know if you have a change of mind.");
            return;
        }

        final int selection = handleQuestSelection(sm, itemList);
        lastSelection = selection;
        requiredItem = itemList.get(lastSelection);

        if (selection < 4) {
            qnt = 50;
        } else {
            qnt = 25;
        }

        if (!sm.askYesNo("Let's see, you want to trade your " + blue(String.valueOf(qnt) + " " + itemName(requiredItem)) + " with my stuff, right? Before trading make sure you have an empty slot available on your use or etc. inventory. Now, do you want to trade with me?")) {
            sm.sayOk("Hmmm...it shouldn't be a bad deal for you. Come see me at the right time and you may get a much better item to be offered. Anyway, let me know if you have a change of mind.");
            return;
        }

        if (!sm.hasItem(requiredItem, qnt)) {
            sm.sayOk("Hmmm... are you sure you have " + blue(String.valueOf(qnt) + " " + itemName(requiredItem)) + "? If so, then please check and see if your item inventory is full or not.");
            return;
        }

        int randPrizeIndex = Util.getRandom(prizes.get(lastSelection).size());
        Tuple<Integer, Integer> reward = prizes.get(lastSelection).get(randPrizeIndex);
        int prizeItem = reward.getLeft();
        int prizeQty = reward.getRight();

        if (prizeItem == 0) {
            // Meso
            sm.removeItem(requiredItem, qnt);
            sm.addMoney(prizeQty);
            sm.sayOk("For your " + blue(qnt + " " + itemName(requiredItem)) + ", here's " + blue(prizeQty + " mesos") + ". What do you think? Did you like the items I gave you in return? I plan on being here for awhile, so if you gather up more items, I'm always open for a trade...");
            return;
        }

        if (!sm.addItem(prizeItem, prizeQty)) {
            sm.sayOk("Your use and etc. inventory seems to be full. You need the free spaces to trade with me! Make room, and then find me.");
            return;
        }

        sm.removeItem(requiredItem, qnt);

        sm.sayOk("For your " + blue(qnt + " " + itemName(requiredItem)) + ", " + blue(prizeQty + " " + itemName(prizeItem)) + ". What do you think? Did you like the items I gave you in return? I plan on being here for awhile, so if you gather up more items, I'm always open for a trade...");
    }

    private static int handleQuestSelection(ScriptManager sm, List<Integer> itemList) {
        Map<Integer, String> selections = new HashMap<>();
        List<Integer> qnty = List.of(50, 25);
        String prompt = "Ok! First you need to choose the item that you'll trade with. The better the item, the more likely the chance that I'll give you something much nicer in return.\r\n";

        for (int i = 0; i < itemList.size(); i++) {
            selections.put(i, itemImage(itemList.get(i)) + " " + blue(itemName(itemList.get(i))) + " " + qnty.get(i / 4));
        }

        return sm.askMenu(prompt, selections);
    }

    @Script("MoStore")
    public static void MoStore(ScriptManager sm) {
        if (!sm.hasQuestCompleted(8225)) {
            sm.sayOk("Hm, at who do you think you are looking at?");
            return;
        }

        sm.openShopNPC(9201099);
    }

    @Script("Gear_Upgrade")
    public static void Gear_Upgrade(ScriptManager sm) {
        if (!sm.hasQuestCompleted(8225)) {
            sm.sayOk("Step aside, novice, we're doing business here.");
            return;
        }

        final int selection = sm.askMenu("Hey, partner! If you have the right goods, I can turn it into something very nice...", Map.of(
                0, "Weapon Forging",
                1, "Weapon Upgrading"
        ));

        int item = -1;
        List<Integer> materials = Collections.emptyList();
        List<Integer> materialsQty = Collections.emptyList();
        int cost = -1;
        int qty = 1;

        if (selection == 0) {
            // Weapon Forging
            final int wfSelection = sm.askMenu("So, what kind of weapon would you like me to forge?", Map.of(
                    0, itemName(2070018),
                    1, itemName(1382060),
                    2, itemName(1442068),
                    3, itemName(1452060)
            ));

            List<Integer> itemSet = List.of(2070018, 1382060, 1442068, 1452060);
            List<List<Integer>> materialList = List.of(
                    List.of(4032015, 4032016, 4032017, 4021008, 4032005),
                    List.of(4032016, 4032017, 4032004, 4032005, 4032012, 4005001),
                    List.of(4032015, 4032017, 4032004, 4032005, 4032012, 4005000),
                    List.of(4032015, 4032016, 4032004, 4032005, 4032012, 4005002)
            );
            List<List<Integer>> materialQtyList = List.of(
                    List.of(1, 1, 1, 100, 30),
                    List.of(1, 1, 400, 10, 30, 4),
                    List.of(1, 1, 500, 40, 20, 4),
                    List.of(1, 1, 300, 75, 10, 4)
            );

            item = itemSet.get(wfSelection);
            materials = materialList.get(wfSelection);
            materialsQty = materialQtyList.get(wfSelection);
            cost = 70000;
        } else if (selection == 1) {
            // Weapon Upgrading
            final int wuSelection = sm.askMenu("An upgraded weapon? Of course, but note that upgrades won't carry over to the new item...", Map.ofEntries(
                    entry(0, itemName(1472074)),
                    entry(1, itemName(1472073)),
                    entry(2, itemName(1472075)),
                    entry(3, itemName(1332079)),
                    entry(4, itemName(1332078)),
                    entry(5, itemName(1332080)),
                    entry(6, itemName(1462054)),
                    entry(7, itemName(1462053)),
                    entry(8, itemName(1462055)),
                    entry(9, itemName(1402050)),
                    entry(10, itemName(1402049)),
                    entry(11, itemName(1402051))
            ));

            List<Integer> itemSet = List.of(1472074, 1472073, 1472075, 1332079, 1332078, 1332080, 1462054, 1462053, 1462055, 1402050, 1402049, 1402051);
            List<List<Integer>> materialList = List.of(
                    List.of(4032017, 4005001, 4021008),
                    List.of(4032015, 4005002, 4021008),
                    List.of(4032016, 4005000, 4021008),
                    List.of(4032017, 4005001, 4021008),
                    List.of(4032015, 4005002, 4021008),
                    List.of(4032016, 4005000, 4021008),
                    List.of(4032017, 4005001, 4021008),
                    List.of(4032017, 4005001, 4021008),
                    List.of(4032016, 4005000, 4021008),
                    List.of(4032017, 4005001, 4021008),
                    List.of(4032015, 4005002, 4021008),
                    List.of(4032016, 4005000, 4021008)
            );
            List<List<Integer>> materialQtyList = List.of(
                    List.of(1, 10, 20),
                    List.of(1, 10, 30),
                    List.of(1, 5, 20),
                    List.of(1, 10, 20),
                    List.of(1, 10, 30),
                    List.of(1, 5, 20),
                    List.of(1, 10, 20),
                    List.of(1, 10, 30),
                    List.of(1, 5, 20),
                    List.of(1, 10, 20),
                    List.of(1, 10, 30),
                    List.of(1, 5, 20)
            );
            List<Integer> costList = List.of(75000, 50000, 50000, 75000, 50000, 50000, 75000, 50000, 50000, 75000, 50000, 50000);

            item = itemSet.get(wuSelection);
            materials = materialList.get(wuSelection);
            materialsQty = materialQtyList.get(wuSelection);
            cost = costList.get(wuSelection);
        }

        StringBuilder prompt = new StringBuilder("You want to make a " + itemName(item) + "?");
        prompt.append(" In that case, I'm going to need specific items from you in order to make it. Make sure you have room in your inventory, though!");

        for (int i = 0; i < materials.size(); i++) {
            prompt.append("\r\n").append(itemImage(materials.get(i))).append(" ").append(materialsQty.get(i) * qty).append(" ").append(itemName(materials.get(i)));
        }

        if (cost > 0) {
            prompt.append("\r\n").append(itemImage(4031138)).append(" ").append(cost * qty).append(" meso");
        }

        if (sm.askYesNo(prompt.toString())) {
            boolean complete = true;

            if (!sm.canAddItem(item, qty)) {
                sm.sayOk("Check your inventory for a free slot first.");
                return;
            }

            if (!sm.canAddMoney(-(cost * qty))) {
                sm.sayOk("I am afraid you don't have enough to pay me, partner. Please check this out first, ok?");
                return;
            }

            for (int i = 0; complete && i < materials.size(); i++) {
                if (!sm.hasItem(materials.get(i), materialsQty.get(i))) {
                    complete = false;
                }
            }

            if (!complete) {
                sm.sayOk("Hey, I need those items to craft properly, you know?");
                return;
            }

            for (int i = 0; i < materials.size(); i++) {
                sm.removeItem(materials.get(i), materialsQty.get(i));
            }

            if (cost > 0) {
                sm.addMoney(-(cost * qty));
            }

            sm.addItem(item, qty);
            sm.sayNext("All done. If you need anything else... Well, I'm not going anywhere.");
        }
    }
}
