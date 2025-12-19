package kinoko.script.event;

import kinoko.handler.stage.GachaponHandler;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.quest.QuestRecordType;


public class Gachapon extends ScriptHandler {
    final static int DEF_RETURN_MAP = 100000000;
    final static int GACHAPON_TICKET = 5220000;

    @Script("gachapon10")
    public static void gachapon10(ScriptManager sm) {
        // Gachapon (9100109)
        //   New Leaf City : NLC Town Center (600000000)
        handleGachapon(sm, "New Leaf City", "new_leaf_city");
    }

    @Script("gachapon18")
    public static void gachapon18(ScriptManager sm) {
        // Gachapon (9100117)
        //   Nautilus : Mid Floor - Hallway (120000200)
        handleGachapon(sm, "Nautilus", "nautilus");
    }

    @Script("gachapon1")
    public static void gachapon1(ScriptManager sm) {
        // Gachapon (9100100)
        //   Henesys : Henesys Market (100000100)
        handleGachapon(sm, "Henesys Market", "henesys");
    }

    @Script("gachapon2")
    public static void gachapon2(ScriptManager sm) {
        // Gachapon (9100101)
        //   Ellinia : Ellinia (101000000)
        handleGachapon(sm, "Ellinia", "ellinia");
    }

    @Script("gachapon3")
    public static void gachapon3(ScriptManager sm) {
        // Gachapon (9100102)
        //   Perion : Perion (102000000)
        handleGachapon(sm, "Perion", "perion");
    }

    @Script("gachapon4")
    public static void gachapon4(ScriptManager sm) {
        // Gachapon (9100103)
        //   Kerning City : Kerning City (103000000)
        handleGachapon(sm, "Kerning City", "kerning_city");
    }

    @Script("gachapon5")
    public static void gachapon5(ScriptManager sm) {
        // Gachapon (9100104)
        //   Dungeon : Sleepywood (105040300)
        handleGachapon(sm, "Sleepywood Dungeon", "sleepywood");
    }

    @Script("gachapon6")
    public static void gachapon6(ScriptManager sm) {
        // Gachapon (9100105)
        //   Zipangu : Mushroom Shrine (800000000)
        handleGachapon(sm, "Mushroom Shrine", "mushroom_shrine");
    }

    @Script("gachapon7")
    // Zipangu: Spa (M)
        // Gachapon (9100106)
        //   Zipangu : Spa (M) (809000101)
    public static void gachapon7(ScriptManager sm) {
        handleGachapon(sm, "Zipangu Spa (M)", "zipangu_spa_m");
    }

    @Script("gachapon8")
    // Zipangu: Spa (F)
        // Gachapon (9100107)
        //   Zipangu : Spa (F) (809000201)
    public static void gachapon8(ScriptManager sm) {
        handleGachapon(sm, "Zipangu Spa (F)", "zipangu_spa_f");
    }

    public static void handleGachapon(ScriptManager sm, String location, String gachaName) {
        if (!sm.hasItem(GACHAPON_TICKET, 1)) {
            sm.sayOk("It doesn't seem like you have a Gachapon ticket. Please purchase one and try again.");
            return;
        }

        if (
                sm.getUser().getInventoryManager().getEquipInventory().getRemaining() < 1
                        || sm.getUser().getInventoryManager().getConsumeInventory().getRemaining() < 1
                        || sm.getUser().getInventoryManager().getEtcInventory().getRemaining() < 1
                        || sm.getUser().getInventoryManager().getInstallInventory().getRemaining() < 1
        ) {
            sm.sayOk("Please make room in your EQP, USE, ETC, and SET-UP inventories.");
            return;
        }

        Tuple<Integer, Integer> item = GachaponHandler.rollGachapon(gachaName);
        sm.addItem(item.getLeft(), item.getRight());
        sm.removeItem(GACHAPON_TICKET, 1);
        sm.sayNext("You have obtained #b#t" + item.getLeft() + "##k from " + location + ".\r\nThank you for using our Gachapon services. Please come again!");
    }
}
