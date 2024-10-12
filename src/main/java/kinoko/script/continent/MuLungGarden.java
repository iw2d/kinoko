package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

public final class MuLungGarden extends ScriptHandler {
    // NPCS
    @Script("hwang")
    public static void hwang(ScriptManager sm) {
        // Captain Hwang (2092001)
        //   Herb Town : Herb Town (251000000)
        if (sm.hasQuestStarted(22587)) {
            sm.warpInstance(925110001, "out00", 251000000, 60 * 30);
        }
    }

    @Script("Pottery")
    public static void Pottery(ScriptManager sm) {
        // Potter (2092101)
        //   Hidden Street : Pirate Treasure Vault  (925110000)
        if (sm.hasQuestStarted(22408)) {
            if (sm.getUser().getField().getMobPool().getCount() > 0) {
                sm.setPlayerAsSpeaker(true);
                sm.sayOk("#b(I can't rescue #p2092101# with all these pirates here.)");
                return;
            }
            if (!sm.addItem(4032497, 1)) {
                sm.sayNext("Please check if your inventory is full or not.");
                return;
            }
            sm.sayOk("Thank you for rescuing me. Let's hurry and get back to town.");
            sm.warp(251000000, "east00");
        }
    }

    // PORTALS
    @Script("enterPottery")
    public static void enterPottery(ScriptManager sm) {
        // Herb Town : Red-Nose Pirate Den 3 (251010403)
        //   in00 (-867, 77)
        if (sm.hasQuestStarted(22408)) {
            sm.warpInstance(925110000, "out00", 251000000, 60 * 10);
        }
    }
}
