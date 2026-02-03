package kinoko.script.boss;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.mob.MobAppearType;

public class Scarlion extends ScriptHandler {
    final static int TARGA_BOSS = 9420542;
    final static int SCARLION_BOSS = 9420546;
    @Script("MalaysiaBoss_GL")
    public static void MalaysiaBossGL(ScriptManager sm) {
        // Aldol (9270047)
        //   Malaysia : Entrance to the Spooky World (551030100)
        if(sm.askYesNo("Do you want to go to the Spooky World Entrance?")) {
            sm.partyWarpInstance(551030200, "sp", 551030100, 60 * 60);
        }
    }

    @Script("myboss0")
    public static void myboss0(ScriptManager sm) {
        // myboss0 (5511000)
        //   Malaysia : Spooky World (551030200)
        sm.broadcastMessage("Beware! The furious Targa has shown himself!");
        sm.spawnMob(TARGA_BOSS, MobAppearType.NORMAL, -527, 637, true);
    }

    @Script("myboss1")
    public static void myboss1(ScriptManager sm) {
        // myboss1 (5511001)
        //   Malaysia : Spooky World (551030200)
        sm.broadcastMessage("Beware! The furious Scarlion has shown himself!");
        sm.spawnMob(SCARLION_BOSS, MobAppearType.NORMAL, -238, 636, true);
    }

    @Script("Malay_Warp")
    public static void malayWarp(ScriptManager sm) {
        // Aldol (9201134)
        //   Malaysia : Spooky World (551030200)
        if(sm.askYesNo("Do you want to go out?")) {
            sm.getField().reset();
            sm.partyWarp(551030100, "sp");
        }
    }
}
