package kinoko.script.continent;

import kinoko.packet.user.UserLocal;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptDispatcher;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.server.dialog.UIType;

public final class NihalDesert extends ScriptHandler {

    // NPC 2100001 - Khan (Repair Service)
    @Script("make_ariant1")
    public static void make_ariant1(ScriptManager sm) {
        if (!sm.askYesNo("I'll be on repair duty for a while. Do you have something you need fixed?")) {
            sm.sayNext("Good items break easily. You should repair them once in a while.");
            return;
        }
        sm.write(UserLocal.openUI(UIType.REPAIRDURABILITY));
    }

    // NPC 2101003 - Ardin (Sand Bandits hideout dialogue)
    @Script("adin_enter")
    public static void adin_enter(ScriptManager sm) {
        sm.sayOk("Hey, hey! Don't start any trouble with anyone. I want nothing to do with you.");
    }

    // NPC 2101011 - Sejan (General dialogue)
    @Script("cejan")
    public static void cejan(ScriptManager sm) {
        sm.sayNext("...");
    }

    // Portal - Sand Bandits hideout entrance (requires quest 3936 completed)
    @Script("ariant_Agit")
    public static void ariant_Agit(ScriptManager sm) {
        if (sm.hasQuestCompleted(3936)) {
            sm.playPortalSE();
            sm.message("The lock opens from the inside of the door. The door slowly opens.");
            sm.warp(260000201, "sp"); // Shabby House
        } else {
            sm.message("The door is locked.");
        }
    }

    // Portal - Ariant Palace entrance (requires Palace Entry Permit item 4031582)
    @Script("ariant_castle")
    public static void ariant_castle(ScriptManager sm) {
        if (sm.hasItem(4031582)) {
            sm.playPortalSE();
            sm.warp(260000301, "sp"); // Ariant Palace Garden
        } else {
            sm.message("Those who have not received the permit cannot enter the palace.");
        }
    }

    // Portal - Hidden Map (Rocky Hills 260010401) - Opens NPC dialogue instead of warping
    @Script("thief_in1")
    public static void thief_in1(ScriptManager sm) {
        // Trigger NPC 2103008 dialogue using ScriptDispatcher
        ScriptDispatcher.startNpcScript(sm.getUser(), sm.getUser(), "2103008", 2103008);
    }

    // NPC 2103008 - Strange Voice (Hidden Map 260010401)
    @Script("2103008")
    public static void npc2103008(ScriptManager sm) {
        final String magicWord = sm.askText("If you want to open the door, then yell out the magic word...", "", 0, 50);

        if (magicWord.equals("Open Sesame") || magicWord.isEmpty()) {
            sm.warp(260010402, "sp");
        }
    }

    // NPC 2111003 - Humanoid A (Magatia) - Snowfield Rose quest
    @Script("jenu_homun")
    public static void jenu_homun(ScriptManager sm) {
        // Quest 3335 - Snowfield Rose blooming
        if (!sm.hasQuestStarted(3335) || sm.hasItem(4031695)) {
            sm.sayOk("I would want nothing more than to be a human being with a warm, beating heart... That way, I can finally hold her hand the way it's meant to be held. Unfortunately, I can't do that right now...");
            return;
        }

        if (!sm.askAccept("You're back... Are you ready to initiate the full bloom of the Snowfield Rose? You're aware that only the May Mist will allow the rose to bloom, right?")) {
            return;
        }

        sm.sayNext("I will now take you to a place where the incubator for the Snowfield Rose awaits...");

        // Warp to Snowfield Rose instance with 15 minute time limit
        sm.warpInstance(926120300, "sp", 261000000, 900);
    }

    // NPC - D.Roid (Magatia Alcadno)
    @Script("sca_DitRoi")
    public static void sca_DitRoi(ScriptManager sm) {
        sm.sayOk("...");
    }

    // NPC 9300172 - Juliet (Romeo & Juliet PQ)
    @Script("juliet_start")
    public static void juliet_start(ScriptManager sm) {
        sm.sayOk("Oh Romeo, Romeo, wherefore art thou Romeo?");
    }

    // NPC 9300171 - Romeo (Romeo & Juliet PQ)
    @Script("romio_start")
    public static void romio_start(ScriptManager sm) {
        sm.sayOk("But soft! What light through yonder window breaks?");
    }

    // Portal - Magatia Dark Lab entrance (map 261020600)
    @Script("magatia_dark0")
    public static void magatia_dark0(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(261020500, "sp");
    }
}