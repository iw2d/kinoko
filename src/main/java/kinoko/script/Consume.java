package kinoko.script;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.mob.MobAppearType;

public final class Consume extends ScriptHandler {
    @Script("consume_2430112")
    public static void consume_2430112(ScriptManager sm) {
        // Miracle Cube Fragment (2430112)
        if (sm.hasItem(2430112, 10)) {
            if (sm.canAddItem(2049400, 1) && sm.removeItem(2430112, 10)) {
                sm.addItem(2049400, 1); // Advanced Potential Scroll
            } else {
                sm.sayNext("Please check if your inventory is full or not.");
            }
        } else if (sm.hasItem(2430112, 5)) {
            if (sm.canAddItem(2049401, 1) && sm.removeItem(2430112, 5)) {
                sm.addItem(2049401, 1); // Potential Scroll
            } else {
                sm.sayNext("Please check if your inventory is full or not.");
            }
        }
    }

    @Script("blackBag")
    public static void blackBag(ScriptManager sm) {
        // Black Bag (2430032)
        sm.spawnMob(9300388, MobAppearType.REGEN, sm.getUser().getX(), sm.getUser().getY(), false);
        sm.removeItem(2430032);
    }

    @Script("consume_2430071")
    public static void consume_2430071(ScriptManager sm) {
        // Opalescent Glass Marble (2430071)
        // Dual Blade Quest 2363 "Time for the Awakening"
        // Gives Mirror of Insight (4032616) when consumed

        if (!sm.hasQuestStarted(2363)) {
            sm.message("You don't have the quest to use this item.");
            return;
        }

        if (sm.hasItem(4032616, 1)) {
            sm.message("You already have the Mirror of Insight.");
            return;
        }

        if (sm.canAddItem(4032616, 1) && sm.removeItem(2430071, 1)) {
            sm.addItem(4032616, 1); // Mirror of Insight
            sm.message("You obtained the Mirror of Insight!");
        } else {
            sm.message("Please check if your inventory is full.");
        }
    }
}
