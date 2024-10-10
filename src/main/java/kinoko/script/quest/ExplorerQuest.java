package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

public final class ExplorerQuest extends ScriptHandler {
    @Script("enter_archer")
    public static void enter_archer(ScriptManager sm) {
        // Singing Mushroom Forest : Spore Hill (100020000)
        //   in02 (3941, 693)
        if (sm.hasQuestStarted(22518)) {
            sm.warpInstance(910060100, "start", 100020000, 60 * 10);
            return;
        }
        sm.warp(910060000); // Victoria Road : Bowman Training Center
    }
}
