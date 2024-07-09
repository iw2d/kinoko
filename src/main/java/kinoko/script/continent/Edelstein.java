package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

public final class Edelstein extends ScriptHandler {
    @Script("enterDangerHair")
    public static void enterDangerHair(ScriptManager sm) {
        // Black Wing Territory : Edelstein (310000000)
        //   in03 (1090, 587)
        sm.playPortalSE();
        sm.warp(310000003, "out00"); // Edelstein : Edelstein Hair Salon
    }

    @Script("enterSecJobResi")
    public static void enterSecJobResi(ScriptManager sm) {
        // Black Wing Territory : Edelstein (310000000)
        //   in02 (1864, -16)
        sm.playPortalSE();
        sm.warp(310000010, "out00"); // Edelstein : Edelstein Temporary Airport
    }
}
