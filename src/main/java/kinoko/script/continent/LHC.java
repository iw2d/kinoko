package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

public class LHC extends ScriptHandler {
    @Script("lionCastle_enter")
    public static void lionCastle_enter(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(211060010, "west00");
    }

    @Script("gotoNext1")
    public static void gotoNext1(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(211060300, "west00");
    }

    @Script("gotoNext2_1")
    public static void gotoNext2_1(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(211060500, "west00");
    }

    @Script("gotoNext2_2")
    public static void gotoNext2_2(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(211060410, "in00");
    }

    @Script("2ndTowerTop")
    public static void secondTowerTop(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(211060401);
    }

    @Script("3rdTowerTop")
    public static void thirdTowerTop(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(211060601);
    }

    @Script("vanleonItem0")
    public static void vanleonItem0(ScriptManager sm) {
        sm.message("Not implemented yet.");
    }

    @Script("q3162s")
    public static void q3162s(ScriptManager sm) {
        sm.sayNext("Royal Guard Ani comes out every hour, but right now he's not feeling like fighting.");
        sm.forceStartQuest(3162);
    }
}
