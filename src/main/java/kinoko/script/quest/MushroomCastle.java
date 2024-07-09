package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.quest.QuestRecordType;

public final class MushroomCastle extends ScriptHandler {
    @Script("TD_MC_first")
    public static void TD_MC_first(ScriptManager sm) {
        // Singing Mushroom Forest : Ghost Mushroom Forest (100020400)
        //   TD00 (-1094, 214)
        if (sm.getLevel() < 30) {
            sm.message("A strange force is blocking you from entering.");
        } else if (sm.getQRValue(QuestRecordType.MushroomCastleOpening).equals("1")) {
            sm.playPortalSE();
            sm.warp(106020000, "left00"); // Mushroom Castle : Mushroom Forest Field
        } else {
            sm.warp(106020001); // TD_MC_Openning
        }
    }

    @Script("TD_MC_title")
    public static void TD_MC_title(ScriptManager sm) {
        // Mushroom Castle : Mushroom Forest Field (106020000)
        sm.screenEffect("temaD/enter/mushCatle");
    }

    @Script("TD_MC_Openning")
    public static void TD_MC_Openning(ScriptManager sm) {
        // null (106020001)
        sm.setQRValue(QuestRecordType.MushroomCastleOpening, "1");
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction2.img/open/back0");
        sm.reservedEffect("Effect/Direction2.img/open/back1");
        sm.reservedEffect("Effect/Direction2.img/open/light");
        sm.reservedEffect("Effect/Direction2.img/open/pepeKing");
        sm.reservedEffect("Effect/Direction2.img/open/line");
        sm.reservedEffect("Effect/Direction2.img/open/violeta0");
        sm.reservedEffect("Effect/Direction2.img/open/violeta1");
        sm.reservedEffect("Effect/Direction2.img/open/frame");
        sm.reservedEffect("Effect/Direction2.img/open/chat");
        sm.reservedEffect("Effect/Direction2.img/open/out");
        sm.setDirectionMode(false, 13000);
    }
}
